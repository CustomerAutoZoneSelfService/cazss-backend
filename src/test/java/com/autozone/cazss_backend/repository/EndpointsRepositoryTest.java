package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
public class EndpointsRepositoryTest {
  @Autowired UserRepository userRepository;

  @Autowired CategoryRepository categoryRepository;

  @Autowired EndpointsRepository endpointsRepository;

  @BeforeEach
  public void setup() {
    // Limpiamos todo antes de cada test para evitar datos duplicados o conflictos
    endpointsRepository.deleteAll();
    userRepository.deleteAll();
    categoryRepository.deleteAll();

    // Crear categorías
    categoryRepository.save(new CategoryEntity("TEST", "#FFFFFF"));
    categoryRepository.save(new CategoryEntity("TEST2", "#000000"));

    // Crear usuarios
    userRepository.save(new UserEntity("johndoe@autozone.com", true, UserRoleEnum.ADMIN));
    userRepository.save(new UserEntity("newemail@autozone.com", true, UserRoleEnum.ADMIN));
  }

  @Test
  public void givenEndpointRepository_whenSaveAndRetreiveEndpoint_thenOK() {
    Optional<CategoryEntity> categoryOptional = categoryRepository.findByName("TEST");
    assertTrue(categoryOptional.isPresent(), "Category should be present");
    CategoryEntity category = categoryOptional.get();

    Optional<UserEntity> userOptional = userRepository.findByEmail("johndoe@autozone.com");
    assertTrue(userOptional.isPresent(), "User should be present");
    UserEntity user = userOptional.get();

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "EndpointOne",
                "Endpoint description",
                EndpointMethodEnum.GET,
                "https://hello.example"));

    Optional<EndpointsEntity> foundEndpointOptional =
        endpointsRepository.findById(endpoint.getEndpointId());

    assertTrue(foundEndpointOptional.isPresent(), "Endpoint should be found");

    EndpointsEntity foundEndpoint = foundEndpointOptional.get();
    assertEquals(endpoint, foundEndpoint);
  }

  @Test
  public void givenEndpointRepository_whenUpdateEndpoint_thenOK() {
    Optional<CategoryEntity> categoryOptional = categoryRepository.findByName("TEST2");
    assertTrue(categoryOptional.isPresent(), "Category should be present");
    CategoryEntity category = categoryOptional.get();

    Optional<UserEntity> userOptional = userRepository.findByEmail("newemail@autozone.com");
    assertTrue(userOptional.isPresent(), "User should be present");
    UserEntity user = userOptional.get();

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "EndpointTwo",
                "Endpoint description",
                EndpointMethodEnum.GET,
                "https://autozone.example"));

    // Modificar datos
    endpoint.setActive(false);
    endpoint.setName("EndpointAZ2");
    endpoint.setDescription("New Description");
    endpoint.setMethod(EndpointMethodEnum.POST);
    endpoint.setUrl("https://datazone.example");

    EndpointsEntity updatedEndpoint = endpointsRepository.save(endpoint);

    Optional<EndpointsEntity> foundEndpointOptional =
        endpointsRepository.findById(updatedEndpoint.getEndpointId());

    assertTrue(foundEndpointOptional.isPresent(), "Updated endpoint should be found");

    EndpointsEntity foundEndpoint = foundEndpointOptional.get();

    assertEquals("EndpointAZ2", foundEndpoint.getName());
    assertEquals("New Description", foundEndpoint.getDescription());
    assertFalse(foundEndpoint.getActive());
    assertEquals(EndpointMethodEnum.POST, foundEndpoint.getMethod());
    assertEquals("https://datazone.example", foundEndpoint.getUrl());
  }

  @Test
  public void givenEndpointRepository_whenDeleteEndpoint_thenOK() {
    Optional<CategoryEntity> categoryOptional = categoryRepository.findByName("TEST2");
    assertTrue(categoryOptional.isPresent(), "Category should be present");
    CategoryEntity category = categoryOptional.get();

    Optional<UserEntity> userOptional = userRepository.findByEmail("newemail@autozone.com");
    assertTrue(userOptional.isPresent(), "User should be present");
    UserEntity user = userOptional.get();

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "EndpointToDelete",
                "Endpoint to delete",
                EndpointMethodEnum.GET,
                "https://autozone.example"));

    Integer endpointId = endpoint.getEndpointId();

    endpointsRepository.deleteById(endpointId);

    Optional<EndpointsEntity> foundEndpointOptional = endpointsRepository.findById(endpointId);

    assertFalse(foundEndpointOptional.isPresent(), "Endpoint should be deleted");
  }
}
