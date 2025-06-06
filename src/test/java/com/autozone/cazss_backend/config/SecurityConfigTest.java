package com.autozone.cazss_backend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.autozone.cazss_backend.repository.*;
import com.autozone.cazss_backend.security.JwtFilter;
import com.autozone.cazss_backend.security.JwtUtil;
import com.autozone.cazss_backend.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// Usamos WebMvcTest para testear la capa web (controladores y configuración de seguridad)
// Especificamos SecurityConfig para asegurarnos de que se cargue nuestra configuración.
@WebMvcTest
@Import({SecurityConfig.class})
class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  // Mockeamos las dependencias de SecurityConfig y JWTFilter
  // ya que no queremos testear su lógica interna aquí, sino la configuración de seguridad.
  @MockitoBean private JwtUtil jwtUtil;

  @MockitoBean private UserDetailsService userDetailsService; // Requerido por SecurityConfig

  @MockitoBean private JwtFilter jwtFilter; // Corregido: JwtFilter con j minúscula

  @MockitoBean private AuthService authService;
  @MockitoBean private HistoryService historyService;
  @MockitoBean private EndpointService endpointService;
  @MockitoBean private ResponsePatternService responsePatternService;
  @MockitoBean private UserFilterService userFilterService;
  @MockitoBean private RequestVariableService requestVariableService;
  @MockitoBean private ResponseService responseService;
  @MockitoBean private EndpointsRepository endpointsRepository;
  @MockitoBean private UserRepository userRepository;
  @MockitoBean private UserFilterRepository userFilterRepository;

  @Test
  void publicAuthEndpoints_shouldBeAccessibleWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/api/auth/login")).andExpect(status().isOk());
  }

  @Test
  void swaggerUiEndpoints_shouldBeAccessibleWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
  }

  @Test
  void protectedEndpoint_shouldReturnUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/api/services")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  void protectedEndpoint_shouldBeAccessibleWithMockAuthentication() throws Exception {
    mockMvc.perform(get("/api/services")).andExpect(status().isOk());
  }

  // Nota: Probar el flujo completo con un token JWT real requeriría no mockear JWTFilter y JwtUtil,
  // y tener un UserDetailsService funcional.
  // Estos tests se centran en la configuración de rutas permitidas/restringidas a un nivel básico.
}
