package com.autozone.cazss_backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

  @Mock private UserRepository userRepository;

  private JwtUtil jwtUtil;

  private final String testSecret = "testSecretKeyForJwtUtilTesting123456789012345678901234567890";
  private final long testAccessTokenExpirationMs = 3600000; // 1 hour
  private final long testRefreshTokenExpirationMs = 86400000; // 1 day

  private UserEntity userEntity;
  private UserDetails userDetails;

  @BeforeEach
  void setUp() {
    jwtUtil =
        new JwtUtil(
            testSecret, testAccessTokenExpirationMs, testRefreshTokenExpirationMs, userRepository);

    userEntity = new UserEntity();
    userEntity.setUserId(1);
    userEntity.setEmail("test@example.com");
    userEntity.setUsername("testuser");
    userEntity.setPassword("hashedpassword");
    userEntity.setRole(UserRoleEnum.ADMIN);
    userEntity.setActive(true);

    List<GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    userDetails =
        new User(String.valueOf(userEntity.getUserId()), userEntity.getPassword(), authorities);
  }

  @Test
  void generateAccessToken_shouldCreateValidTokenWithCorrectClaims() {
    when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));

    String token = jwtUtil.generateAccessToken(userDetails);
    assertNotNull(token);

    DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
    assertEquals(String.valueOf(userEntity.getUserId()), decodedJWT.getSubject());
    assertEquals(userEntity.getUsername(), decodedJWT.getClaim("username").asString());
    assertEquals(userEntity.getEmail(), decodedJWT.getClaim("email").asString());
    assertEquals("ROLE_ADMIN", decodedJWT.getClaim("role").asString());
    assertFalse(jwtUtil.isTokenExpired(token));

    verify(userRepository).findById(1);
  }

  @Test
  void generateAccessToken_shouldThrowUsernameNotFoundException_whenUserNotInRepository() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class,
        () -> {
          jwtUtil.generateAccessToken(userDetails);
        });
  }

  @Test
  void generateRefreshToken_shouldCreateValidToken() {
    when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));

    String token = jwtUtil.generateRefreshToken(userDetails);
    assertNotNull(token);

    DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
    assertEquals(String.valueOf(userEntity.getUserId()), decodedJWT.getSubject());
    assertEquals(userEntity.getUsername(), decodedJWT.getClaim("username").asString());
    assertFalse(jwtUtil.isTokenExpired(token));

    verify(userRepository).findById(1);
  }

  @Test
  void validateToken_shouldReturnTrue_forValidTokenAndMatchingUserDetails() {
    when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
    String token = jwtUtil.generateAccessToken(userDetails);

    assertTrue(jwtUtil.validateToken(token, userDetails));
  }

  @Test
  void validateToken_shouldReturnFalse_forExpiredToken() throws InterruptedException {
    // Create a token with very short lifespan for testing expiration
    JwtUtil shortLivedJwtUtil =
        new JwtUtil(testSecret, 1, testRefreshTokenExpirationMs, userRepository);
    when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));

    String token = shortLivedJwtUtil.generateAccessToken(userDetails);
    Thread.sleep(50); // Wait for token to expire

    assertTrue(jwtUtil.isTokenExpired(token)); // isTokenExpired should be true
    assertFalse(jwtUtil.validateToken(token, userDetails)); // validateToken should be false
  }

  @Test
  void validateToken_shouldReturnFalse_forMismatchedUserIdInUserDetails() {
    when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
    String token = jwtUtil.generateAccessToken(userDetails);

    UserDetails differentUserDetails = new User("2", "password", Collections.emptyList());
    assertFalse(jwtUtil.validateToken(token, differentUserDetails));
  }

  @Test
  void validateToken_shouldReturnFalse_forInvalidTokenSignature() {
    String tamperedToken =
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJ0ZXN0dXNlciIsInJvbGUiOiJST0xFX0FETUlOIiwiaWF0IjoxNjc4ODg2NDAwLCJleHAiOjE2Nzg4ODk5OTh9.tamperedSignaturePart";
    assertFalse(jwtUtil.validateToken(tamperedToken, userDetails));
    assertThrows(JWTVerificationException.class, () -> jwtUtil.extractUserId(tamperedToken));
  }

  @Test
  void isTokenExpired_shouldReturnTrue_whenVerificationFails() {
    String malformedToken = "this.is.not.a.jwt";
    assertTrue(jwtUtil.isTokenExpired(malformedToken));
  }
}