package com.hotelier.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginDto {
    @Email(message = "Invalid Email Address") @NotNull @Size(min = 2, message = "email address character must be greater than 1 in length")
    private String emailAddress;
    @NotNull @Size(min = 6, message = "password character must be greater than 6 in length")
    private String password;
}
