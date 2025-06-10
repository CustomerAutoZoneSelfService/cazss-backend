package com.autozone.cazss_backend.exceptions;

public class CategoryAlreadyExistsException extends RuntimeException {
  public CategoryAlreadyExistsException(String message) {
    super(message);
  }
}
