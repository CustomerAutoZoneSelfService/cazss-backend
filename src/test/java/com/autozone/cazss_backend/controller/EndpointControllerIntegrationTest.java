package com.autozone.cazss_backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.DTO.CreateServiceDTO;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.AuthStrategyEnum;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.AuthenticationStrategyRepository;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import com.autozone.cazss_backend.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest
@AutoConfigureMockMvc
public class EndpointControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private EndpointsRepository endpointsRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private AuthenticationStrategyRepository authenticationStrategyRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private JwtUtil jwtUtil;

  private EndpointsEntity savedEndpoint;
  private String authToken;
  private UserEntity testUser;
  private AuthenticationStrategyEntity testAuthStrategy;

  @BeforeEach
  public void setup() {
    // Create test user
    testUser = new UserEntity();
    testUser.setActive(true);
    testUser.setEmail("qwertest@example.com");
    testUser.setUsername("testuserasdf");
    testUser.setPassword("passwordeqwr");
    testUser.setRole(UserRoleEnum.ADMIN);
    testUser = userRepository.save(testUser);

    // Create test category
    CategoryEntity category = new CategoryEntity();
    category.setColor("red");
    category.setName("test-category");
    category = categoryRepository.save(category);

    // create test authentication strategy
    testAuthStrategy = new AuthenticationStrategyEntity();
    testAuthStrategy.setName("Test Auth Strategy");
    testAuthStrategy.setStrategy(AuthStrategyEnum.Bearer);
    AuthenticationStrategyAttributeEntity testAuthEntity =
        new AuthenticationStrategyAttributeEntity();
    testAuthEntity.setKeyName("Key");
    testAuthEntity.setValue("Value");
    testAuthEntity.setAuthStrategy(testAuthStrategy);
    List<AuthenticationStrategyAttributeEntity> testAuthEntities = new ArrayList<>();
    testAuthEntities.add(testAuthEntity);
    testAuthStrategy.setAttributes(testAuthEntities);
    testAuthStrategy = authenticationStrategyRepository.save(testAuthStrategy);

    // Create test endpoint
    EndpointsEntity endpoint = new EndpointsEntity();
    endpoint.setActive(true);
    endpoint.setCategory(category);
    endpoint.setMethod(EndpointMethodEnum.GET);
    endpoint.setDescription("Test endpoint");
    endpoint.setName("Test Endpoint");
    endpoint.setUrl("/test/url");
    savedEndpoint = endpointsRepository.save(endpoint);

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
  }

  @Test
  @Transactional
  public void testGetAllEndpoints() throws Exception {
    mockMvc
        .perform(
            get("/services")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$[0].category.categoryId").value(savedEndpoint.getCategory().getCategoryId()))
        .andExpect(jsonPath("$[0].category.name").value(savedEndpoint.getCategory().getName()))
        .andExpect(jsonPath("$[0].services[0].endpointId").value(savedEndpoint.getEndpointId()))
        .andExpect(jsonPath("$[0].services[0].name").value(savedEndpoint.getName()))
        .andExpect(jsonPath("$[0].services[0].description").value(savedEndpoint.getDescription()));
  }

  @Test
  @Transactional
  public void testGetEndpointById() throws Exception {
    mockMvc
        .perform(
            get("/services/" + savedEndpoint.getEndpointId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(savedEndpoint.getName()))
        .andExpect(jsonPath("$.description").value(savedEndpoint.getDescription()));
  }

  @Test
  @Transactional
  public void testCreateEndpoint() throws Exception {
    // Use existing test user and generate new token
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
    String authToken = jwtUtil.generateAccessToken(userDetails);

    CreateServiceDTO createServiceDTO = new CreateServiceDTO();
    createServiceDTO.setName("New Test Endpoint");
    createServiceDTO.setDescription("New Test Description");
    createServiceDTO.setMethod(EndpointMethodEnum.GET);
    createServiceDTO.setUrl("/new-test-url");
    createServiceDTO.setCategoryId(savedEndpoint.getCategory().getCategoryId());
    createServiceDTO.setActive(true);
    createServiceDTO.setAuthenticationStrategy(testAuthStrategy.getAuthStrategyId());

    mockMvc
        .perform(
            post("/services")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createServiceDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value(createServiceDTO.getName()))
        .andExpect(jsonPath("$.description").value(createServiceDTO.getDescription()));
  }
}
