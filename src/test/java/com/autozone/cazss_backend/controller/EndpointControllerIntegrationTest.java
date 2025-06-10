// package com.autozone.cazss_backend.controller;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import com.autozone.cazss_backend.DTO.CreateServiceDTO;
// import com.autozone.cazss_backend.entity.CategoryEntity;
// import com.autozone.cazss_backend.entity.EndpointsEntity;
// import com.autozone.cazss_backend.entity.UserEntity;
// import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
// import com.autozone.cazss_backend.enumerator.UserRoleEnum;
// import com.autozone.cazss_backend.repository.CategoryRepository;
// import com.autozone.cazss_backend.repository.EndpointsRepository;
// import com.autozone.cazss_backend.repository.UserRepository;
// import com.autozone.cazss_backend.security.JwtUtil;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import java.util.Collections;
// import java.util.UUID;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// public class EndpointControllerIntegrationTest {
//
//  @Autowired private MockMvc mockMvc;
//
//  @Autowired private EndpointsRepository endpointsRepository;
//
//  @Autowired private UserRepository userRepository;
//
//  @Autowired private CategoryRepository categoryRepository;
//
//  @Autowired private ObjectMapper objectMapper;
//
//  @Autowired private JwtUtil jwtUtil;
//
//  private EndpointsEntity savedEndpoint;
//  private String authToken;
//  private UserEntity testUser;
//
//  @BeforeEach
//  public void setup() {
//    // Create test user
//    testUser = new UserEntity();
//    testUser.setUserId(90);
//    testUser.setActive(true);
//    testUser.setEmail("test@example.com");
//    testUser.setUsername("testuser");
//    testUser.setPassword("password");
//    testUser.setRole(UserRoleEnum.ADMIN);
//    testUser = userRepository.save(testUser);
//
//    // Create test category
//    CategoryEntity category = new CategoryEntity();
//    category.setColor("red");
//    category.setName("test-category");
//    category = categoryRepository.save(category);
//
//    // Create test endpoint
//    EndpointsEntity endpoint = new EndpointsEntity();
//    endpoint.setActive(true);
//    endpoint.setCategory(category);
//    endpoint.setMethod(EndpointMethodEnum.GET);
//    endpoint.setDescription("Test endpoint");
//    endpoint.setName("Test Endpoint");
//    endpoint.setUrl("/test/url");
//    savedEndpoint = endpointsRepository.save(endpoint);
//
//    // Generate JWT token
//    UserDetails userDetails =
//        new User(
//            String.valueOf(testUser.getUserId()),
//            testUser.getPassword(),
//            testUser.getActive(),
//            true,
//            true,
//            true,
//            Collections.singletonList(
//                new SimpleGrantedAuthority("ROLE_" + testUser.getRole().name())));
//    authToken = jwtUtil.generateAccessToken(userDetails);
//  }
//
//  @Test
//  @Transactional
//  public void testGetAllEndpoints() throws Exception {
//    mockMvc
//        .perform(
//            get("/services")
//                .header("Authorization", "Bearer " + authToken)
//                .contentType(MediaType.APPLICATION_JSON))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$[0].endpointId").value(savedEndpoint.getEndpointId()))
//        .andExpect(jsonPath("$[0].name").value(savedEndpoint.getName()))
//        .andExpect(jsonPath("$[0].description").value(savedEndpoint.getDescription()));
//  }
//
//  @Test
//  @Transactional
//  public void testGetEndpointById() throws Exception {
//    mockMvc
//        .perform(
//            get("/services/" + savedEndpoint.getEndpointId())
//                .header("Authorization", "Bearer " + authToken)
//                .contentType(MediaType.APPLICATION_JSON))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.name").value(savedEndpoint.getName()))
//        .andExpect(jsonPath("$.description").value(savedEndpoint.getDescription()));
//  }
//
//  @Test
//  @Transactional
//  public void testCreateEndpoint() throws Exception {
//    // Use existing test user and generate new token
//    UserDetails userDetails =
//        new User(
//            String.valueOf(testUser.getUserId()),
//            testUser.getPassword(),
//            testUser.getActive(),
//            true,
//            true,
//            true,
//            Collections.singletonList(
//                new SimpleGrantedAuthority("ROLE_" + testUser.getRole().name())));
//    String authToken = jwtUtil.generateAccessToken(userDetails);
//
//    CreateServiceDTO createServiceDTO = new CreateServiceDTO();
//    createServiceDTO.setName("New Test Endpoint");
//    createServiceDTO.setDescription("New Test Description");
//    createServiceDTO.setMethod(EndpointMethodEnum.GET);
//    createServiceDTO.setUrl("/new-test-url");
//    createServiceDTO.setCategoryId(savedEndpoint.getCategory().getCategoryId());
//    createServiceDTO.setActive(true);
//
//    mockMvc
//        .perform(
//            post("/services")
//                .header("Authorization", "Bearer " + authToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(createServiceDTO)))
//        .andExpect(status().isCreated())
//        .andExpect(jsonPath("$.name").value(createServiceDTO.getName()))
//        .andExpect(jsonPath("$.description").value(createServiceDTO.getDescription()));
//  }
// }
