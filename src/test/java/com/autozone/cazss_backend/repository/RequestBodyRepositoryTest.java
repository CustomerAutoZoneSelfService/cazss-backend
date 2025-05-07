package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.RequestBodyEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RequestBodyRepositoryTest {
  @Autowired EndpointsRepository endpointsRepository;

  @Autowired RequestBodyRepository requestBodyRepository;

  @Test
  @Transactional
  public void givenRequestBodyRepository_whenSaveAndRetreiveRequestBody_thenOK() {
    // Creamos e insertamos un endpoint para que exista
    EndpointsEntity endpoint = new EndpointsEntity();
    endpoint.setName("RequestBodyTestEndpoint");
    endpoint.setDescription("Test endpoint for request body tests"); // Asignamos una descripción
    endpoint.setMethod(EndpointMethodEnum.GET);
    endpoint.setUrl("https://example.com/api/endpointOne");
    endpoint.setActive(true);
    endpointsRepository.save(endpoint);

    Optional<EndpointsEntity> endpointOptional =
        endpointsRepository.findByName("RequestBodyTestEndpoint");
    assertTrue(endpointOptional.isPresent(), "Endpoint should be present");

    EndpointsEntity savedEndpoint = endpointOptional.get();

    // Creamos un request body
    RequestBodyEntity requestBody =
        requestBodyRepository.save(new RequestBodyEntity(savedEndpoint, "<xml></xml>"));

    Optional<RequestBodyEntity> foundRequestBodyOptional =
        requestBodyRepository.findById(requestBody.getEndpointId());
    assertTrue(foundRequestBodyOptional.isPresent(), "Request body is present");

    RequestBodyEntity foundRequestBody = foundRequestBodyOptional.get();
    assertEquals(requestBody.getTemplate(), foundRequestBody.getTemplate());
    assertEquals(savedEndpoint.getEndpointId(), foundRequestBody.getEndpointId());
  }
}
