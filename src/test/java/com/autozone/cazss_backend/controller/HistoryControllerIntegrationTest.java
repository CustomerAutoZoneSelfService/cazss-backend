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
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class HistoryControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private HistoryRepository historyRepository;

  @Autowired private HistoryDataRepository historyDataRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private EndpointsRepository endpointsRepository;

  @Autowired private CategoryRepository categoryRepository;

  private HistoryEntity savedHistory;

  private HistoryDataEntity savedHistoryDataEntityRequest;

  private HistoryDataEntity savedHistoryDataEntityResponse;

  public void setup() {
    HistoryEntity history = new HistoryEntity();
    UserEntity user = new UserEntity();
    EndpointsEntity endpoint = new EndpointsEntity();

    user.setActive(true);
    user.setEmail("prueba.12@example.com");
    user.setRole(UserRoleEnum.ADMIN);
    user = userRepository.save(user);

    CategoryEntity category = new CategoryEntity();
    category.setColor("red");
    category.setName("gets");
    category = categoryRepository.save(category);

    endpoint.setActive(true);
    endpoint.setCategory(category);
    endpoint.setMethod(EndpointMethodEnum.GET);
    endpoint.setDescription("Esto es una prueba de endpoint en History");
    endpoint.setName("Prueba de Endpoint");
    endpoint.setUrl("/test/url");
    endpoint = endpointsRepository.save(endpoint);

    history.setUser(user);
    history.setStatusCode(200);
    history.setCreatedAt(LocalDateTime.now());
    history.setEndpoint(endpoint);

    savedHistory = historyRepository.save(history);

    HistoryDataEntity historyDataEntityRequest = new HistoryDataEntity();
    historyDataEntityRequest.setHistory(savedHistory);
    historyDataEntityRequest.setType(HistoryDataTypeEnum.REQUEST);
    historyDataEntityRequest.setContent("Request test");
    savedHistoryDataEntityRequest = historyDataRepository.save(historyDataEntityRequest);

    HistoryDataEntity historyDataEntityResponse = new HistoryDataEntity();
    historyDataEntityResponse.setHistory(savedHistory);
    historyDataEntityResponse.setType(HistoryDataTypeEnum.RESPONSE);
    historyDataEntityResponse.setContent("Response test");
    savedHistoryDataEntityResponse = historyDataRepository.save(historyDataEntityResponse);
  }

  @Transactional
  @Test
  public void testGetAllHistory() throws Exception {
    setup();
    System.out.println("The saved history ID is the following:");
    System.out.println(savedHistory.getHistoryId());

    mockMvc
        .perform(get("/services/history").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].historyId").value(savedHistory.getHistoryId()))
        .andExpect(jsonPath("$[0].email").value(savedHistory.getUser().getEmail()))
        .andExpect(jsonPath("$[0].endpointName").value(savedHistory.getEndpoint().getName()))
        .andExpect(
            jsonPath("$[0].endpointDescription")
                .value(savedHistory.getEndpoint().getDescription()));
  }

  @Transactional
  @Test
  public void testGetServiceById() throws Exception {
    setup();
    System.out.println("The saved history ID is the following:");
    System.out.println(savedHistory.getHistoryId());

    mockMvc
        .perform(
            get("/services/history/{id}", savedHistory.getHistoryId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.historyId").value(savedHistory.getHistoryId()))
        .andExpect(jsonPath("$.statusCode").value(savedHistory.getStatusCode()))
        .andExpect(
            jsonPath("$.endpoint.endpointId").value(savedHistory.getEndpoint().getEndpointId()))
        .andExpect(jsonPath("$.endpoint.name").value(savedHistory.getEndpoint().getName()))
        .andExpect(
            jsonPath("$.endpoint.description").value(savedHistory.getEndpoint().getDescription()))
        .andExpect(
            jsonPath("$.historyData.request").value(savedHistoryDataEntityRequest.getContent()))
        .andExpect(
            jsonPath("$.historyData.response").value(savedHistoryDataEntityResponse.getContent()));
  }
}
