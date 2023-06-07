package com.hotelier.model.dto;

import com.hotelier.model.entity.Property;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalTime;

@Setter @Getter @NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyDto {
    Long id;
    @NotNull
    @Size(min = 2, max = 40)
    String name;
    @NotNull
    @Size(min = 2, max = 10)
    String postalCode;
    @NotNull
    @Size(min = 2, max = 10)
    String city;
    @NotNull
    @Size(min = 2, max = 100)
    String fullAddress;
    @NotNull
    StateDto state;
    @NotNull @Size(min = 2, max = 40)
    @Email
    String email;
    @NotNull @Size(min = 2, max = 40)
    String phone;
    Float rating;
    @NotNull
    CompanyDto company;
    @NotNull
    @DateTimeFormat(pattern = "HH:mm:ss")
    LocalTime checkInTime;
    @NotNull
    @DateTimeFormat(pattern = "HH:mm:ss")
    LocalTime checkOutTime;



    public static Property toProperty(PropertyDto propertyDto) {
        if(propertyDto == null){
            return null;
        }
        Property property = new Property();
        property.setId(propertyDto.getId());
        property.setName(propertyDto.getName());
        property.setCompany(propertyDto.getCompany() == null? null :CompanyDto.toCompany(propertyDto.getCompany()));
        property.setPostalCode(propertyDto.getPostalCode());
        property.setSupportEmail(propertyDto.getEmail());
        property.setSupportPhone(propertyDto.getPhone());
        property.setStateAddr(propertyDto.getState() == null? null: StateDto.toState(propertyDto.getState()));
        property.setCity(propertyDto.getCity());
        property.setFullAddress(propertyDto.getFullAddress());
        property.setCheckInTime(propertyDto.getCheckInTime());
        property.setCheckOutTime(propertyDto.getCheckOutTime());
        return property;
    }

    public static PropertyDto fromProperty(Property property){
        PropertyDto propertyDto = new PropertyDto();
        propertyDto.setId(property.getId());
        propertyDto.setCompany(CompanyDto.fromCompany(property.getCompany()));
        propertyDto.setName(property.getName());
        propertyDto.setState(StateDto.fromState(property.getStateAddr()));
        propertyDto.setCity(property.getCity());
        propertyDto.setFullAddress(property.getFullAddress());
        propertyDto.setEmail(property.getSupportEmail());
        propertyDto.setPostalCode(property.getPostalCode());
        propertyDto.setPhone(property.getSupportPhone());
        propertyDto.setRating(property.getRating());
        propertyDto.setCheckInTime(property.getCheckInTime());
        propertyDto.setCheckOutTime(property.getCheckOutTime());
        return propertyDto;
    }
}
