package com.autozone.cazss_backend.controller;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.DTO.CreateResponsePatternDTO;
import com.autozone.cazss_backend.security.JwtUtil;
import com.autozone.cazss_backend.service.ResponsePatternService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ResponsesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ResponsesControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ResponsePatternService responsePatternService;

  @MockitoBean
  private com.autozone.cazss_backend.service.UserDetailsServiceImpl
      userDetailsService; // <-- Add this

  @MockitoBean private JwtUtil jwtUtil;

  @Test
  void whenPostValidPatterns_thenReturnsCreatedAndPatternsList() throws Exception {
    // Arrange
    int responseId = 1;
    String jsonPayload = "[{\"pattern\":\"foo.*\"}]";

    CreateResponsePatternDTO dto = new CreateResponsePatternDTO();
    dto.setPattern("foo.*");
    List<CreateResponsePatternDTO> mockResponse = Collections.singletonList(dto);

    when(responsePatternService.addPatterns(eq(responseId), anyList())).thenReturn(mockResponse);

    // Act & Assert
    mockMvc
        .perform(
            post("/responses/{id}/response-patterns", responseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$[0].pattern").value("foo.*"));
    verify(responsePatternService).addPatterns(eq(responseId), anyList());
  }
}
