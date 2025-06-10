package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.AuthStrategyEnum;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = CazssBackendApplication.class)
public class EndpointsRepositoryTest {
  @Autowired UserRepository userRepository;

  @Autowired CategoryRepository categoryRepository;

  @Autowired EndpointsRepository endpointsRepository;

  @Autowired AuthenticationStrategyRepository authenticationStrategyRepository;

  public void setUpData() {
    categoryRepository.save(new CategoryEntity("TEST01", "#FFFFFF"));
    categoryRepository.save(new CategoryEntity("TEST02", "#000000"));

    userRepository.save(
        new UserEntity("endpointRepoTestUser01@autozone.com", true, UserRoleEnum.ADMIN));
    userRepository.save(
        new UserEntity("endpointRepoTestUser02@autozone.com", true, UserRoleEnum.ADMIN));
    authenticationStrategyRepository.save(
        new AuthenticationStrategyEntity("EndpointsRepositoryTest", AuthStrategyEnum.Bearer, null));
  }

  @Test
  @Transactional
  public void givenEndpointRepository_whenSaveAndRetreiveEndpoint_thenOK() {
    setUpData();

    Optional<CategoryEntity> categoryOptional = categoryRepository.findByName("TEST01");
    assertTrue(categoryOptional.isPresent(), "Category should be present");
    CategoryEntity category = categoryOptional.get();

    Optional<UserEntity> userOptional =
        userRepository.findByEmail("endpointRepoTestUser01@autozone.com");
    assertTrue(userOptional.isPresent(), "User should be present");
    UserEntity user = userOptional.get();

    Optional<AuthenticationStrategyEntity> authStrategyOptional =
        authenticationStrategyRepository.findByName("EndpointsRepositoryTest");
    assertTrue(authStrategyOptional.isPresent());
    AuthenticationStrategyEntity authStrategy = authStrategyOptional.get();

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "EndpointOne",
                "Endpoint description",
                EndpointMethodEnum.GET,
                "https://hello.example",
                authStrategy));

    Optional<EndpointsEntity> foundEndpointOptional =
        endpointsRepository.findById(endpoint.getEndpointId());

    assertTrue(foundEndpointOptional.isPresent(), "Endpoint should be found");

    EndpointsEntity foundEndpoint = foundEndpointOptional.get();
    assertEquals(endpoint, foundEndpoint);
  }

  @Test
  @Transactional
  public void givenEndpointRepository_whenUpdateEndpoint_thenOK() {
    setUpData();

    Optional<CategoryEntity> categoryOptional = categoryRepository.findByName("TEST02");
    assertTrue(categoryOptional.isPresent(), "Category should be present");
    CategoryEntity category = categoryOptional.get();

    Optional<UserEntity> userOptional =
        userRepository.findByEmail("endpointRepoTestUser02@autozone.com");
    assertTrue(userOptional.isPresent(), "User should be present");
    UserEntity user = userOptional.get();

    Optional<AuthenticationStrategyEntity> authStrategyOptional =
        authenticationStrategyRepository.findByName("EndpointsRepositoryTest");
    assertTrue(authStrategyOptional.isPresent());
    AuthenticationStrategyEntity authStrategy = authStrategyOptional.get();

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "EndpointTwo",
                "Endpoint description",
                EndpointMethodEnum.GET,
                "https://autozone.example",
                authStrategy));

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
  @Transactional
  public void givenEndpointRepository_whenDeleteEndpoint_thenOK() {
    setUpData();

    Optional<CategoryEntity> categoryOptional = categoryRepository.findByName("TEST02");
    assertTrue(categoryOptional.isPresent(), "Category should be present");
    CategoryEntity category = categoryOptional.get();

    Optional<UserEntity> userOptional =
        userRepository.findByEmail("endpointRepoTestUser01@autozone.com");
    assertTrue(userOptional.isPresent(), "User should be present");
    UserEntity user = userOptional.get();

    Optional<AuthenticationStrategyEntity> authStrategyOptional =
        authenticationStrategyRepository.findByName("EndpointsRepositoryTest");
    assertTrue(authStrategyOptional.isPresent());
    AuthenticationStrategyEntity authStrategy = authStrategyOptional.get();

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "EndpointToDelete",
                "Endpoint to delete",
                EndpointMethodEnum.GET,
                "https://autozone.example",
                authStrategy));

    Integer endpointId = endpoint.getEndpointId();

    endpointsRepository.deleteById(endpointId);

    Optional<EndpointsEntity> foundEndpointOptional = endpointsRepository.findById(endpointId);

    assertFalse(foundEndpointOptional.isPresent(), "Endpoint should be deleted");
  }
}
