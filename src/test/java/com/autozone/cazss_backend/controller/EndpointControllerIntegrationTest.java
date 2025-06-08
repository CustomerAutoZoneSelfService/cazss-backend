package com.autozone.cazss_backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.UserCategoryEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class EndpointControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private EndpointsRepository endpointsRepository;

  @Autowired private UserCategoryRepository userCategoryRepository;

  private EndpointsEntity savedEndpoint;

  private UserEntity savedUser;

  private CategoryEntity savedCategory;

  private UserCategoryEntity savedUserCategory;

  public void setupUserCategory() {
    UserCategoryEntity userCategory = new UserCategoryEntity();
    userCategory.setCategory(savedCategory);
    userCategory.setUser(savedUser);
    userCategory.setId(
        new UserCategoryEntity.UserCategoryId(
            savedUser.getUserId(), savedCategory.getCategoryId()));
    savedUserCategory = userCategoryRepository.save(userCategory);
  }

  public void setupEndpoint(UserRoleEnum userRole) {
    // Create and save a test endpoint into the real database
    EndpointsEntity endpoint = new EndpointsEntity();
    endpoint.setName("EndpointControllerIntegrationTestEndpoint");
    endpoint.setDescription("This is a test endpoint");
    endpoint.setActive(true);
    if (userRole != UserRoleEnum.ADMIN) {
      endpoint.setCategory(savedCategory);
    }
    endpoint.setMethod(EndpointMethodEnum.GET);
    endpoint.setUrl("/test/url");

    savedEndpoint = endpointsRepository.save(endpoint);
  }

  public void setupUser(UserRoleEnum userRole) {
    UserEntity user = new UserEntity();

    user.setEmail("test@example.com");
    user.setRole(userRole);
    user.setActive(true);

    savedUser = userRepository.save(user);
  }

  public void setupCategory() {
    CategoryEntity category = new CategoryEntity();
    category.setName("EndpointControllerIntegrationTestCategory");
    category.setColor("#FF0000");
    savedCategory = categoryRepository.save(category);
  }

  @Transactional
  @Test
  public void testGetServiceById() throws Exception {
    setupUser(UserRoleEnum.USER);
    setupEndpoint(UserRoleEnum.USER);
    System.out.println("The saved endpoint id is the following:");
    System.out.println(savedEndpoint.getEndpointId());

    mockMvc
        .perform(
            get("/services/{id}", savedEndpoint.getEndpointId())
                .header("userId", savedUser.getUserId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("EndpointControllerIntegrationTestEndpoint"))
        .andExpect(jsonPath("$.description").value("This is a test endpoint"))
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.method").value("GET"))
        .andExpect(jsonPath("$.url").value("/test/url"));
  }

  @Transactional
  @Test
  void testGetServiceById_NotFound() throws Exception {
    setupUser(UserRoleEnum.USER);
    mockMvc
        .perform(
            get("/services/{id}", 9999)
                .header("userId", savedUser.getUserId())) // ID that doesn't exist
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Endpoint not found with id: 9999"))
        .andExpect(jsonPath("$.details").value("The requested resource was not found"))
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.traceId").isString())
        .andExpect(jsonPath("$.traceId").isNotEmpty());
  }

  @Transactional
  @Test
  public void testGetAvailableServicesAsAdmin() throws Exception {
    setupUser(UserRoleEnum.ADMIN);
    setupEndpoint(UserRoleEnum.ADMIN);
    System.out.println("The result from getting all services is the following");
    System.out.println(mockMvc.perform(get("/services").contentType(MediaType.APPLICATION_JSON)));

    mockMvc
        .perform(
            get("/services")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", savedUser.getUserId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("EndpointControllerIntegrationTestEndpoint"))
        .andExpect(jsonPath("$[0].description").value("This is a test endpoint"))
        .andExpect(jsonPath("$[0].endpointId").exists());
  }

  @Transactional
  @Test
  public void testGetAvailableServicesAsNormalUserWithNoEndpointAccess() throws Exception {
    setupUser(UserRoleEnum.USER);
    setupEndpoint(UserRoleEnum.ADMIN);
    System.out.println("The result from getting all services is the following");
    System.out.println(mockMvc.perform(get("/services").contentType(MediaType.APPLICATION_JSON)));

    mockMvc
        .perform(
            get("/services")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", savedUser.getUserId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Transactional
  @Test
  public void testGetAvailableServicesAsNormalUserWithEndpointAccess() throws Exception {
    setupUser(UserRoleEnum.USER);
    setupCategory();
    setupEndpoint(UserRoleEnum.USER);
    setupUserCategory();
    System.out.println("The result from getting all services is the following");
    System.out.println(
        mockMvc.perform(
            get("/services")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", savedUser.getUserId())));

    mockMvc
        .perform(
            get("/services")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", savedUser.getUserId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("EndpointControllerIntegrationTestEndpoint"))
        .andExpect(jsonPath("$[0].description").value("This is a test endpoint"))
        .andExpect(jsonPath("$[0].endpointId").exists());
  }

  @Transactional
  @Test
  public void testCreateService() throws Exception {
    setupUser(UserRoleEnum.ADMIN);
    setupCategory();

    String payload =
        """
    {
      "categoryId": %d,
      "name": "New Service",
      "description": "Created via integration test",
      "method": "GET",
      "url": "/test/create",
      "active": true,
      "template": null,
      "requestVariables": [],
      "responses": []
    }
    """
            .formatted(savedCategory.getCategoryId());

    mockMvc
        .perform(
            post("/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .header("userId", savedUser.getUserId()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.endpointId").isNumber())
        .andExpect(jsonPath("$.name").value("New Service"))
        .andExpect(jsonPath("$.description").value("Created via integration test"));
  }
}
