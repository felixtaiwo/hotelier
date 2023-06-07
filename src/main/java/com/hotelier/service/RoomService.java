package com.hotelier.service;

import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.*;
import com.hotelier.model.entity.*;
import com.hotelier.model.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomCategoryRepo roomCategoryRepo;
    private final RoomRepo roomRepo;
    private final ReservationRepo reservationRepo;
    private final InvoiceRepo invoiceRepo;
    private final GuestService guestService;
    private final FileService fileService;
    private final TransactionRepo transactionRepo;

    public List<RoomCategoryDto> getRoomCategories() {
        return roomCategoryRepo.findAll().stream()
                .map(roomCat->RoomCategoryDto.fromRoomCategory(roomCat,fileService)).collect(Collectors.toList());
    }
    public List<RoomDto> getRoomByCategory(Long categoryId){
        if(!isCategoryExistent(categoryId))
            throw new Hotelier(HttpStatus.NOT_FOUND, String.format("Room category with id %s does not exist", categoryId));
        RoomCategory roomCategory = new RoomCategory(categoryId);
        return roomRepo.findByCategory(roomCategory).stream()
                 .map(room -> RoomDto.fromRoom(room,fileService)).collect(Collectors.toList());
    }
    private boolean isCategoryExistent(Long id){
       return roomCategoryRepo.existsById(id);
    }

    public Room getRoomById(Long id){
        return roomRepo.findById(id).orElseThrow(
                ()-> new Hotelier(HttpStatus.NOT_FOUND, String.format("Room with id %s does not exist", id))
        );
    }

    public void lockRoom(Long roomId, LocalDate ci, LocalDate co, Long guestId) {
        Guest guest;
        if(guestId != null){
            guest = guestService.getGuest(guestId);
        } else{
            guest = defaultGuest();
        }
        Room room = getRoomById(roomId);
        List<RoomDto> availableRooms = getAvailableRooms(ci,co, room.getCategory().getProperty().getId());
        if(availableRooms.stream().noneMatch(r-> Objects.equals(r.getId(), roomId))){
            throw new Hotelier(HttpStatus.GONE, String.format("Room with id %s is not available", roomId));
        }
        Reservation reservation = reservationRepo.findByStatusAndGuest(ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.LOCKED), guest);
        if (reservation == null) {
            reservation = new Reservation();
            reservation.setRoom(room);
            reservation.setGuest(guest);
            reservation.setCheckInDate(ci);
            reservation.setCheckOutDate(co);
        } else {
            reservation.setRoom(room);
        }
        reservationRepo.save(reservation);
    }

    private Guest defaultGuest() {
        Guest loggedInGuest = guestService.getLoggedInGuest();
        if(loggedInGuest == null)
            throw new Hotelier(HttpStatus.FORBIDDEN, "User is not a registered as guest");
        return loggedInGuest;
    }

    public ReservationDto makeReservation(@Valid @NotNull @NotBlank @Size(max = 224) String purposeOfStay, Long guestId){
        Guest guest;
        if(guestId != null){
            guest = guestService.getGuest(guestId);
        } else{
            guest = defaultGuest();
        }
        Reservation savedReservation = reservationRepo.findByStatusAndGuest(ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.LOCKED), guest);
        if(savedReservation == null){
            throw new Hotelier(HttpStatus.NOT_FOUND, "Reservation failed");
        }
        savedReservation.setPurposeOfStay(purposeOfStay);
        savedReservation.setStatus(ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.OPEN));
        reservationRepo.save(savedReservation);
        List<Invoice> invoices = Collections.singletonList(calculateReservationCost(savedReservation));
        invoiceRepo.saveAll(invoices);
        return ReservationDto.fromReservation(savedReservation);
    }

    private Invoice calculateReservationCost(Reservation reservation){
        Invoice invoice = new Invoice();
        invoice.setReservation(reservation);
        invoice.setDescription("Reservation for room: "+ reservation.getRoom().getNumber());
        invoice.setQuantity(1);
        invoice.setUnitPrice(reservation.getRoom().getCategory().getPrice() * (ChronoUnit.DAYS.between(reservation.getCheckInDate(),reservation.getCheckOutDate())));
        invoice.setPrice(invoice.getQuantity() * invoice.getUnitPrice());
        return invoice;
    }

    public void assentReservation(Long id){
        Reservation reservation = getReservation(id);
        reservation.setStatus(ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.CONFIRMED));
        reservationRepo.save(reservation);
    }

    public Reservation getReservation(Long id){
        return reservationRepo.findById(id).orElseThrow(
                ()-> new Hotelier(HttpStatus.NOT_FOUND, "Reservation does not exist")
        );
    }


    public void cancelReservation(Long reservationId){
        Reservation reservation = getReservation(reservationId);
        if(reservation.getGuest() == defaultGuest()){
            reservation.setStatus(ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.DECLINED));
            reservationRepo.save(reservation);
        }
    }
    public void checkInReservation(Long reservationId){
        Reservation reservation = getReservation(reservationId);
        if(reservation.getStatus().equals(ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.CONFIRMED))){
            reservation.setStatus(ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.CHECKED_IN));
            reservationRepo.save(reservation);
        }else {
            throw new Hotelier(HttpStatus.BAD_REQUEST, "Reservation not in the right state for checkin");
        }
    }
    public void manuallyCheckOutReservation(Long reservationId){
        Reservation reservation = getReservation(reservationId);
        checkoutReservation(reservation, ReservationDto.ReservationStatus.CANCELLED);
    }
