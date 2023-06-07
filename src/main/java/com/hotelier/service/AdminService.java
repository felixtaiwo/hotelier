package com.hotelier.service;

import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.*;
import com.hotelier.model.entity.*;
import com.hotelier.model.enums.SystemRoles;
import com.hotelier.security.JwtUtils;
import com.hotelier.model.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CompanyRepo companyRepo;
    private final PropertyRepo propertyRepo;
    private final FeatureRepo featureRepo;
    private final RoomCategoryRepo roomCategoryRepo;
    private final UserRepo userRepo;
    private final PersonnelRepo personnelRepo;
    private final RoleRepo roleRepo;
    private final JwtUtils jwtUtils;
    private final RoomService roomService;
    private final GuestRepo guestRepo;
    private final RoomRepo roomRepo;
    private final ReservationRepo reservationRepo;
    private final FileService fileService;
    private final TransactionRepo transactionRepo;

    public CompanyDto addOrEditCompany(CompanyDto companyDto) {
        Company company = companyRepo.save(CompanyDto.toCompany(companyDto));
        return CompanyDto.fromCompany(company);
    }

    public List<CompanyDto> getAllCompany() {
        return companyRepo.findAll().stream().map(CompanyDto::fromCompany).collect(Collectors.toList());
    }

    public void addOrEditProperty(PropertyDto propertyDto) {
        propertyRepo.save(PropertyDto.toProperty(propertyDto));
    }

    public List<PropertyDto> propertyDtoList() {
        Personnel loggedInPersonnel = jwtUtils.getLoggedInPersonnel();
        User user = jwtUtils.userContext();

        if (loggedInPersonnel == null) {
            if (user.getRoles().stream().anyMatch(r -> r.getTitle() == SystemRoles.SUPER_ADMIN)) {
                return propertyRepo.findAll().stream().map(PropertyDto::fromProperty).collect(Collectors.toList());
            } else
                throw new Hotelier(HttpStatus.FORBIDDEN, "user is neither a nor a super user");
        }


        if (loggedInPersonnel.getPropertyList() == null) {
            throw new Hotelier(HttpStatus.BAD_REQUEST, "personnel is not a assigned to any property");
        }
        return loggedInPersonnel.getPropertyList().stream().map(PropertyDto::fromProperty).collect(Collectors.toList());
    }

    public List<FeatureDto> addFeatures(FeatureDto[] features) {
        List<Feature> savedFeatures = featureRepo.saveAll(Arrays.stream(features).map(FeatureDto::toFeature).collect(Collectors.toList()));
        return savedFeatures.stream().map(FeatureDto::fromFeature).collect(Collectors.toList());
    }


    public RoomCategoryDto addOrEditRoomCategory(RoomCategoryDto roomCategoryDto) {
        RoomCategory roomCategory;
        if (roomCategoryDto.getId() == null) {
            roomCategory = new RoomCategory();
        } else {
            roomCategory = roomCategoryRepo.findById(roomCategoryDto.getId())
                    .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, "Room category does not exist"));
        }
        roomCategory.setName(roomCategoryDto.getTitle());
        roomCategory.setGuestSize(roomCategoryDto.getGuestSize());
        roomCategory.setProperty(PropertyDto.toProperty(roomCategoryDto.getProperty()));
        roomCategory.setPrice(roomCategoryDto.getPrice());
        List<Feature> features = Arrays.stream(roomCategoryDto.getFeatures()).map(f -> {
                    Optional<Feature> optionalFeature = featureRepo.findById(f.getId() == null ? 0 : f.getId());
                    return optionalFeature.orElseGet(() ->
                            featureRepo.save(FeatureDto.toFeature(f))
                    );
                }
        ).collect(Collectors.toList());
        roomCategory.setFeatures(features);
        roomCategoryRepo.save(roomCategory);
        return RoomCategoryDto.fromRoomCategory(roomCategory, fileService);
    }

    public void addPersonnel(PersonnelDto personnelDto) {
        Personnel personnel = new Personnel();
        personnel.setDesignation(personnelDto.getDesignation());
        personnel.setUser(userRepo.findById(personnelDto.getUser().getId())
                .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, String.format("user with id %s does not exist", personnelDto.getUser().getId()))));
        if (personnelRepo.findByUser(personnel.getUser()) != null) {
            throw new Hotelier(HttpStatus.BAD_REQUEST, "User is already a personnel");
        }
        List<Property> propertyList = new ArrayList<>();
        for (PropertyDto l : personnelDto.getProperty()) {
            Property property = propertyRepo.findById(l.getId())
                    .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, "Property with id %s does not exist"));
            propertyList.add(property);
        }
        personnel.setPropertyList(propertyList.isEmpty() ? null : propertyList);
        personnelRepo.save(personnel);


        User user = personnel.getUser();
        Role guestRole = roleRepo.findByTitle(SystemRoles.ADMIN);
        getUserRole(user, guestRole, userRepo);
    }

    static void getUserRole(User user, Role guestRole, UserRepo userRepo) {
        Set<Role> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
        }
        if (roles.contains(guestRole)) {
            return;
        } else {
            roles.add(guestRole);
        }
        userRepo.save(user);
    }


    public List<RoomDto> availableRoomsForToday(long propertyId) {
        return roomService.getAvailableRooms(LocalDate.now(), LocalDate.now(), propertyId);
    }

    public List<RoomDto> availableRooms(LocalDate startTime, LocalDate endTime, Long propertyId) {
        return roomService.getAvailableRooms(startTime, endTime, propertyId);
    }

    public List<GuestDto> searchGuest(String searchText) {
        List<User> users = userRepo.searchByEmailPhoneAndFullName(searchText);
        if(users == null || users.isEmpty()){
            throw new Hotelier(HttpStatus.NOT_FOUND, "No guest found");
        }
        List<User> userList = users.stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<Guest> guests = new ArrayList<>();
        for(User user: userList){
            Guest byUser = guestRepo.findByUser(user);
            if(byUser!=null){
                guests.add(byUser);
            }
        }
        return guests.stream().map(g->GuestDto.fromGuest(g, fileService)).collect(Collectors.toList());
    }

    public PersonnelDto getPersonnel() {
        Personnel personnel = jwtUtils.getLoggedInPersonnel();
        if (personnel == null) {
            throw new Hotelier(HttpStatus.BAD_REQUEST, "Currently logged in user is not a personnel");
        }
        return PersonnelDto.fromPersonnel(personnel, true);
    }


    @Transactional(dontRollbackOn = Exception.class)
    public RoomDto addRoom(RoomDto roomDto) {
        Room room;
        if (roomDto.getId() == null) {
            room = new Room();
        } else {
            room = roomRepo.findById(roomDto.getId())
                    .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, "Room does not exist"));
        }
        room.setNumber(roomDto.getRoomNumber());
        RoomCategory category = new RoomCategory();
        if (validateEntry(roomDto.getCategory().getId())) {
            category.setId(roomDto.getCategory().getId());
            room.setCategory(category);
            room = roomRepo.save(room);
        } else {
            throw new Hotelier(HttpStatus.BAD_REQUEST, "Invalid Payload");
        }
        return RoomDto.fromRoom(room, fileService);
    }

    boolean validateEntry(Long roomCat) {
        try {
            return roomCategoryRepo.existsById(roomCat);
        } catch (Exception e) {
            return false;
        }
    }

    public List<ReservationDto> getReservations(Long propertyId, ReservationDto.ReservationStatus status, LocalDate date) {
        List<Reservation> reservations = reservations(propertyId);
        if (status == null && date == null) {
            return reservations.stream().map(ReservationDto::fromReservation).collect(Collectors.toList());
        }
        if (status != null && date != null) {
            return reservations.stream().filter(
                    r -> Objects.equals(r.getStatus(), ReservationDto.ReservationStatus.getStatus(status)) && r.getCheckInDate().isEqual(date)
            ).map(ReservationDto::fromReservation).collect(Collectors.toList());
        }
        if (status != null) {
            return reservations.stream().filter(
                    r -> Objects.equals(r.getStatus(), ReservationDto.ReservationStatus.getStatus(status))
            ).map(ReservationDto::fromReservation).collect(Collectors.toList());
        } else
            return reservations.stream().filter(
                    r -> r.getCheckInDate().isEqual(date)
            ).map(ReservationDto::fromReservation).collect(Collectors.toList());
    }

    public HashMap<String, Object> getStat(Long propertyId, LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new Hotelier(HttpStatus.BAD_REQUEST, "Start date cannot be after end date");
        }
        HashMap<String, Object> finalOutput = new HashMap<>();
        for (LocalDate startDate = start; startDate.isBefore(end); startDate = startDate.plusDays(1L)) {
            HashMap<String, Object> res = new HashMap<>();
            for (ReservationDto.ReservationStatus status : ReservationDto.ReservationStatus.values()) {
                List<ReservationDto> reservations = getReservations(propertyId, status, startDate);
                res.put(String.valueOf(status), reservations.size());
            }
            finalOutput.put(startDate.toString(), res);
        }

        return finalOutput;
    }



    public List<PersonnelDto> getAllPersonnel(Long propertyId) {
        return propertyRepo.findById(propertyId)
                .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, "Property does not exits"))
                .getPersonnelList().stream().map(p -> PersonnelDto.fromPersonnel(p, false)).collect(Collectors.toList());
    }




    public List<FeatureDto> getFeatures() {
        List<Feature> all = featureRepo.findAll();
        if (all.isEmpty()) {
            return new ArrayList<>();
        }
        return all.stream().map(FeatureDto::fromFeature).collect(Collectors.toList());
    }

    public List<ReservationDto> getReservations(Long propertyId) {
        return reservations(propertyId).stream().map(ReservationDto::fromReservation).collect(Collectors.toList());
    }

    private List<Reservation> reservations(Long propId) {
        Property p = new Property();
        p.setId(propId);
        List<RoomCategory> roomCategories = roomCategoryRepo.findByProperty(p);
        List<Room> rooms = new ArrayList<>();
        List<Reservation> reservations = new ArrayList<>();
        for (RoomCategory roomCategory : roomCategories) {
            List<Room> byCategory = roomRepo.findByCategory(roomCategory);
            rooms.addAll(byCategory);
        }
        for (Room room : rooms) {
            List<Reservation> byRoom = reservationRepo.findByRoom(room);
            reservations.addAll(byRoom);
        }
        return reservations;
    }

    public List<ReservationDto> findReservationByGuest(Long guestId, ReservationDto.ReservationStatus status) {
        Guest guest = guestRepo.findById(guestId)
                .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, "Guest does not exist"));
        List<Reservation> byGuest = reservationRepo.findByGuest(guest);
        if (byGuest == null || byGuest.isEmpty()) {
            throw new Hotelier(HttpStatus.NO_CONTENT, "No reservation found for guest");
        }
        if(status == null){
            return byGuest.stream().map(ReservationDto::fromReservation).collect(Collectors.toList());
        }
        else return streamlineReservationByStatus(byGuest, status);

    }
    private List<ReservationDto> streamlineReservationByStatus(List<Reservation> reservation, ReservationDto.ReservationStatus status){
        return reservation.stream().filter(r-> ReservationDto.ReservationStatus.getStatus(status).equals(r.getStatus())).map(ReservationDto::fromReservation).collect(Collectors.toList());
    }

    public void transpaymentByCash(Long transactionId) {
        Transaction transaction = transactionRepo.findById(transactionId)
                .orElseThrow((() -> new Hotelier(HttpStatus.NOT_FOUND, "Transaction not found")));

        User user = jwtUtils.userContext();

        transaction.setChannel(TransactionDto.PaymentOption.CASH);
        transaction.setPaymentId("personnel - " + user.getEmailAddress());
        transaction.setStatus(1);
        transactionRepo.save(transaction);


    }

    public List<RoomDto> getRoomList(Long propertyId) {
        List<Room> rooms = new ArrayList<>();
        if (propertyId == null) {
            rooms = roomRepo.findAll();
        } else {
            Property property = new Property();
            property.setId(propertyId);
            List<RoomCategory> categoryList = roomCategoryRepo.findByProperty(property);
            if (categoryList == null || categoryList.isEmpty()) {
                throw new Hotelier(HttpStatus.NOT_FOUND, "No room associated with property");
            }
            for(RoomCategory category:categoryList){
                List<Room> byCategory = roomRepo.findByCategory(category);
                if(byCategory != null && !byCategory.isEmpty()){
                    rooms.addAll(byCategory);
                }
            }
        }
        return rooms.stream().map(r->RoomDto.fromRoom(r,fileService)).collect(Collectors.toList());


    }

    public List<ReservationDto> getRoomListByRoomNumber(String roomNumber, ReservationDto.ReservationStatus status) {
        List<Room> rooms = roomRepo.findByNumber(roomNumber);
        List<Room> roomList = rooms.stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<Reservation> reservations = new ArrayList<>();
        if(rooms.isEmpty()){
            throw new Hotelier(HttpStatus.NOT_FOUND, "Room does not exist");
        }
        for(Room room: roomList){
            List<Reservation> byRoom = reservationRepo.findByRoom(room);
            reservations.addAll(byRoom);
        }

        if(status == null){
            return reservations.stream().map(ReservationDto::fromReservation).collect(Collectors.toList());
        }
        return reservations.stream().filter(r -> ReservationDto.ReservationStatus.getStatus(status).equals(r.getStatus()))
                .map(ReservationDto::fromReservation).collect(Collectors.toList());

    }

    public List<RoomCategoryDto> filterCategory(double minPrice, double maxPrice, Long properTyId, String[] features, String name) {
        List<RoomCategory> roomCategories = new ArrayList<>();
        if (properTyId == null) {
            roomCategories = roomCategoryRepo.filterCategory(minPrice, maxPrice, name);
        } else {
            Property property = propertyRepo.findById(properTyId)
                    .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, "Property does not exist"));
            roomCategories = roomCategoryRepo.filterCategory(minPrice, maxPrice, property, name);
        }

        if (roomCategories == null || roomCategories.isEmpty()) {
            throw new Hotelier(HttpStatus.NO_CONTENT, "No room category found!");
        }
        return roomCategories
                .stream().filter(roomCategory ->
                        new HashSet<>(roomCategory.getFeatures()
                                .stream().map(Feature::getName).collect(Collectors.toList()))
                                .containsAll(List.of(features))

                ).collect(Collectors.toList())
                .stream().map(r -> RoomCategoryDto.fromRoomCategory(r, fileService))
                .collect(Collectors.toList());
    }

    public List<RoomDto> filterRoom(double minPrice, double maxPrice, Long properTyId, String[] features, String name) {
        List<RoomCategoryDto> roomCategoryDtos = filterCategory(minPrice, maxPrice, properTyId, features, name);
        List<RoomCategory> collect = roomCategoryDtos.stream().map(RoomCategoryDto::toRoomCategory).collect(Collectors.toList());
        List<Room> rooms = new ArrayList<>();
        for (RoomCategory roomCategory : collect) {
            List<Room> byCategory = roomRepo.findByCategory(roomCategory);
            if (byCategory != null && !byCategory.isEmpty()) {
                rooms.addAll(byCategory);
            }
        }
        return rooms.stream().map(r -> RoomDto.fromRoom(r, fileService)).collect(Collectors.toList());
    }

    public TransactionDto getTransaction(Long transactionId) {
        Transaction transaction = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new Hotelier(HttpStatus.NOT_FOUND, "Transaction does not exist"));

        return TransactionDto.fromTransaction(transaction);
    }

}
