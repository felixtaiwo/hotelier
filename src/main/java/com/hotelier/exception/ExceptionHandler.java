package com.hotelier.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler({Hotelier.class})
    public ResponseEntity<ExceptionResponse<String>> hotelierEx(Hotelier ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ExceptionResponse.<String>builder()
                        .message(ex.getMessage() != null? ex.getMessage() : "Hotelier: Error please try again")
                        .timestamp(LocalDateTime.now())
                        .errors(List.of(ex.getMessage()))
                        .build()
                );
    }
    @org.springframework.web.bind.annotation.ExceptionHandler({NullPointerException.class})
    public ResponseEntity<ExceptionResponse<String>> nullPEx(NullPointerException ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.<String>builder()
                        .message(ex.getMessage() != null? ex.getMessage() : "Hotelier: NullPointer Exception")
                        .timestamp(LocalDateTime.now())
                        .errors(Collections.singletonList(Arrays.toString(ex.getStackTrace())))
                        .build()
                );
    }
    @org.springframework.web.bind.annotation.ExceptionHandler({BindException.class})
    public ResponseEntity<ExceptionResponse<Violation>> raiseForValidationErrors(BindException be) {
        var errors = new ArrayList<Violation>();
        be.getBindingResult().getAllErrors()
                .forEach(cv -> errors
                        .add(
                                Violation.builder()
                                        .prop(String.format("%s.%s",
                                                cv.getObjectName(), ((FieldError) cv).getField())
                                        )
                                        .error(cv.getDefaultMessage())
                                        .build()
                        )
                );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.<Violation>builder()
                                .message("Validation Exception")
                                .timestamp(LocalDateTime.now())
                                .errors(errors)
                                .build()
                );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ExceptionResponse<Violation>> raiseForConstraintValidationErrors(ConstraintViolationException be) {
        var violations = be.getConstraintViolations()
                .stream()
                .map(
                        cv -> Violation.builder()
                                .prop(cv.getPropertyPath().toString())
                                .error(cv.getMessage()
                                )
                                .build()
                ).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.<Violation>builder()
                                .message("Validation Exception")
                                .timestamp(LocalDateTime.now())
                                .errors(violations)
                                .build()
                );
    }

    @Getter
    @Jacksonized
    @Builder
    public static class Violation implements Serializable {
        String prop;
        String error;
    }
}
