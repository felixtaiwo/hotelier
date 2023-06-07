package com.hotelier.resource;


import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.ReservationDto;
import com.hotelier.model.dto.RoomCategoryDto;
import com.hotelier.model.dto.RoomDto;
import com.hotelier.model.dto.TransactionDto;
import com.hotelier.service.AdminService;
import com.hotelier.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Constants;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.BindException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;


@RestController @RequiredArgsConstructor
@RequestMapping("api/v1/room") @Transactional(rollbackFor = {Hotelier.class, NullPointerException.class, Exception.class, BindException.class, Constants.ConstantException.class})
@Tag(name="Room", description = "room endpoints")
public class RoomResource {
    Logger log = Logger.getLogger(this.getClass().getName());
    private final RoomService roomService;
    private final AdminService adminService;

    @GetMapping ("category")
    public List<RoomCategoryDto> getRoomCategories(){
        return roomService.getRoomCategories();
    }
    @GetMapping ("category/property/{id}")
    public List<RoomCategoryDto> getRoomCategoriesByProperty(@PathVariable("id") Long propertyId){
        return roomService.getRoomCategoriesByProperty(propertyId);
    }
    @GetMapping("category/{id}")
    public List<RoomDto> getRoomByCategory(@PathVariable Long id){
        return roomService.getRoomByCategory(id);
    }
    @GetMapping("{id}/lock")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void lockRoom(@PathVariable Long id,
                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInTime, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutTime,
                         @RequestParam(name = "guestId", required = false) Long guestId
                         ){
         roomService.lockRoom(id, checkInTime, checkOutTime, guestId);
    }

    @PostMapping("reservation")
    @Operation(summary = "Make reservation (N.B: lock a room first before making reservation")
    public ReservationDto makeReservation(@Valid @RequestBody String purposeOfStay, @RequestParam(name = "guestId", required = false) Long guestId){
        return roomService.makeReservation(purposeOfStay, guestId);
    }
    @PutMapping("reservation/{id}")
    @Operation(summary = "Assent your reservation")
    public void assentReservation(@PathVariable Long id){
        roomService.assentReservation(id);
    }
    @GetMapping("reservation/{id}")
    public ReservationDto getReservation(@PathVariable Long id){
        return ReservationDto.fromReservation(roomService.getReservation(id));
    }
    @GetMapping("reservation/{id}/transaction")
    public TransactionDto getReservationTx(@PathVariable Long id){
        return roomService.getReservationTx(id);
    }
    @DeleteMapping("reservation/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteReservation(@PathVariable Long id){
        roomService.cancelReservation(id);
    }

    @PostMapping
    public RoomDto addRoom(@RequestBody @Valid RoomDto roomDto){
        return adminService.addRoom(roomDto);
    }
    @GetMapping
    public List<RoomDto> getRoomList(@RequestParam(required = false) Long propertyId){
        return adminService.getRoomList(propertyId);
    }
    @GetMapping("reservation/room/{roomNumber}")
    public List<ReservationDto> getReservationList(@PathVariable String roomNumber, @RequestParam(required = false) ReservationDto.ReservationStatus status){
        return adminService.getRoomListByRoomNumber(roomNumber, status);
    }
    @GetMapping("search/category")
    public List<RoomCategoryDto> filterCategory(@RequestParam double minPrice,
                                                 @RequestParam double maxPrice,
                                                 @RequestParam(required = false) Long properTyId,
                                                 @RequestParam(required = false, defaultValue = "") String name,
                                                 @RequestParam String[] features
                                                 ){
        return adminService.filterCategory(minPrice, maxPrice, properTyId, features, name);
    }

    @GetMapping("search/room")
    public List<RoomDto> filterRoom(@RequestParam double minPrice,
                                                @RequestParam double maxPrice,
                                                @RequestParam(required = false) Long properTyId,
                                                @RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam String[] features
    ){
        return adminService.filterRoom(minPrice, maxPrice, properTyId, features, name);
    }

}
