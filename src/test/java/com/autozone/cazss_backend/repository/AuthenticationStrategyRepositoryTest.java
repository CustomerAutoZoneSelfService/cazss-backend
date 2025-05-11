package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.AuthenticationStrategyEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.enumerator.AuthStrategyEnum;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
public class AuthenticationStrategyRepositoryTest {
  @Autowired EndpointsRepository endpointsRepository;

  @Autowired AuthenticationStrategyRepository authenticationStrategyRepository;

  @Test
  @Transactional
  public void
      givenAuthenticationStrategyRepository_whenSaveAndRetrieveAuthenticationStrategy_thenOK() {
    // Crear el endpoint manualmente
    EndpointsEntity endpoint = new EndpointsEntity();
    endpoint.setName("EndpointOneAuthRepoTest");
    endpoint.setDescription("Test endpoint description");
    endpoint.setMethod(EndpointMethodEnum.GET);
    endpoint.setUrl("http://localhost/test");
    endpoint.setActive(true);

    endpointsRepository.save(endpoint);

    Optional<EndpointsEntity> endpointOptional =
        endpointsRepository.findByName("EndpointOneAuthRepoTest");
    assertTrue(endpointOptional.isPresent(), "Endpoint should be present");

    EndpointsEntity savedEndpoint = endpointOptional.get();

    AuthenticationStrategyEntity authenticationStrategy =
        authenticationStrategyRepository.save(
            new AuthenticationStrategyEntity(
                savedEndpoint, AuthStrategyEnum.Bearer, new ArrayList<>()));

    Optional<AuthenticationStrategyEntity> foundAuthStrategyOptional =
        authenticationStrategyRepository.findById(authenticationStrategy.getAuthStrategyId());

    assertTrue(foundAuthStrategyOptional.isPresent(), "Auth strategy should be found");

    AuthenticationStrategyEntity foundAuthStrategy = foundAuthStrategyOptional.get();
    assertEquals(authenticationStrategy, foundAuthStrategy);
  }
}
