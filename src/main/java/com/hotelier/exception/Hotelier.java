package com.hotelier.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class Hotelier extends RuntimeException{
    private final HttpStatus status;

    public Hotelier(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
