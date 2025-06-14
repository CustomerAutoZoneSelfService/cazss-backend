package com.autozone.cazss_backend.advice;

import com.autozone.cazss_backend.exceptions.*;
import com.autozone.cazss_backend.exceptions.ResponsePatternTreeValidatorExceptions.CycleDetectedException;
import com.autozone.cazss_backend.exceptions.ResponsePatternTreeValidatorExceptions.DuplicateResponsePatternIdException;
import com.autozone.cazss_backend.exceptions.ResponsePatternTreeValidatorExceptions.InvalidRootNodeException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Handles custom exceptions for domain logic (e.g., services, response trees). */
@ControllerAdvice
public class ControllerAdvisor {

  @ExceptionHandler({ServiceNotFoundException.class, UserNotFoundException.class})
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

  @ExceptionHandler(DuplicateResponsePatternIdException.class)
  public ResponseEntity<ErrorResponseTemplate> handleDuplicateId(
      DuplicateResponsePatternIdException ex) {
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "DUPLICATE_ID",
            ex.getMessage(),
            "Each response pattern ID must be unique",
            LocalDateTime.now(),
            UUID.randomUUID().toString());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidRootNodeException.class)
  public ResponseEntity<ErrorResponseTemplate> handleInvalidRoot(InvalidRootNodeException ex) {
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "INVALID_ROOT",
            ex.getMessage(),
            "There must be exactly one root node",
            LocalDateTime.now(),
            UUID.randomUUID().toString());
    return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(CycleDetectedException.class)
  public ResponseEntity<ErrorResponseTemplate> handleCycle(CycleDetectedException ex) {
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "CYCLE_ERROR",
            ex.getMessage(),
            "Tree structure must not contain cycles",
            LocalDateTime.now(),
            UUID.randomUUID().toString());
    return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
  }
}
