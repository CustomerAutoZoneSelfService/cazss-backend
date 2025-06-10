package com.autozone.cazss_backend.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.DTO.LoginRequestDTO;
import com.autozone.cazss_backend.DTO.RefreshTokenRequestDTO;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private UserRepository userRepository;

  private static final String TEST_EMAIL = "integration@test.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_USERNAME = "integrationUser";
  private static final String TEST_ROLE = "USER";

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();

    UserEntity user = new UserEntity();
    user.setEmail(TEST_EMAIL);
    user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
    user.setUsername(TEST_USERNAME);
    user.setActive(true); // Set all required, non-nullable fields
    user.setRole(UserRoleEnum.USER);
    userRepository.save(user);
  }

  @Test
  void loginUser_ShouldReturnOkAndTokens_WhenCredentialsAreValid() throws Exception {
    LoginRequestDTO loginRequest = new LoginRequestDTO();
    loginRequest.setEmail(TEST_EMAIL);
    loginRequest.setPassword(TEST_PASSWORD);

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Login successful!"))
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  }

  @Test
  void loginUser_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
    LoginRequestDTO loginRequest = new LoginRequestDTO();
    loginRequest.setEmail("wrong@email.com");
    loginRequest.setPassword("wrongpassword");

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void refreshToken_ShouldReturnOk_WhenRefreshTokenIsValid() throws Exception {
    // Step 1: Login to get refresh token
    LoginRequestDTO loginRequest = new LoginRequestDTO();
    loginRequest.setEmail(TEST_EMAIL);
    loginRequest.setPassword(TEST_PASSWORD);

    String loginResponseJson =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String refreshToken = objectMapper.readTree(loginResponseJson).get("refreshToken").asText();

    // Step 2: Use refresh token to get new tokens
    RefreshTokenRequestDTO refreshTokenRequest = new RefreshTokenRequestDTO();
    refreshTokenRequest.setRefreshToken(refreshToken);

    mockMvc
        .perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Token refreshed successfully!"))
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  }

  @Test
  void refreshToken_ShouldReturnUnauthorized_WhenRefreshTokenIsInvalid() throws Exception {
    RefreshTokenRequestDTO refreshTokenRequest = new RefreshTokenRequestDTO();
    refreshTokenRequest.setRefreshToken("invalid-or-expired-token");

    mockMvc
        .perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void encodePassword_ShouldReturnEncodedPassword() throws Exception {
    String rawPassword = "mySecretPassword";

    mockMvc
        .perform(get("/api/auth/encode-password").param("password", rawPassword))
        .andExpect(status().isOk())
        .andExpect(
            result -> {
              String encoded = result.getResponse().getContentAsString();
              assertTrue(encoded != null && !encoded.isEmpty());
              assertTrue(passwordEncoder.matches(rawPassword, encoded));
            });
  }
}
