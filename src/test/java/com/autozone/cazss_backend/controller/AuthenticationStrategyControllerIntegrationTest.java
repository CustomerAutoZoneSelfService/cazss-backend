package com.autozone.cazss_backend.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.DTO.AuthenticationStrategyAttributeDTO;
import com.autozone.cazss_backend.DTO.AuthenticationStrategyDTO;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.UserRepository;
import com.autozone.cazss_backend.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
public class AuthenticationStrategyControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private JwtUtil jwtUtil;
  @Autowired private ObjectMapper objectMapper;
  private String authToken;

  private AuthenticationStrategyDTO buildTestStrategy(String name) {
    AuthenticationStrategyDTO dto = new AuthenticationStrategyDTO();
    dto.setName(name);
    dto.setType("Bearer");

    AuthenticationStrategyAttributeDTO attr = new AuthenticationStrategyAttributeDTO();
    attr.setKey("key1");
    attr.setValue("value1");

    dto.setAttributes(List.of(attr));
    return dto;
  }

  @Test
  public void testCreateAndFetchStrategy() throws Exception {
    AuthenticationStrategyDTO strategy = buildTestStrategy("Integration Strategy");
    UserEntity testUser;

    // Create test user
    testUser = new UserEntity();
    testUser.setActive(true);
    testUser.setEmail("test@example.com");
    testUser.setUsername("testuser");
    testUser.setPassword("password");
    testUser.setRole(UserRoleEnum.ADMIN);
    testUser = userRepository.save(testUser);

    // Generate JWT token
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

    // Crear
    String response =
        mockMvc
            .perform(
                post("/authentication-strategies")
                    .header("Authorization", "Bearer " + authToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(strategy)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Integration Strategy")))
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Obtener ID generado
    AuthenticationStrategyDTO created =
        objectMapper.readValue(response, AuthenticationStrategyDTO.class);
    Integer createdId = created.getAuthStrategyId();

    // Consultar por ID
    mockMvc
        .perform(
            get("/authentication-strategies/" + createdId)
                .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is("Integration Strategy")))
        .andExpect(jsonPath("$.authStrategyId", is(createdId)));
  }

  @Test
  public void testUpdateStrategy() throws Exception {
    AuthenticationStrategyDTO strategy = buildTestStrategy("To Update");

    UserEntity testUser;

    // Create test user
    testUser = new UserEntity();
    testUser.setActive(true);
    testUser.setEmail("test@example.com");
    testUser.setUsername("testuser");
    testUser.setPassword("password");
    testUser.setRole(UserRoleEnum.ADMIN);
    testUser = userRepository.save(testUser);

    // Generate JWT token
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

    // Crear primero
    String response =
        mockMvc
            .perform(
                post("/authentication-strategies")
                    .header("Authorization", "Bearer " + authToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(strategy)))
            .andReturn()
            .getResponse()
            .getContentAsString();

    AuthenticationStrategyDTO created =
        objectMapper.readValue(response, AuthenticationStrategyDTO.class);
    Integer createdId = created.getAuthStrategyId();

    // Cambiar nombre
    created.setName("Updated Strategy");

    // Actualizar
    mockMvc
        .perform(
            put("/authentication-strategies/" + createdId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(created)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is("Updated Strategy")));
  }

  @Test
  public void testGetAllStrategies() throws Exception {
    // Crear dos estrategias
    AuthenticationStrategyDTO strategy1 = buildTestStrategy("Strategy 1");
    AuthenticationStrategyDTO strategy2 = buildTestStrategy("Strategy 2");

    UserEntity testUser;

    // Create test user
    testUser = new UserEntity();
    testUser.setActive(true);
    testUser.setEmail("test@example.com");
    testUser.setUsername("testuser");
    testUser.setPassword("password");
    testUser.setRole(UserRoleEnum.ADMIN);
    testUser = userRepository.save(testUser);

    // Generate JWT token
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

    mockMvc
        .perform(
            post("/authentication-strategies")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(strategy1)))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/authentication-strategies")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(strategy2)))
        .andExpect(status().isOk());

    // Consultar todas
    mockMvc
        .perform(get("/authentication-strategies").header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));
  }
}
