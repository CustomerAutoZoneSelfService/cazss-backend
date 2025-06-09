package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.LoginRequestDTO;
import com.autozone.cazss_backend.DTO.LoginResponseDTO;
import com.autozone.cazss_backend.DTO.RefreshTokenRequestDTO;
import com.autozone.cazss_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

  private final AuthService authService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public AuthController(AuthService authService, PasswordEncoder passwordEncoder) {
    this.authService = authService;
    this.passwordEncoder = passwordEncoder;
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

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponseDTO> refreshToken(
      @Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
    try {
      LoginResponseDTO response = authService.refreshToken(refreshTokenRequest);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new LoginResponseDTO(e.getMessage()));
    }
  }

  // just to encode them in the database, might delete later or idk xd
  @GetMapping("/encode-password")
  public String encodePassword(@RequestParam String password) {
    return passwordEncoder.encode(password);
  }
}
