package com.autozone.cazss_backend.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.DTO.AuthenticationStrategyAttributeDTO;
import com.autozone.cazss_backend.DTO.AuthenticationStrategyDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
public class AuthenticationStrategyControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

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

    // Crear
    String response =
        mockMvc
            .perform(
                post("/authentication-strategies")
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
        .perform(get("/authentication-strategies/" + createdId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is("Integration Strategy")))
        .andExpect(jsonPath("$.authStrategyId", is(createdId)));
  }

  @Test
  public void testUpdateStrategy() throws Exception {
    AuthenticationStrategyDTO strategy = buildTestStrategy("To Update");

    // Crear primero
    String response =
        mockMvc
            .perform(
                post("/authentication-strategies")
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

    mockMvc
        .perform(
            post("/authentication-strategies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(strategy1)))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/authentication-strategies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(strategy2)))
        .andExpect(status().isOk());

    // Consultar todas
    mockMvc
        .perform(get("/authentication-strategies"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));
  }
}
