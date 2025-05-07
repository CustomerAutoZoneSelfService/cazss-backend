package com.autozone.cazss_backend.advice;

import com.autozone.cazss_backend.exceptions.ErrorResponseTemplate;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {

  @ExceptionHandler(ServiceNotFoundException.class)
  public ResponseEntity<ErrorResponseTemplate> handleNotFoundException(
      ServiceNotFoundException ex) {
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "NOT_FOUND",
            ex.getMessage(),
            "The requested resource was not found",
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }
}
