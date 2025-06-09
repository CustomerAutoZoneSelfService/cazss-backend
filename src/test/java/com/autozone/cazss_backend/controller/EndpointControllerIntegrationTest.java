package com.autozone.cazss_backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.DTO.CreateServiceDTO;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import com.autozone.cazss_backend.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
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

  @Autowired private ObjectMapper objectMapper;

  @Autowired private JwtUtil jwtUtil;

  private EndpointsEntity savedEndpoint;
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

    // Create test category
    CategoryEntity category = new CategoryEntity();
    category.setColor("red");
    category.setName("test-category");
    category = categoryRepository.save(category);

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
            testUser.getEmail(),
            testUser.getPassword(),
            testUser.getActive(),
            true,
            true,
            true,
            Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + testUser.getRole().name())));
    authToken = jwtUtil.generateAccessToken(userDetails);
  }

  @Transactional
  @Test
  public void testGetServiceById() throws Exception {
    mockMvc
        .perform(
            get("/services/{id}", savedEndpoint.getEndpointId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedEndpoint.getEndpointId()))
        .andExpect(jsonPath("$.name").value(savedEndpoint.getName()))
        .andExpect(jsonPath("$.description").value(savedEndpoint.getDescription()));
  }

  @Transactional
  @Test
  public void testGetServiceById_NotFound() throws Exception {
    mockMvc
        .perform(
            get("/services/999")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Transactional
  @Test
  public void testGetAllServices() throws Exception {
    mockMvc
        .perform(
            get("/services")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].endpointId").value(savedEndpoint.getEndpointId()))
        .andExpect(jsonPath("$[0].name").value(savedEndpoint.getName()))
        .andExpect(jsonPath("$[0].description").value(savedEndpoint.getDescription()));
  }

  @Transactional
  @Test
  public void testCreateService() throws Exception {
    CategoryEntity category = new CategoryEntity();
    category.setColor("red");
    category.setName("new-category");
    category = categoryRepository.save(category);

    CreateServiceDTO newService = new CreateServiceDTO();
    newService.setCategoryId(category.getCategoryId());
    newService.setActive(true);
    newService.setMethod(EndpointMethodEnum.GET);
    newService.setDescription("New test endpoint");
    newService.setName("New Test Endpoint");
    newService.setUrl("/test/new-url");
    newService.setTemplate(null);
    newService.setRequestVariables(Collections.emptyList());
    newService.setResponses(Collections.emptyList());

    mockMvc
        .perform(
            post("/services")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newService)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.endpointId").exists())
        .andExpect(jsonPath("$.name").value(newService.getName()))
        .andExpect(jsonPath("$.description").value(newService.getDescription()));
  }
}
