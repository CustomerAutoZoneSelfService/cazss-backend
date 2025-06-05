package com.autozone.cazss_backend.DTO;

public class LoginResponseDTO {
  private String message;
  private String accessToken;
  private String refreshToken;

  public LoginResponseDTO(String message) {
    this.message = message;
  }

  public LoginResponseDTO(String message, String accessToken, String refreshToken) {
    this.message = message;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
