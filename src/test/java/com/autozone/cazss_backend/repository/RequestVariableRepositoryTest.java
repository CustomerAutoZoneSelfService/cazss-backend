package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.AuthStrategyEnum;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.RequestVariableTypeEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import java.util.Optional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = CazssBackendApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RequestVariableRepositoryTest {

  @Autowired EndpointsRepository endpointsRepository;

  @Autowired RequestVariableRepository requestVariableRepository;

  @Autowired CategoryRepository categoryRepository;

  @Autowired UserRepository userRepository;

  @Autowired AuthenticationStrategyRepository authenticationStrategyRepository;

  @Test
  @Transactional
  public void givenRequestVariableRepository_whenSaveAndRetreiveRequestVariable_thenOK() {
    UserEntity user =
        userRepository.save(
            new UserEntity(
                "danagtz@autozone.com", true, UserRoleEnum.USER, "testPassword123", "danagtz"));

    Optional<UserEntity> foundUserOptional = userRepository.findById(user.getUserId());

    // Assert
    assertTrue(foundUserOptional.isPresent(), "User should be found");

    CategoryEntity category =
        categoryRepository.save(new CategoryEntity("MY_NEW_CATEGORY", "#FFFFF"));

    Optional<CategoryEntity> foundCategoryOptional =
        categoryRepository.findById(category.getCategoryId());

    // Assert
    assertTrue(foundCategoryOptional.isPresent(), "Category should be found");

    AuthenticationStrategyEntity authStrategy =
        authenticationStrategyRepository.save(
            new AuthenticationStrategyEntity(
                "RequestVariableRepositoryTest", AuthStrategyEnum.Bearer, null));

    Optional<AuthenticationStrategyEntity> foundAuthStrategyOptional =
        authenticationStrategyRepository.findByName(authStrategy.getName());

    // Assert
    assertTrue(foundAuthStrategyOptional.isPresent(), "Authentication strategy should be found");

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "EndpointDANA",
                "Endpoint description",
                EndpointMethodEnum.GET,
                "https://hello.example",
                authStrategy));

    Optional<EndpointsEntity> foundEndpointOptional =
        endpointsRepository.findById(endpoint.getEndpointId());

    assertTrue(foundEndpointOptional.isPresent(), "Endpoint should be found");

    RequestVariableEntity requestVaraible =
        requestVariableRepository.save(
            new RequestVariableEntity(
                endpoint, RequestVariableTypeEnum.BODY, "id", "", true, "Id description"));

    Optional<RequestVariableEntity> foundRequestVariableEntityOptional =
        requestVariableRepository.findById(requestVaraible.getRequestVariableId());

    assertTrue(foundRequestVariableEntityOptional.isPresent(), "Request variable is present");

    RequestVariableEntity foundRequestVariable = foundRequestVariableEntityOptional.get();
    assertEquals(requestVaraible, foundRequestVariable);
  }
}
