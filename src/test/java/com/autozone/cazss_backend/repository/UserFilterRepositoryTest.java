package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
public class UserFilterRepositoryTest {

  @Autowired private UserFilterRepository userFilterRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private ResponsePatternRepository responsePatternRepository;

  @Autowired private CategoryRepository categoryRepository;

  private UserEntity user;
  private CategoryEntity category;
  private ResponsePatternEntity responsePattern;

  @BeforeEach
  void setUp() {
    String timestamp = String.valueOf(System.currentTimeMillis());

    // Create user with all required fields
    user =
        new UserEntity(
            "test" + timestamp + "@autozone.com",
            true,
            UserRoleEnum.USER,
            "testPassword123",
            "testUser" + timestamp);
    user = userRepository.save(user);

    // Create category with unique name
    category = new CategoryEntity();
    category.setName("Test Category " + timestamp);
    category.setColor("#FFFFFF");
    category = categoryRepository.save(category);

    // Create response pattern
    responsePattern = new ResponsePatternEntity();
    responsePattern.setName("Test Pattern");
    responsePattern.setDescription("Test Pattern Description");
    responsePattern.setPattern("ValidPattern123");
    responsePattern.setParentId(null);
    responsePattern.setIsLeaf(true);
    responsePattern = responsePatternRepository.save(responsePattern);
  }

  @Test
  public void givenUserFilterRepository_whenSavedAndRetrieved_thenOK() {
    // Create UserFilter with appropriate values
    UserFilterEntity userFilter = new UserFilterEntity();

    // Create composite key
    UserFilterEntity.UserFilterId userFilterId =
        new UserFilterEntity.UserFilterId(user.getUserId(), responsePattern.getResponsePatternId());
    userFilter.setId(userFilterId);

    // Assign user and response pattern
    userFilter.setUser(user);
    userFilter.setResponsePattern(responsePattern);

    // Save to repository
    userFilter = userFilterRepository.save(userFilter);

    // Verify it was saved correctly
    assertNotNull(userFilter.getId());
    assertEquals(user.getUserId(), userFilter.getUser().getUserId());
    assertEquals(
        responsePattern.getResponsePatternId(),
        userFilter.getResponsePattern().getResponsePatternId());
  }

  @Test
  public void givenUserFilterRepository_whenUpdated_thenOK() {
    // Create and save a UserFilter
    UserFilterEntity userFilter = new UserFilterEntity();
    UserFilterEntity.UserFilterId userFilterId =
        new UserFilterEntity.UserFilterId(user.getUserId(), responsePattern.getResponsePatternId());
    userFilter.setId(userFilterId);
    userFilter.setUser(user);
    userFilter.setResponsePattern(responsePattern);
    userFilter = userFilterRepository.save(userFilter);

    // Update response pattern name
    responsePattern.setName("Updated Pattern Name");

    // Save updated response pattern
    responsePattern = responsePatternRepository.save(responsePattern);

    // Verify pattern was updated correctly
    assertEquals("Updated Pattern Name", responsePattern.getName());

    // Verify UserFilter entity also reflects the update
    UserFilterEntity updatedUserFilter =
        userFilterRepository.findById(userFilter.getId()).orElseThrow();
    assertEquals("Updated Pattern Name", updatedUserFilter.getResponsePattern().getName());
  }

  @Test
  public void givenUserFilterRepository_whenDeleted_thenOK() {
    // Create and save a UserFilter
    UserFilterEntity userFilter = new UserFilterEntity();
    UserFilterEntity.UserFilterId userFilterId =
        new UserFilterEntity.UserFilterId(user.getUserId(), responsePattern.getResponsePatternId());
    userFilter.setId(userFilterId);
    userFilter.setUser(user);
    userFilter.setResponsePattern(responsePattern);
    userFilter = userFilterRepository.save(userFilter);

    // Delete the UserFilter
    userFilterRepository.delete(userFilter);

    // Verify it was deleted correctly
    assertFalse(userFilterRepository.existsById(userFilter.getId()));
  }
}
