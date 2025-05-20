package com.autozone.cazss_backend.advice;

import com.autozone.cazss_backend.exceptions.AZClientException;
import com.autozone.cazss_backend.exceptions.CustomException;
import com.autozone.cazss_backend.exceptions.ErrorResponseTemplate;
import com.autozone.cazss_backend.exceptions.ValidationException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class) // Excepción personalizable
  public ResponseEntity<Object> handleCustomException(final CustomException ex) {
    // Crear el objeto ErrorResponse
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "CUSTOM_ERROR",
            ex.getMessage(),
            "Detalles específicos sobre el error",
            LocalDateTime.now(),
            UUID.randomUUID().toString());
    // Devolver el error con el código de estado adecuado
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(
      ValidationException.class) // Excepción de validación de datos lanzados al executeService
  public ResponseEntity<Object> handleValidationException(final ValidationException ex) {
    // Crear el objeto ErrorResponse
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "VALIDATION_ERROR",
            ex.getMessage(),
            "Error al validar el ID, headers o body del servicio solicitado",
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(AZClientException.class) // Excepción de AZClient
  public ResponseEntity<Object> handleAZClientException(final AZClientException ex) {
    // Crear el objeto ErrorResponse
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "SERVICE_ERROR",
            ex.getMessage(),
            "Service could be unavailable",
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class) // Excepción de bad request
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex) {
    // Obtener el mensaje de error de validación
    String defaultMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    // Crear el objeto ErrorResponse
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "VALIDATION_ERROR",
            defaultMessage,
            "Detalles del error de validación",
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class) // Excepción general
  public ResponseEntity<Object> handleGeneralException(final Exception ex) {
    // Crear el objeto ErrorResponse
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "INTERNAL_ERROR",
            "Unexpected error occurred",
            ex.getMessage(), // use actual error message
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
