package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.LoginRequestDTO;
import com.autozone.cazss_backend.DTO.LoginResponseDTO;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;

  @Autowired
  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public LoginResponseDTO loginUser(LoginRequestDTO loginRequest) {
    UserEntity user =
        userRepository
            .findByEmail(loginRequest.getEmail())
            .orElseThrow(
                () ->
                    new RuntimeException("User not found with email: " + loginRequest.getEmail()));

    if (!loginRequest.getPassword().equals(user.getPassword())) {
      throw new RuntimeException("Invalid credentials: Incorrect password.");
    }

    return new LoginResponseDTO("Login successful!");
  }
}
