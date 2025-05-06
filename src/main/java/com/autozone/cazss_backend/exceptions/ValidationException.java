package com.autozone.cazss_backend.exceptions;

// ValidationException -> Cuando el usuario envía datos inválidos (errores 4xx).

public class ValidationException extends RuntimeException {
  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
