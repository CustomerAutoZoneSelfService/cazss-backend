package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.AuthStrategyEnum;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthenticationStrategyAttributeRepositoryTest {

  @Autowired
  private AuthenticationStrategyAttributeRepository authenticationStrategyAttributeRepository;

  @Autowired private AuthenticationStrategyRepository authenticationStrategyRepository;

  @Autowired private EndpointsRepository endpointsRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private CategoryRepository categoryRepository;

  private UserEntity user;
  private CategoryEntity category;
  private EndpointsEntity endpoint;
  private AuthenticationStrategyEntity authStrategy;

  public void setUp() {
    // Crear un correo electrónico único para evitar duplicidad
    String email = "test" + System.currentTimeMillis() + "@example.com";

    // Crear usuario
    user = new UserEntity();
    user.setEmail(email);
    user.setActive(true);
    user.setRole(UserRoleEnum.USER); // Asegúrate de tener el Enum definido
    user = userRepository.save(user);

    // Crear categoría
    category = new CategoryEntity();
    category.setName("Test Category");
    category.setColor("#FFFFFF");
    category = categoryRepository.save(category);

    // Crear endpoint
    endpoint = new EndpointsEntity();
    endpoint.setUser(user);
    endpoint.setCategory(category);
    endpoint.setActive(true);
    endpoint.setName("Test Endpoint");
    endpoint.setDescription("Test Endpoint Description");
    endpoint.setMethod(EndpointMethodEnum.GET); // Asegúrate de que tienes este enum
    endpoint.setUrl("https://example.com");
    endpoint = endpointsRepository.save(endpoint);

    // Crear estrategia de autenticación
    authStrategy = new AuthenticationStrategyEntity();
    authStrategy.setEndpoint(endpoint);
    authStrategy.setStrategy(AuthStrategyEnum.Bearer);
    authStrategy = authenticationStrategyRepository.save(authStrategy);
  }

  @Test
  @Transactional
  public void givenAuthenticationStrategyAttributeRepository_whenSavedAndRetrieved_thenOK() {
    setUp();

    // Crear atributo de estrategia de autenticación
    AuthenticationStrategyAttributeEntity attribute = new AuthenticationStrategyAttributeEntity();
    attribute.setAuthStrategy(authStrategy);
    attribute.setKeyName("Authorization");
    attribute.setValue("Bearer some-token");

    // Guardar el atributo en el repositorio
    AuthenticationStrategyAttributeEntity savedAttribute =
        authenticationStrategyAttributeRepository.save(attribute);

    // Verificar que el atributo se ha guardado correctamente
    assertNotNull(savedAttribute.getAuthStrategyAttributeId());
    assertEquals("Authorization", savedAttribute.getKeyName());
    assertEquals("Bearer some-token", savedAttribute.getValue());

    // Recuperar el atributo desde el repositorio
    AuthenticationStrategyAttributeEntity retrievedAttribute =
        authenticationStrategyAttributeRepository
            .findById(savedAttribute.getAuthStrategyAttributeId())
            .orElse(null);

    // Verificar que el atributo recuperado es el mismo que el guardado
    assertNotNull(retrievedAttribute);
    assertEquals(
        savedAttribute.getAuthStrategyAttributeId(),
        retrievedAttribute.getAuthStrategyAttributeId());
    assertEquals("Authorization", retrievedAttribute.getKeyName());
    assertEquals("Bearer some-token", retrievedAttribute.getValue());
  }
}
