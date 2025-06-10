package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.LoginRequestDTO;
import com.autozone.cazss_backend.DTO.LoginResponseDTO;
import com.autozone.cazss_backend.DTO.RefreshTokenRequestDTO;
import com.autozone.cazss_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private JwtUtil jwtUtil;
  @Autowired private UserDetailsServiceImpl userDetailsService;

  public LoginResponseDTO loginUser(LoginRequestDTO loginRequest) {
    // Authenticate using Spring Security
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));

    // Get UserDetails
    UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

    // Generate both tokens
    String accessToken = jwtUtil.generateAccessToken(userDetails);
    String refreshToken = jwtUtil.generateRefreshToken(userDetails);

    return new LoginResponseDTO("Login successful!", accessToken, refreshToken);
  }

  public LoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest) {
    String refreshToken = refreshTokenRequest.getRefreshToken();

    if (jwtUtil.isTokenExpired(refreshToken)) {
      throw new RuntimeException("Refresh token has expired");
    }

    String userId = jwtUtil.extractUserId(refreshToken);
    UserDetails userDetails = userDetailsService.loadUserById(Integer.parseInt(userId));

    if (!jwtUtil.validateToken(refreshToken, userDetails)) {
      throw new RuntimeException("Invalid refresh token");
    }

    String newAccessToken = jwtUtil.generateAccessToken(userDetails);
    String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

    return new LoginResponseDTO("Token refreshed successfully!", newAccessToken, newRefreshToken);
  }
}
