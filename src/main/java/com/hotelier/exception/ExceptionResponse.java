package com.hotelier.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Jacksonized @Builder
public class ExceptionResponse<T extends Serializable> {
  String message;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  LocalDateTime timestamp;
  List<T> errors;
}
