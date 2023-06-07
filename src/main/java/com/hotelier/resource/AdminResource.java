package com.hotelier.resource;


import com.hotelier.exception.Hotelier;
import com.hotelier.model.dto.*;
import com.hotelier.service.AdminService;
import com.hotelier.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Constants;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.BindException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
@Transactional(rollbackFor = {Hotelier.class, NullPointerException.class, Exception.class, BindException.class, Constants.ConstantException.class})
@Tag(name = "Admin", description = "admin endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AdminResource {
    Logger log = Logger.getLogger(this.getClass().getName());
    private final AdminService adminService;
    private final RoomService roomService;

    @PostMapping("company")
    public CompanyDto addOrEditCompany(@RequestBody @Valid CompanyDto request) {
        return adminService.addOrEditCompany(request);
    }

    @GetMapping("company")
    public List<CompanyDto> getAllCompany() {
        return adminService.getAllCompany();
    }

    @PostMapping("property")
    public void addOrEditProperty(@RequestBody @Valid PropertyDto propertyDto) {
        adminService.addOrEditProperty(propertyDto);
    }

    @GetMapping("property")
    public List<PropertyDto> propertyDtoList() {
        return adminService.propertyDtoList();
    }

    @PostMapping("feature")
    public List<FeatureDto> addFeatures(@RequestBody @Valid FeatureDto[] features) {
        return adminService.addFeatures(features);
    }

    @GetMapping("feature")
    public List<FeatureDto> getFeatures() {
        return adminService.getFeatures();
    }

    @GetMapping("reservation/{id}/cancel")
    @RolesAllowed({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public void deleteReservation(@PathVariable Long id) {
        roomService.manuallyCheckOutReservation(id);
    }

    @GetMapping("reservation/{id}/check-out")
    @RolesAllowed({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public TransactionDto checkout(@PathVariable Long id) {
        return roomService.checkOutReservation(id);
    }

    @PutMapping("reservation/{id}/checkIn")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public void checkInReservation(@PathVariable Long id) {
        roomService.checkInReservation(id);
    }

    @PostMapping("roomcategory")
    public RoomCategoryDto addOrEditRoomCategory(@RequestBody RoomCategoryDto dto) {
        return adminService.addOrEditRoomCategory(dto);
    }

    @GetMapping("reservation")
    @RolesAllowed({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public List<ReservationDto> countByCategory(@RequestParam(required = false) ReservationDto.ReservationStatus status, @RequestParam Long propertyId, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate) {
        return adminService.getReservations(propertyId, status, startDate);
    }

    @GetMapping("reservation/all/{id}")
    @RolesAllowed({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public List<ReservationDto> getReservations(@PathVariable("id") Long propertyId) {
        return adminService.getReservations(propertyId);
    }

    @GetMapping("reservation/guest/{id}")
    @RolesAllowed({"ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_GUEST"})
    public List<ReservationDto> findReservationByGuest(@PathVariable Long id, @RequestParam(required = false) ReservationDto.ReservationStatus status) {
        return adminService.findReservationByGuest(id, status);
    }

    @PostMapping("personnel")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addPersonnel(@RequestBody @Valid PersonnelDto personnelDto) {
        adminService.addPersonnel(personnelDto);
    }

    @GetMapping("personnel")
    @RolesAllowed({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public PersonnelDto getPersonnel() {
        return adminService.getPersonnel();
    }

    @GetMapping("personnel/all")
    @RolesAllowed({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public List<PersonnelDto> getAllPersonnel(@RequestParam Long propertyId) {
        return adminService.getAllPersonnel(propertyId);
    }

    @GetMapping("room/available")
    @RolesAllowed({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    @Operation(summary = "Rooms available today")
    public List<RoomDto> availableRooms(@RequestParam @Min(1) Long propertyId) {
        return adminService.availableRoomsForToday(propertyId);
    }

    @GetMapping("room/available/{startTime}/{endTime}")
    @RolesAllowed({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public List<RoomDto> availableRooms(@PathVariable @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
                                        @PathVariable @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime, @RequestParam @Min(1) Long propertyId) {
        return adminService.availableRooms(startTime, endTime, propertyId);
    }

    @GetMapping("guest/search/{searchText}")
    public List<GuestDto> searchGuest(@PathVariable String searchText) {
        return adminService.searchGuest(searchText);
    }

    @GetMapping("transaction/{transactionId}")
    public TransactionDto getTransaction(@PathVariable Long transactionId) {
        return adminService.getTransaction(transactionId);
    }

    @GetMapping("transaction/pay/{transactionId}")
    @RolesAllowed({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public void payTransactionByCash(@PathVariable Long transactionId) {
        adminService.transpaymentByCash(transactionId);
    }
    @GetMapping("summary/reservation/{propertyId}")
    public HashMap<String, Object> summary(@PathVariable Long propertyId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){
        return adminService.getStat(propertyId,startDate, endDate);
    }

}
