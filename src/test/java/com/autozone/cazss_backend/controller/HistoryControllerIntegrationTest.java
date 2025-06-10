package com.autozone.cazss_backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.HistoryDataEntity;
import com.autozone.cazss_backend.entity.HistoryEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.HistoryDataTypeEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.HistoryDataRepository;
import com.autozone.cazss_backend.repository.HistoryRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import com.autozone.cazss_backend.security.JwtUtil;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
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
@Transactional
public class HistoryControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private HistoryRepository historyRepository;

  @Autowired private HistoryDataRepository historyDataRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private EndpointsRepository endpointsRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private JwtUtil jwtUtil;

  private HistoryEntity savedHistory;
  private UserEntity testUser;

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
    category.setColor("red" + UUID.randomUUID());
    category.setName("test-category" + UUID.randomUUID());
    category = categoryRepository.save(category);

    // Create test endpoint
    EndpointsEntity endpoint = new EndpointsEntity();
    endpoint.setActive(true);
    endpoint.setCategory(category);
    endpoint.setMethod(EndpointMethodEnum.GET);
    endpoint.setDescription("Test endpoint" + UUID.randomUUID());
    endpoint.setName("Test Endpoint" + UUID.randomUUID());
    endpoint.setUrl("/test/url" + UUID.randomUUID());
    endpoint = endpointsRepository.save(endpoint);

    // Create test history
    HistoryEntity history = new HistoryEntity();
    history.setUser(testUser);
    history.setEndpoint(endpoint);
    history.setStatusCode(200);
    history.setCreatedAt(LocalDateTime.now());
    savedHistory = historyRepository.save(history);

    // Create test history data
    HistoryDataEntity historyDataEntityRequest = new HistoryDataEntity();
    historyDataEntityRequest.setHistory(savedHistory);
    historyDataEntityRequest.setType(HistoryDataTypeEnum.REQUEST);
    historyDataEntityRequest.setContent("{\"id\": 1}");
    historyDataRepository.save(historyDataEntityRequest);

    HistoryDataEntity historyDataEntityResponse = new HistoryDataEntity();
    historyDataEntityResponse.setHistory(savedHistory);
    historyDataEntityResponse.setType(HistoryDataTypeEnum.RESPONSE);
    historyDataEntityResponse.setContent("{\"result\": \"ok\"}");
    historyDataRepository.save(historyDataEntityResponse);
  }

  @Test
  public void testGetAllHistory() throws Exception {
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

    mockMvc
        .perform(
            get("/services/history")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].historyId").value(savedHistory.getHistoryId()))
        .andExpect(jsonPath("$[0].email").value(savedHistory.getUser().getEmail()))
        .andExpect(jsonPath("$[0].endpointName").value(savedHistory.getEndpoint().getName()))
        .andExpect(
            jsonPath("$[0].endpointDescription")
                .value(savedHistory.getEndpoint().getDescription()));
  }

  @Test
  public void testGetServiceById() throws Exception {
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

    mockMvc
        .perform(
            get("/services/history/{id}", savedHistory.getHistoryId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.historyId").value(savedHistory.getHistoryId()))
        .andExpect(jsonPath("$.statusCode").value(savedHistory.getStatusCode()))
        .andExpect(
            jsonPath("$.endpoint.endpointId").value(savedHistory.getEndpoint().getEndpointId()))
        .andExpect(jsonPath("$.endpoint.name").value(savedHistory.getEndpoint().getName()))
        .andExpect(
            jsonPath("$.endpoint.description").value(savedHistory.getEndpoint().getDescription()))
        .andExpect(jsonPath("$.historyData.request").isMap())
        .andExpect(jsonPath("$.historyData.response").isMap());
  }
}
