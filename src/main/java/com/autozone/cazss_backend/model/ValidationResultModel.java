package com.autozone.cazss_backend.model;

import java.util.HashMap;
import java.util.Map;

public class ValidationResultModel {
  private String status = "true"; // Por defecto, la validación es exitosa
  private Map<String, Map<String, String>> errors = new HashMap<>();

  public ValidationResultModel(String status, Map<String, Map<String, String>> errors) {
    this.status = status;
    this.errors = errors;
  }

  public ValidationResultModel() {}

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Map<String, Map<String, String>> getErrors() {
    return errors;
  }

  public void setErrors(Map<String, Map<String, String>> errors) {
    this.errors = errors;
  }
}
