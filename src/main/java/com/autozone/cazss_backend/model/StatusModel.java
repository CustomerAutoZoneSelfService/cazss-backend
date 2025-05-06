package com.autozone.cazss_backend.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Encapsulates the HTTP status of the service response")
public class StatusModel {
  @Schema(description = "HTTP status code", example = "200")
  private int code;

  @Schema(description = "HTTP status description", example = "OK")
  private String description;

  public StatusModel(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
