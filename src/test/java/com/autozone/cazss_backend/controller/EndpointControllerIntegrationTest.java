package com.autozone.cazss_backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.repository.EndpointsRepository;
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

  @Autowired private EndpointsRepository endpointsRepository;

  private EndpointsEntity savedEndpoint;

  public void setup() {
    // Create and save a test endpoint into the real database
    EndpointsEntity endpoint = new EndpointsEntity();
    endpoint.setName("EndpointControllerIntegrationTestEndpoint");
    endpoint.setDescription("This is a test endpoint");
    endpoint.setActive(true);
    endpoint.setMethod(EndpointMethodEnum.GET);
    endpoint.setUrl("/test/url");

    savedEndpoint = endpointsRepository.save(endpoint);
  }

  @Transactional
  @Test
  public void testGetServiceById() throws Exception {
    setup();
    System.out.println("The saved endpoint id is the following:");
    System.out.println(savedEndpoint.getEndpointId());

    mockMvc
        .perform(get("/services/{id}", savedEndpoint.getEndpointId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("EndpointControllerIntegrationTestEndpoint"))
        .andExpect(jsonPath("$.description").value("This is a test endpoint"))
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.method").value("GET"))
        .andExpect(jsonPath("$.url").value("/test/url"));
  }

  @Test
  void testGetServiceById_NotFound() throws Exception {
    mockMvc
        .perform(get("/services/{id}", 9999)) // ID that doesn't exist
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
  public void testGetAllServices() throws Exception {
    setup();
    System.out.println("The result from getting all services is the following");
    System.out.println(mockMvc.perform(get("/services").contentType(MediaType.APPLICATION_JSON)));

    mockMvc
        .perform(get("/services").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("EndpointControllerIntegrationTestEndpoint"))
        .andExpect(jsonPath("$[0].description").value("This is a test endpoint"))
        .andExpect(jsonPath("$[0].endpointId").exists());
  }
}
