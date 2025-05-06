package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.UserCategoryEntity;
import com.autozone.cazss_backend.entity.UserCategoryEntity.UserCategoryId;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
public class UserCategoryRepositoryTest {

  @Autowired private UserCategoryRepository userCategoryRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private CategoryRepository categoryRepository;

  private UserCategoryEntity createSampleUserCategory() {
    String uniqueEmail = "usercat+" + System.currentTimeMillis() + "@autozone.com";
    UserEntity user = userRepository.save(new UserEntity(uniqueEmail, true, UserRoleEnum.USER));
    CategoryEntity category =
        categoryRepository.save(
            new CategoryEntity("UserCat_" + System.currentTimeMillis(), "#FFDDEE"));

    UserCategoryId id = new UserCategoryId(user.getUserId(), category.getCategoryId());
    return userCategoryRepository.save(new UserCategoryEntity(id, user, category));
  }

  @Test
  public void givenUserCategoryRepository_whenSaveAndFind_thenOK() {
    UserCategoryEntity saved = createSampleUserCategory();

    Optional<UserCategoryEntity> foundOptional = userCategoryRepository.findById(saved.getId());
    assertTrue(foundOptional.isPresent(), "UserCategory should be found");

    UserCategoryEntity found = foundOptional.get();
    assertEquals(saved.getId(), found.getId());
    assertEquals(saved.getUser().getEmail(), found.getUser().getEmail());
    assertEquals(saved.getCategory().getName(), found.getCategory().getName());
  }

  @Test
  public void givenUserCategoryRepository_whenDelete_thenOK() {
    UserCategoryEntity saved = createSampleUserCategory();
    UserCategoryId id = saved.getId();

    userCategoryRepository.deleteById(id);

    Optional<UserCategoryEntity> foundOptional = userCategoryRepository.findById(id);
    assertFalse(foundOptional.isPresent(), "UserCategory should be deleted");
  }
}
