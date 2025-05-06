package com.autozone.cazss_backend.DTO;

public class ServiceResponseDTO {
  private Integer statusCode;
  private String response;

  public ServiceResponseDTO(Integer statusCode, String response) {
    this.statusCode = statusCode;
    this.response = response;
  }

  public ServiceResponseDTO() {}

  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }
}
