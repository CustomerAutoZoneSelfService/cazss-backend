package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.autozone.cazss_backend.DTO.LoginRequestDTO;
import com.autozone.cazss_backend.DTO.LoginResponseDTO;
import com.autozone.cazss_backend.DTO.RefreshTokenRequestDTO;
import com.autozone.cazss_backend.repository.UserRepository;
import com.autozone.cazss_backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private AuthenticationManager authenticationManager;

  @Mock private JwtUtil jwtUtil;

  @Mock private UserDetailsServiceImpl userDetailsService;

  @Mock private Authentication authentication;

  @Mock private UserDetails userDetails;

  @InjectMocks private AuthService authService;

  private final String email = "test@example.com";
  private final String password = "password123";
  private final String accessToken = "access-token";
  private final String refreshToken = "refresh-token";
  private final String userId = "42";

  @BeforeEach
  void setUp() {
    // MockitoAnnotations.openMocks(this); // Not needed with @ExtendWith(MockitoExtension.class)
  }

  @Test
  void loginUser_ShouldReturnLoginResponseDTO_WhenCredentialsAreValid() {
    // Arrange
    LoginRequestDTO loginRequest = new LoginRequestDTO();
    loginRequest.setEmail(email);
    loginRequest.setPassword(password);

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);

    when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

    when(jwtUtil.generateAccessToken(userDetails)).thenReturn(accessToken);
    when(jwtUtil.generateRefreshToken(userDetails)).thenReturn(refreshToken);

    // Act
    LoginResponseDTO response = authService.loginUser(loginRequest);

    // Assert
    assertNotNull(response);
    assertEquals("Login successful!", response.getMessage());
    assertEquals(accessToken, response.getAccessToken());
    assertEquals(refreshToken, response.getRefreshToken());

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userDetailsService).loadUserByUsername(email);
    verify(jwtUtil).generateAccessToken(userDetails);
    verify(jwtUtil).generateRefreshToken(userDetails);
  }

  @Test
  void refreshToken_ShouldReturnNewTokens_WhenRefreshTokenIsValid() {
    // Arrange
    RefreshTokenRequestDTO refreshTokenRequest = new RefreshTokenRequestDTO();
    refreshTokenRequest.setRefreshToken(refreshToken);

    when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(false);
    when(jwtUtil.extractUserId(refreshToken)).thenReturn(userId);
    when(userDetailsService.loadUserById(Integer.parseInt(userId))).thenReturn(userDetails);
    when(jwtUtil.validateToken(refreshToken, userDetails)).thenReturn(true);
    when(jwtUtil.generateAccessToken(userDetails)).thenReturn(accessToken);
    when(jwtUtil.generateRefreshToken(userDetails)).thenReturn(refreshToken);

    // Act
    LoginResponseDTO response = authService.refreshToken(refreshTokenRequest);

    // Assert
    assertNotNull(response);
    assertEquals("Token refreshed successfully!", response.getMessage());
    assertEquals(accessToken, response.getAccessToken());
    assertEquals(refreshToken, response.getRefreshToken());

    verify(jwtUtil).isTokenExpired(refreshToken);
    verify(jwtUtil).extractUserId(refreshToken);
    verify(userDetailsService).loadUserById(Integer.parseInt(userId));
    verify(jwtUtil).validateToken(refreshToken, userDetails);
    verify(jwtUtil).generateAccessToken(userDetails);
    verify(jwtUtil).generateRefreshToken(userDetails);
  }

  @Test
  void refreshToken_ShouldThrowException_WhenRefreshTokenIsExpired() {
    // Arrange
    RefreshTokenRequestDTO refreshTokenRequest = new RefreshTokenRequestDTO();
    refreshTokenRequest.setRefreshToken(refreshToken);

    when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(true);

    // Act & Assert
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> authService.refreshToken(refreshTokenRequest));

    assertEquals("Refresh token has expired", exception.getMessage());

    verify(jwtUtil).isTokenExpired(refreshToken);
    verifyNoMoreInteractions(jwtUtil, userDetailsService);
  }

  @Test
  void refreshToken_ShouldThrowException_WhenRefreshTokenIsInvalid() {
    // Arrange
    RefreshTokenRequestDTO refreshTokenRequest = new RefreshTokenRequestDTO();
    refreshTokenRequest.setRefreshToken(refreshToken);

    when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(false);
    when(jwtUtil.extractUserId(refreshToken)).thenReturn(userId);
    when(userDetailsService.loadUserById(Integer.parseInt(userId))).thenReturn(userDetails);
    when(jwtUtil.validateToken(refreshToken, userDetails)).thenReturn(false);

    // Act & Assert
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> authService.refreshToken(refreshTokenRequest));

    assertEquals("Invalid refresh token", exception.getMessage());

    verify(jwtUtil).isTokenExpired(refreshToken);
    verify(jwtUtil).extractUserId(refreshToken);
    verify(userDetailsService).loadUserById(Integer.parseInt(userId));
    verify(jwtUtil).validateToken(refreshToken, userDetails);
    verifyNoMoreInteractions(jwtUtil);
  }
}
