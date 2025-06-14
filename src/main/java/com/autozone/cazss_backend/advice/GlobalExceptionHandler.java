package com.autozone.cazss_backend.advice;

import com.autozone.cazss_backend.exceptions.*;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Handles framework-level, validation, integration, and unknown exceptions. */
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

  @ExceptionHandler(
      ServiceNotActiveException
          .class) // Excepción de validación de datos lanzados al executeService
  public ResponseEntity<Object> handleServiceNotActiveException(
      final ServiceNotActiveException ex) {
    // Crear el objeto ErrorResponse
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "SERVICE_NOT_ACTIVE_ERROR",
            ex.getMessage(),
            "The service you are trying to reach is inactive",
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(UnauthorizedUserException.class)
  public ResponseEntity<Object> handleValidationException(final UnauthorizedUserException ex) {
    // Crear el objeto ErrorResponse
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "VALIDATION_ERROR",
            ex.getMessage(),
            "This feature is only available to administrators.",
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(
      CategoryNotFoundException.class) // Excepción de categoría no encontrada en alguna operación
  public ResponseEntity<Object> handleValidationException(final CategoryNotFoundException ex) {
    // Crear el objeto ErrorResponse
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "CATEGORY_DOES_NOT_EXIST",
            ex.getMessage(),
            "No such category was found.",
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(
      CategoryAlreadyExistsException.class) // Excepción de acceso no autorizado a un endpoint
  public ResponseEntity<Object> handleValidationException(final CategoryAlreadyExistsException ex) {
    // Crear el objeto ErrorResponse
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "CATEGORY_ALREADY_EXISTS",
            ex.getMessage(),
            "Can't create category with already existing name.",
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
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

  @ExceptionHandler(MethodArgumentNotValidException.class)
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

  @ExceptionHandler(MissingRequestHeaderException.class) // Excepción de bad request
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      final MissingRequestHeaderException ex) {
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "MISSING_REQUEST_HEADER",
            ex.getMessage(),
            "Missing headers during endpoint call",
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class) // Excepción de bad request
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      final HttpMessageNotReadableException ex) {
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "INVALID_REQUEST",
            ex.getMessage(),
            "Check your request fields.",
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      final HandlerMethodValidationException ex) {
    // Obtener el mensaje de error de validación
    String defaultMessage = ex.getMessage();
    // Crear el objeto ErrorResponse
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "VALIDATION_ERROR",
            defaultMessage,
            ex.getAllErrors().getLast().getDefaultMessage(),
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentTypeMismatchException ex) {
    // Obtener el mensaje de error de validación
    String defaultMessage = ex.getMessage();
    // Crear el objeto ErrorResponse
    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "VALIDATION_ERROR",
            "Check your parameters",
            defaultMessage,
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGeneralException(final Exception ex) {
    ex.printStackTrace(); // ⬅️ TEMPORARY for console logging

    ErrorResponseTemplate error =
        new ErrorResponseTemplate(
            "INTERNAL_ERROR",
            "Unexpected error occurred",
            ex.getMessage(), // show real error details
            LocalDateTime.now(),
            UUID.randomUUID().toString());

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
