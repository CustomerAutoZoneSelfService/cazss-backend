package com.autozone.cazss_backend.exceptions;

public class AuthenticationStrategyNotFoundException extends RuntimeException {
  public AuthenticationStrategyNotFoundException(String message) {
    super(message);
  }
}
