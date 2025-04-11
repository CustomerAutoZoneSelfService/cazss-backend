package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CazssBackendApplication.class)
public class EndpointsRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    EndpointsRepository endpointsRepository;

    @Test
    public void givenEndpointRepository_whenSaveAndRetreiveEndpoint_thenOK(){
        Optional<CategoryEntity> categoryOptional = categoryRepository.findByName("TEST");
        assertTrue(categoryOptional.isPresent(), "Category should be present");
        CategoryEntity category = categoryOptional.get();

        Optional<UserEntity> userOptional = userRepository.findByEmail("johndoe@autozone.com");
        assertTrue(userOptional.isPresent(), "User should be present");
        UserEntity user = userOptional.get();

        EndpointsEntity endpoint = endpointsRepository
                .save(new EndpointsEntity(
                        category,
                        user,
                        true,
                        "EndpointOne",
                        "Endpoint description",
                        EndpointMethodEnum.GET,
                        "https://hello.example"));

        Optional<EndpointsEntity> foundEndpointOptional = endpointsRepository.findById(endpoint.getEndpointId());

        assertTrue(foundEndpointOptional.isPresent(), "Endpoint should be found");

        EndpointsEntity foundEndpoint = foundEndpointOptional.get();
        assertEquals(endpoint, foundEndpoint);

    }

    @Test
    public void givenEndpointRepository_whenUpdateEndpoint_thenOK(){
        Optional<CategoryEntity> categoryOptional = categoryRepository.findByName("TEST2");
        assertTrue(categoryOptional.isPresent(), "Category should be present");
        CategoryEntity category = categoryOptional.get();

        Optional<UserEntity> userOptional = userRepository.findByEmail("newemail@autozone.com");
        assertTrue(userOptional.isPresent(), "User should be present");
        UserEntity user = userOptional.get();

        EndpointsEntity endpoint = endpointsRepository
                .save(new EndpointsEntity(
                        category,
                        user,
                        true,
                        "EndpointTwo",
                        "Endpoint description",
                        EndpointMethodEnum.GET,
                        "https://autozone.example"));

        endpoint.setActive(false);
        endpoint.setName("EndpointAZ2");
        endpoint.setDescription("New Description");
        endpoint.setMethod(EndpointMethodEnum.POST);
        endpoint.setUrl("https://datazone.example");

        EndpointsEntity updatedEndpoint = endpointsRepository.save(endpoint);

        Optional<EndpointsEntity> foundEndpointOptional = endpointsRepository.findById(updatedEndpoint.getEndpointId());

        assertTrue(foundEndpointOptional.isPresent(), "Updated endpoint should be found");

        EndpointsEntity foundEndpoint = foundEndpointOptional.get();

        assertEquals("EndpointAZ2",foundEndpoint.getName());
        assertEquals("New Description",foundEndpoint.getDescription());
        assertEquals(false,foundEndpoint.getActive());
        assertEquals(EndpointMethodEnum.POST,foundEndpoint.getMethod());
        assertEquals("https://datazone.example",foundEndpoint.getUrl());

    }

    @Test
    public void givenEndpointRepository_whenDeleteEndpoint_thenOK(){
        Optional<CategoryEntity> categoryOptional = categoryRepository.findByName("TEST2");
        assertTrue(categoryOptional.isPresent(), "Category should be present");
        CategoryEntity category = categoryOptional.get();

        Optional<UserEntity> userOptional = userRepository.findByEmail("newemail@autozone.com");
        assertTrue(userOptional.isPresent(), "User should be present");
        UserEntity user = userOptional.get();

        EndpointsEntity endpoint = endpointsRepository
                .save(new EndpointsEntity(
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
