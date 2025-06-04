package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.LoginRequestDTO;
import com.autozone.cazss_backend.DTO.LoginResponseDTO;
import com.autozone.cazss_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> loginUser(
      @Valid @RequestBody LoginRequestDTO loginRequest) {
    try {
      LoginResponseDTO response = authService.loginUser(loginRequest);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new LoginResponseDTO(e.getMessage()));
    }
  }
}
