package com.hotelier.model.dto;

import com.hotelier.model.entity.Guest;
import com.hotelier.model.repository.CountryRepo;
import com.hotelier.model.repository.StateRepo;
import com.hotelier.service.FileService;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Setter
@Getter
@NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuestDto {
    Long id;
    @NotNull
    LocalDate dateOfBirth;
    @NotNull
    StateDto.CountryDto nationality;
    @NotNull
    StateDto stateOfResidence;
    @NotNull @Size(min = 2)
    String residentialCity;
    String postalCode;
    FileDto[] files;
    UserDto user;

    @SneakyThrows

    public static GuestDto fromGuest(Guest guest, FileService fileService) {
        GuestDto guestDto = new GuestDto();
        guestDto.setId(guest.getId());
        guestDto.setDateOfBirth(guest.getDateOfBirth());
        guestDto.setNationality(StateDto.CountryDto.fromCountry(guest.getNationality(), false));
        guestDto.setStateOfResidence(StateDto.fromState(guest.getResidentialState()));
        guestDto.setResidentialCity(guest.getResidentialCity());
        guestDto.setPostalCode(guest.getPostalCode());
        guestDto.setFiles(fileService == null ? null : fileService.downloadFile(guest.getIdentity()));
        guestDto.setUser(UserDto.fromUser(guest.getUser()));
        return guestDto;
    }

    public static Guest toGuest(GuestDto guestDto, StateRepo stateRepo, CountryRepo countryRepo) {
        Guest guest = new Guest();
        guest.setId(guest.getId());
        guest.setDateOfBirth(guestDto.getDateOfBirth());
        guest.setNationality(countryRepo.getById(guestDto.getNationality().getId()));
        guest.setPostalCode(guestDto.getPostalCode());
        guest.setResidentialCity(guestDto.getResidentialCity());
        guest.setResidentialState(stateRepo.getById(guestDto.getStateOfResidence().getId()));
        return guest;
    }


}
