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
public class ResponsePatternRepositoryTest {

  @Autowired private ResponsePatternRepository responsePatternRepository;

  @Autowired private ResponseRepository responseRepository;

  @Autowired private EndpointsRepository endpointsRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private CategoryRepository categoryRepository;

  private ResponsePatternEntity createSampleResponsePattern() {
    String uniqueSuffix = String.valueOf(System.currentTimeMillis());

    UserEntity user =
        userRepository.save(
            new UserEntity("pattern+" + uniqueSuffix + "@autozone.com", true, UserRoleEnum.USER));

    CategoryEntity category =
        categoryRepository.save(new CategoryEntity("PATTERN_" + uniqueSuffix, "#FAFAFA"));

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "Pattern Endpoint " + uniqueSuffix,
                "Pattern Endpoint Description",
                EndpointMethodEnum.GET,
                "https://pattern.endpoint"));

    ResponseEntity response =
        responseRepository.save(new ResponseEntity(null, endpoint, 200, "OK Response"));

    ResponsePatternEntity pattern = new ResponsePatternEntity();
    pattern.setResponse(response);
    pattern.setPattern("$.data.items[*].name");
    pattern.setName("Item Name Pattern " + uniqueSuffix);
    pattern.setDescription("Extracts item names from response");
    pattern.setParentId(null);
    pattern.setIsLeaf(true);

    return responsePatternRepository.save(pattern);
  }

  @Test
  public void givenResponsePatternRepository_whenSaveAndFind_thenOK() {
    ResponsePatternEntity saved = createSampleResponsePattern();

    Optional<ResponsePatternEntity> foundOpt =
        responsePatternRepository.findById(saved.getResponsePatternId());
    assertTrue(foundOpt.isPresent());

    ResponsePatternEntity found = foundOpt.get();
    assertEquals("$.data.items[*].name", found.getPattern());
    assertEquals(saved.getName(), found.getName());
    assertEquals("Extracts item names from response", found.getDescription());
    assertEquals(200, found.getResponse().getStatusCode());
  }

  @Test
  public void givenResponsePatternRepository_whenUpdate_thenOK() {
    ResponsePatternEntity pattern = createSampleResponsePattern();
    pattern.setPattern("$.data.items[*].id");

    ResponsePatternEntity updated = responsePatternRepository.save(pattern);

    Optional<ResponsePatternEntity> foundOpt =
        responsePatternRepository.findById(updated.getResponsePatternId());
    assertTrue(foundOpt.isPresent());
    assertEquals("$.data.items[*].id", foundOpt.get().getPattern());
  }

  @Test
  public void givenResponsePatternRepository_whenDelete_thenOK() {
    ResponsePatternEntity pattern = createSampleResponsePattern();
    Integer id = pattern.getResponsePatternId();

    responsePatternRepository.deleteById(id);

    Optional<ResponsePatternEntity> found = responsePatternRepository.findById(id);
    assertFalse(found.isPresent(), "Pattern should be deleted");
  }
}
