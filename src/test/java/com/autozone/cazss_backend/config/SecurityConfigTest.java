package com.autozone.cazss_backend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.autozone.cazss_backend.security.JwtFilter;
import com.autozone.cazss_backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

// Usamos WebMvcTest para testear la capa web (controladores y configuración de seguridad)
// Especificamos SecurityConfig para asegurarnos de que se cargue nuestra configuración.
@WebMvcTest
@Import(SecurityConfig.class) // Importar explícitamente SecurityConfig
class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  // Mockeamos las dependencias de SecurityConfig y JWTFilter
  // ya que no queremos testear su lógica interna aquí, sino la configuración de seguridad.
  @MockBean private JwtUtil jwtUtil;

  @MockBean private UserDetailsService userDetailsService; // Requerido por SecurityConfig

  @MockBean private JwtFilter jwtFilter; // Corregido: JwtFilter con j minúscula

  @Test
  void publicAuthEndpoints_shouldBeAccessibleWithoutAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/auth/login"))
        .andExpect(status().isNotFound()); // Esperamos 404 porque no hay un controlador real para
    // /api/auth/login
    // pero el test es para ver si la ruta NO es 401/403 por seguridad.
    mockMvc.perform(get("/api/auth/refresh")).andExpect(status().isNotFound()); // Idem
  }

  @Test
  void swaggerUiEndpoints_shouldBeAccessibleWithoutAuthentication() throws Exception {
    mockMvc
        .perform(get("/swagger-ui/index.html"))
        .andExpect(status().isOk()); // Swagger UI debería estar disponible
    mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk()); // OpenAPI docs también
  }

  @Test
  void protectedEndpoint_shouldReturnUnauthorizedWithoutAuthentication() throws Exception {
    // Un endpoint genérico que no está en las reglas de "permitAll"
    mockMvc
        .perform(get("/api/some-protected-resource"))
        .andExpect(
            status()
                .isUnauthorized()); // O .isForbidden() dependiendo de la configuración exacta del
    // filtro mockeado
    // Como JWTFilter está mockeado, es probable que devuelva 401 por defecto si no hay auth.
  }

  @Test
  @WithMockUser // Simula un usuario autenticado (muy básico, sin roles específicos por ahora)
  void protectedEndpoint_shouldBeAccessibleWithMockAuthentication() throws Exception {
    // Cuando hay un usuario autenticado (incluso mockeado), y JWTFilter está mockeado (no bloquea),
    // la solicitud debería llegar al dispatcher, que devolverá 404 si no hay un handler.
    // El objetivo es ver que no sea 401/403.
    mockMvc
        .perform(get("/api/some-protected-resource-authenticated"))
        .andExpect(status().isNotFound());
  }

  // Nota: Probar el flujo completo con un token JWT real requeriría no mockear JWTFilter y JwtUtil,
  // y tener un UserDetailsService funcional.
  // Estos tests se centran en la configuración de rutas permitidas/restringidas a un nivel básico.
}
