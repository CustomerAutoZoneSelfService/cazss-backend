package com.autozone.cazss_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// ValidationException -> Cuando el usuario envía datos inválidos (errores 4xx).

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class AZClientException extends RuntimeException {
  public AZClientException(String message) {
    super(message);
  }
}
