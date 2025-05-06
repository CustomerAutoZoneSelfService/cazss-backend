package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
public class ResponseRepositoryTest {

  @Autowired private ResponseRepository responseRepository;

  @Autowired private EndpointsRepository endpointsRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private UserRepository userRepository;

  private ResponseEntity createSampleResponse() {
    String uniqueSuffix = String.valueOf(System.currentTimeMillis());

    UserEntity user =
        userRepository.save(
            new UserEntity("response+" + uniqueSuffix + "@autozone.com", true, UserRoleEnum.USER));

    CategoryEntity category =
        categoryRepository.save(new CategoryEntity("RESPONSE_" + uniqueSuffix, "#C2F0C2"));

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "Sample Endpoint " + uniqueSuffix,
                "This is a test endpoint",
                EndpointMethodEnum.POST,
                "https://sample.endpoint"));

    ResponseEntity response = new ResponseEntity();
    response.setEndpoint(endpoint);
    response.setStatusCode(201);
    response.setDescription("Created");

    return responseRepository.save(response);
  }

  @Test
  public void givenResponseRepository_whenSaveAndFind_thenOK() {
    ResponseEntity saved = createSampleResponse();

    Optional<ResponseEntity> foundOpt = responseRepository.findById(saved.getResponseId());
    assertTrue(foundOpt.isPresent());

    ResponseEntity found = foundOpt.get();
    assertEquals(201, found.getStatusCode());
    assertEquals("Created", found.getDescription());
    assertEquals(saved.getEndpoint().getName(), found.getEndpoint().getName());
  }

  @Test
  public void givenResponseRepository_whenUpdate_thenOK() {
    ResponseEntity response = createSampleResponse();
    response.setDescription("Updated Description");
    response.setStatusCode(202);

    ResponseEntity updated = responseRepository.save(response);

    Optional<ResponseEntity> foundOpt = responseRepository.findById(updated.getResponseId());
    assertTrue(foundOpt.isPresent());
    assertEquals("Updated Description", foundOpt.get().getDescription());
    assertEquals(202, foundOpt.get().getStatusCode());
  }

  @Test
  public void givenResponseRepository_whenDelete_thenOK() {
    ResponseEntity response = createSampleResponse();
    Integer id = response.getResponseId();

    responseRepository.deleteById(id);

    Optional<ResponseEntity> found = responseRepository.findById(id);
    assertFalse(found.isPresent(), "Response should be deleted");
  }
}
