package com.autozone.cazss_backend.controller;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.DTO.CreateResponsePatternDTO;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.UserRepository;
import com.autozone.cazss_backend.security.JwtUtil;
import com.autozone.cazss_backend.service.ResponsePatternService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ResponsesControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private JwtUtil jwtUtil;

  @MockBean private ResponsePatternService responsePatternService;

  private String authToken;
  private UserEntity testUser;

  @BeforeEach
  public void setup() {
    // Create test user
    testUser = new UserEntity();
    testUser.setActive(true);
    testUser.setEmail("test@example.com");
    testUser.setUsername("testuser");
    testUser.setPassword("password");
    testUser.setRole(UserRoleEnum.ADMIN);
    testUser = userRepository.save(testUser);

    // Generate JWT token using userId as subject
    UserDetails userDetails =
        new User(
            String.valueOf(testUser.getUserId()),
            testUser.getPassword(),
            testUser.getActive(),
            true,
            true,
            true,
            Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + testUser.getRole().name())));
    authToken = jwtUtil.generateAccessToken(userDetails);
  }

  @Test
  void whenPostValidPatterns_thenReturnsCreatedAndPatternsList() throws Exception {
    // Arrange
    int responseId = 1;
    String jsonPayload = "[{\"pattern\":\"foo.*\"}]";

    CreateResponsePatternDTO dto = new CreateResponsePatternDTO();
    dto.setPattern("foo.*");
    List<CreateResponsePatternDTO> mockResponse = Collections.singletonList(dto);

    when(responsePatternService.addPatterns(eq(responseId), anyList())).thenReturn(mockResponse);

    // Act & Assert
    mockMvc
        .perform(
            post("/responses/{id}/response-patterns", responseId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$[0].pattern").value("foo.*"));
    verify(responsePatternService).addPatterns(eq(responseId), anyList());
  }
}
