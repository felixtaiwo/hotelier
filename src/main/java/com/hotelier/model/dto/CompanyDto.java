package com.hotelier.model.dto;

import com.hotelier.model.entity.Company;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter @Getter @NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyDto {
    Long id;
    @NotNull @Size(min = 2, max = 40)
    String name;
    @NotNull @Size(min = 2, max = 40)
    String city;
    String postalCode;
    @NotNull
    StateDto state;
    String logoUrl;
    @Email @NotNull @Size(min = 2, max = 40)
    String email;
    @Size(min = 9, max = 40)
    String phone;

    public static Company toCompany(CompanyDto companyDto){
        if(companyDto == null)
            return null;
        Company company = new Company();
        company.setId(companyDto.getId());
        company.setHqCity(companyDto.getCity());
        company.setName(companyDto.getName());
        company.setLogoUrl(companyDto.getLogoUrl());
        company.setHqPostalCode(companyDto.getPostalCode());
        company.setSupportEmail(companyDto.getEmail());
        company.setSupportPhone(companyDto.getPhone());
        company.setHqState(StateDto.toState(companyDto.getState()));
        return company;
    }

    public static CompanyDto fromCompany(Company company){
        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(company.getId());
        companyDto.setName(company.getName());
        companyDto.setCity(company.getHqCity());
        companyDto.setPostalCode(company.getHqPostalCode());
        companyDto.setState(StateDto.fromState(company.getHqState()));
        companyDto.setLogoUrl(company.getLogoUrl());
        companyDto.setEmail(company.getSupportEmail());
        companyDto.setPhone(company.getSupportPhone());
        return companyDto;
    }

}
