package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.RequestBodyEntity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CazssBackendApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RequestBodyRepositoryTest {
    @Autowired
    EndpointsRepository endpointsRepository;

    @Autowired
    RequestBodyRepository requestBodyRepository;

    @Test
    @Transactional
    @Order(10)
    public void givenRequestBodyRepository_whenSaveAndRetreiveRequestBody_thenOK() {
        Optional<EndpointsEntity> endpointOptional = endpointsRepository.findByName("EndpointOne");
        assertTrue(endpointOptional.isPresent(), "Endpoint should be present");
        EndpointsEntity endpoint = endpointOptional.get();

        RequestBodyEntity requestBody = requestBodyRepository
                .save(new RequestBodyEntity(endpoint,"<xml></xml>"));

        Optional<RequestBodyEntity> foundRequestBodyOptional = requestBodyRepository.findById(requestBody.getEndpointId());

        assertTrue(foundRequestBodyOptional.isPresent(), "Request body is present");

        RequestBodyEntity foundRequestBody = foundRequestBodyOptional.get();
        assertEquals(requestBody, foundRequestBody);
    }

    @Test
    @Transactional
    @Order(11)
    public void givenRequestBodyRepository_whenUpdateRequestBody_thenOK(){
        Optional<EndpointsEntity> endpointOptional = endpointsRepository.findByName("EndpointAZ2");
        assertTrue(endpointOptional.isPresent(), "Endpoint should be present");
        EndpointsEntity endpoint = endpointOptional.get();

        RequestBodyEntity requestBody = requestBodyRepository
                .save(new RequestBodyEntity(endpoint,"<xml></xml>"));

        requestBody.setTemplate("<>Hello<>");
        RequestBodyEntity updatedRequestBody = requestBodyRepository.save(requestBody);
        Optional<RequestBodyEntity> foundRequestBodyOptional = requestBodyRepository.findById(updatedRequestBody.getEndpointId());
        assertTrue(foundRequestBodyOptional.isPresent(), "Updated endpoint should be found");
        RequestBodyEntity foundRequestBody = foundRequestBodyOptional.get();
        assertEquals(endpoint.getEndpointId(),foundRequestBody.getEndpointId());

    }


}
