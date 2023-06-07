package com.hotelier.model.dto;

import com.hotelier.model.entity.Personnel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data @RequiredArgsConstructor
public class PersonnelDto {
    @NotNull
    UserDto user;
    @NotNull @Size(min = 2, max = 45)
    String designation;
    @NotNull
    PropertyDto[] property;

    public static PersonnelDto fromPersonnel(Personnel personnel, boolean prop){
        if(personnel == null){
            return null;
        }
        PersonnelDto personnelDto = new PersonnelDto();
        personnelDto.setDesignation(personnel.getDesignation());
        personnelDto.setUser(UserDto.fromUser(personnel.getUser()));
        personnelDto.setProperty(prop?personnel.getPropertyList().stream().map(PropertyDto::fromProperty).toArray(PropertyDto[]::new):null);
        return personnelDto;
    }

    public static Personnel toPersonnel(PersonnelDto personnelDto) {
        if(personnelDto == null){
            return null;
        }
        personnelDto.getUser().setPassword(null);
        Personnel personnel  = new Personnel();
        personnel.setDesignation(personnelDto.getDesignation());
        personnel.setUser(UserDto.toUser(personnelDto.getUser(), null));
        personnel.setPropertyList(Arrays.stream(personnelDto.getProperty()).map(PropertyDto::toProperty).collect(Collectors.toList()));
        return personnel;
    }
}
