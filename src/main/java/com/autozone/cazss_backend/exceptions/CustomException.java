package com.autozone.cazss_backend.exceptions;

// Para errores generales del negocio que no encajan en otra categoría.

public class CustomException extends RuntimeException {

  public CustomException() {
    super();
  }

  public CustomException(final String message) {
    super(message);
  }
}
