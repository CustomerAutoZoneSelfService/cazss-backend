package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.autozone.cazss_backend.DTO.LoginRequestDTO;
import com.autozone.cazss_backend.DTO.LoginResponseDTO;
import com.autozone.cazss_backend.DTO.RefreshTokenRequestDTO;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.UserRepository;
import com.autozone.cazss_backend.security.JwtUtil;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private AuthenticationManager authenticationManager;

  @Mock private JwtUtil jwtUtil;

  @Mock private UserDetailsServiceImpl userDetailsService;

  @Mock private Authentication authentication;

  @InjectMocks private AuthService authService;

  private final String email = "test@example.com";
  private final String password = "password123";
  private final String accessToken = "access-token";
  private final String refreshToken = "refresh-token";
  private final Integer userId = 42;
  private UserDetails userDetails;
  private UserEntity userEntity;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity();
    userEntity.setUserId(userId);
    userEntity.setEmail(email);
    userEntity.setUsername("testuser");
    userEntity.setPassword(password);
    userEntity.setRole(UserRoleEnum.ADMIN);
    userEntity.setActive(true);

    userDetails =
        new User(
            String.valueOf(userId),
            password,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
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
    when(jwtUtil.extractUserId(refreshToken)).thenReturn(String.valueOf(userId));
    when(userDetailsService.loadUserById(userId)).thenReturn(userDetails);
    when(jwtUtil.validateToken(refreshToken, userDetails)).thenReturn(true);
    when(jwtUtil.generateAccessToken(userDetails)).thenReturn("new-access-token");
    when(jwtUtil.generateRefreshToken(userDetails)).thenReturn("new-refresh-token");

    // Act
    LoginResponseDTO response = authService.refreshToken(refreshTokenRequest);

    // Assert
    assertNotNull(response);
    assertEquals("Token refreshed successfully!", response.getMessage());
    assertEquals("new-access-token", response.getAccessToken());
    assertEquals("new-refresh-token", response.getRefreshToken());

    verify(jwtUtil).isTokenExpired(refreshToken);
    verify(jwtUtil).extractUserId(refreshToken);
    verify(userDetailsService).loadUserById(userId);
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
    verify(jwtUtil, never()).extractUserId(anyString());
    verify(userDetailsService, never()).loadUserById(anyInt());
    verify(jwtUtil, never()).validateToken(anyString(), any(UserDetails.class));
    verify(jwtUtil, never()).generateAccessToken(any(UserDetails.class));
    verify(jwtUtil, never()).generateRefreshToken(any(UserDetails.class));
  }

  @Test
  void refreshToken_ShouldThrowException_WhenRefreshTokenIsInvalid() {
    // Arrange
    RefreshTokenRequestDTO refreshTokenRequest = new RefreshTokenRequestDTO();
    refreshTokenRequest.setRefreshToken(refreshToken);

    when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(false);
    when(jwtUtil.extractUserId(refreshToken)).thenReturn(String.valueOf(userId));
    when(userDetailsService.loadUserById(userId)).thenReturn(userDetails);
    when(jwtUtil.validateToken(refreshToken, userDetails)).thenReturn(false);

    // Act & Assert
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> authService.refreshToken(refreshTokenRequest));
    assertEquals("Invalid refresh token", exception.getMessage());

    verify(jwtUtil).isTokenExpired(refreshToken);
    verify(jwtUtil).extractUserId(refreshToken);
    verify(userDetailsService).loadUserById(userId);
    verify(jwtUtil).validateToken(refreshToken, userDetails);
    verify(jwtUtil, never()).generateAccessToken(any(UserDetails.class));
    verify(jwtUtil, never()).generateRefreshToken(any(UserDetails.class));
  }
}
