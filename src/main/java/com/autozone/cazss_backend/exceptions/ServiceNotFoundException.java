package com.autozone.cazss_backend.exceptions;

public class ServiceNotFoundException extends RuntimeException {
  public ServiceNotFoundException(String message) {
    super(message);
  }
}
