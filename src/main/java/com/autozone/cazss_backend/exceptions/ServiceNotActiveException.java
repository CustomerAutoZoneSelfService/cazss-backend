package com.autozone.cazss_backend.exceptions;

public class ServiceNotActiveException extends RuntimeException {
  public ServiceNotActiveException(Integer id) {
    super(String.format("Service with id %s is not active", id));
  }
}
