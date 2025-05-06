package com.autozone.cazss_backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EndpointControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private EndpointsRepository endpointsRepository;

  private EndpointsEntity savedEndpoint;

  @BeforeEach
  public void setup() {
    // Clean database before each test to avoid conflicts
    endpointsRepository.deleteAll();

    // Create and save a test endpoint into the real database
    EndpointsEntity endpoint = new EndpointsEntity();
    endpoint.setName("Test Service");
    endpoint.setDescription("This is a test endpoint");
    endpoint.setActive(true);
    endpoint.setMethod(EndpointMethodEnum.POST);
    endpoint.setUrl("/test/url");

    savedEndpoint = endpointsRepository.save(endpoint);
  }

  @Test
  public void testGetServiceById() throws Exception {
    mockMvc
        .perform(post("/services/getServiceById/{id}", savedEndpoint.getEndpointId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test Service"))
        .andExpect(jsonPath("$.description").value("This is a test endpoint"))
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.method").value("POST"))
        .andExpect(jsonPath("$.url").value("/test/url"));
  }

  @Test
  public void testHandleEndpointNotFound() throws Exception {
    mockMvc
        .perform(post("/services/getServiceById/{id}", 9999)) // ID that doesn't exist
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Endpoint with ID 9999 not found"));
  }

  @Test
  public void testGetAllServices() throws Exception {
    mockMvc
        .perform(post("/services/getAllServices").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Test Service"))
        .andExpect(jsonPath("$[0].description").value("This is a test endpoint"))
        .andExpect(jsonPath("$[0].endpointId").exists());
  }
}
