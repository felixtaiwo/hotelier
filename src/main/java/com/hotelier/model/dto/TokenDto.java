package com.hotelier.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data @AllArgsConstructor
public class TokenDto {
    UserDto userDto;
    String token;
    LocalDateTime expiry;
}
