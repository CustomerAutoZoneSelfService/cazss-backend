package com.autozone.cazss_backend.DTO;

public class LoginResponseDTO {
  private String message;
  private String token;

  public LoginResponseDTO(String message) {
    this.message = message;
  }

  public LoginResponseDTO(String message, String token) {
    this.message = message;
    this.token = token;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  // JWT probably goes here or idk
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
