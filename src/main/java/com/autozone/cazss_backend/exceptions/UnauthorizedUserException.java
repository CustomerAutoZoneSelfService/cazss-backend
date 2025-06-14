package com.autozone.cazss_backend.exceptions;

public class UnauthorizedUserException extends RuntimeException {
  public UnauthorizedUserException(String message) {
    super(message);
  }
}
