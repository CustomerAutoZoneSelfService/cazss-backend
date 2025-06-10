package com.autozone.cazss_backend.DTO;

import jakarta.validation.constraints.NotBlank;

// This DTO will hold the email and password sent by the client for login.
public class LoginRequestDTO {

  @NotBlank(message = "Email cannot be empty")
  private String email; // Changed from 'username' to 'email' as per request

  @NotBlank(message = "Password cannot be empty")
  private String password;

  // Constructors
  public LoginRequestDTO() {}

  // Corrected constructor: 'email' parameter now correctly assigned
  public LoginRequestDTO(String email, String password) {
    this.email = email;
    this.password = password;
  }

  // Getters and Setters
  public String getEmail() { // Changed from getUsername()
    return email;
  }

  public void setEmail(String email) { // Changed from setUsername()
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    // IMPORTANT: Do NOT include password in toString() in a production application for security
    // reasons.
    return "LoginRequestDTO{" + "email='" + email + '\'' + '}';
  }
}