//    @Scheduled(fixedDelay = 1500000, initialDelay = 0)
    public void checkOutReservation(){
        List<Reservation> rooms = reservationRepo.findByCheckOutDate(LocalDate.now());
        rooms.forEach(r->{
            if(r.getCheckOutDate().atTime(r.getRoom().getCategory().getProperty().getCheckOutTime()).isBefore(LocalDateTime.now()) && !Objects.equals(r.getStatus(), ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.CHECKED_OUT))){
                checkoutReservation(r, ReservationDto.ReservationStatus.CHECKED_OUT);
            }
        });
    }

    void checkoutReservation(@NotNull Reservation reservation, ReservationDto.ReservationStatus status){
        reservation.setStatus(ReservationDto.ReservationStatus.getStatus(status));
        reservationRepo.save(reservation);
    }

    @Scheduled(fixedDelay = 300000, initialDelay = 0)
    public void clearLockedRoom(){
        List<Reservation> rooms = reservationRepo.findByStatus(ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.LOCKED));
        reservationRepo.deleteAll(rooms.stream().filter(r->r.getCreateDate().isBefore(r.getCurrentDateTime().minusMinutes(5))).collect(Collectors.toList()));
    }
    @Scheduled(fixedDelay = 300000, initialDelay = 0)
    public void clearUnAssentedReservations(){
        List<Reservation> rooms = reservationRepo.findByStatus(ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.OPEN));
        reservationRepo.deleteAll(rooms.stream().filter(r->r.getLastModified().isBefore(r.getCurrentDateTime().minusMinutes(15))).collect(Collectors.toList()));
    }

    public List<RoomDto> getAvailableRooms(LocalDate checkInTime, LocalDate checkOutTime, Long propertyId) {
        Property property = new Property();
        property.setId(propertyId);
        List<Room> rooms = new ArrayList<>();
        List<RoomCategory> categoryList = roomCategoryRepo.findByProperty(property);
        if(categoryList == null || categoryList.isEmpty()){
            throw new Hotelier(HttpStatus.NOT_FOUND, "No Room found for property");
        }
        for(RoomCategory category: categoryList) {
            List<Room> byCategory = roomRepo.findByCategory(category);
            if(byCategory!=null && !byCategory.isEmpty()){
                rooms.addAll(byCategory);
            }
        }
        List<Room> availableRooms = new ArrayList<>();
        for(Room room: rooms){
            if(!reservationRepo.isRoomOccupiedWithinDateRange(room, checkInTime, checkOutTime)){
                availableRooms.add(room);
            }
        }

        return availableRooms.stream().map(r->RoomDto.fromRoom(r, fileService)).collect(Collectors.toList());
    }

    public List<InvoiceDto.InvoiceItemDto> getReservationInvoice(Long reservationId){
        return InvoiceDto.getInvoice(invoiceRepo.findByReservation(reservationRepo.findById(reservationId)
                .orElseThrow(()->new Hotelier(HttpStatus.NOT_FOUND, "Reservation does not exist"))
        ));
    }

    public List<RoomCategoryDto> getRoomCategoriesByProperty(Long propertyId) {
        Property property = new Property();
        property.setId(propertyId);
        List<RoomCategory> byProperty = roomCategoryRepo.findByProperty(property);
        if (byProperty == null || byProperty.isEmpty()){
            throw new Hotelier(HttpStatus.NOT_FOUND, "Property has no room category to it");
        }
        return byProperty.stream().map(r->RoomCategoryDto.fromRoomCategory(r,fileService)).collect(Collectors.toList());
    }

    public TransactionDto checkOutReservation(Long id) {
        double amount = 0;
        Reservation reservation = reservationRepo.findById(id)
                .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, "Reservation does not exist"));
        if(Objects.equals(reservation.getStatus(), ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.CHECKED_OUT))){
            throw new Hotelier(HttpStatus.BAD_REQUEST, "Reservation is checked-out already");
        }
        if(!Objects.equals(reservation.getStatus(), ReservationDto.ReservationStatus.getStatus(ReservationDto.ReservationStatus.CHECKED_IN))){
            throw new Hotelier(HttpStatus.BAD_REQUEST, "Reservation is not in check-in status");
        }

        checkoutReservation(reservation, ReservationDto.ReservationStatus.CHECKED_OUT);
        List<Invoice> byReservation = invoiceRepo.findByReservation(reservation);
        Transaction transaction = new Transaction();
        transaction.setReferenceId(transaction.generateRef(reservation.getGuest().getId()));
        for (Invoice i : byReservation) {
            amount += i.getPrice();
        }
        transaction.setAmount(amount);
        transaction.setFee(200);
        transaction.setTotal(transaction.getFee() + transaction.getAmount());
        Transaction save = transactionRepo.save(transaction);
        for (Invoice i : byReservation) {
            i.setTransaction(save);
            invoiceRepo.save(i);
        }
        save.setInvoice(byReservation);
        return TransactionDto.fromTransaction(save);
    }

    public TransactionDto getReservationTx(Long id) {
        List<Invoice> byReservation = invoiceRepo.findByReservation(reservationRepo.findById(id)
                .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, "Reservation does not exist")));
       if(byReservation == null || byReservation.isEmpty()){
           throw new Hotelier(HttpStatus.NO_CONTENT, "No transaction found for reservation");
       }
       Transaction transaction = byReservation.get(0).getTransaction();
       double amount = 0;
       for(Invoice i: byReservation){
           amount+=i.getPrice();
       }
       if(transaction == null){
           transaction  = new Transaction();
           transaction.setInvoice(byReservation);
           transaction.setAmount(amount);
           transaction.setFee(200);
           transaction.setStatus(-2);
       }
        transaction.setTotal(transaction.getFee() + transaction.getAmount());
        return TransactionDto.fromTransaction(transaction);
    }
}
