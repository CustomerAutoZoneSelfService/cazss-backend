package com.autozone.cazss_backend.exceptions;

public class HistoryNotFoundException extends RuntimeException {
  public HistoryNotFoundException(String message) {
    super(message);
  }
}
