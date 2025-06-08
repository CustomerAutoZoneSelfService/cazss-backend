package com.autozone.cazss_backend.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class EndpointControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private CategoryRepository categoryRepository;

  @MockitoBean private UserRepository userRepository;

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
        .perform(get("/services").contentType(MediaType.APPLICATION_JSON).header("userId", 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("EndpointControllerIntegrationTestEndpoint"))
        .andExpect(jsonPath("$[0].description").value("This is a test endpoint"))
        .andExpect(jsonPath("$[0].endpointId").exists());
  }

  @Transactional
  @Test
  public void testCreateService() throws Exception {
    CategoryEntity cat = new CategoryEntity();
    cat.setName("Test Category");
    cat.setColor("#FF0000");
    categoryRepository.save(cat);

    UserEntity usr = new UserEntity();

    usr.setEmail("test@example.com");
    given(userRepository.findById(90)).willReturn(Optional.of(usr));
    // usr.setUserId(90);
    // usr.setEmail("foo@bar.com");
    // userRepository.save(usr);

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
            .formatted(cat.getCategoryId());

    mockMvc
        .perform(post("/services").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.endpointId").isNumber())
        .andExpect(jsonPath("$.name").value("New Service"))
        .andExpect(jsonPath("$.description").value("Created via integration test"));
  }
}
