package com.autozone.cazss_backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.UserCategoryEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerIntegrationTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private EndpointsRepository endpointsRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserCategoryRepository userCategoryRepository;

  @Transactional
  @Test
  public void testDeleteCategory() throws Exception {
    UserEntity userToSave = new UserEntity();
    CategoryEntity categoryToSave = new CategoryEntity();
    UserCategoryEntity userCategoryToSave = new UserCategoryEntity();
    EndpointsEntity endpointToSave = new EndpointsEntity();
    UserCategoryEntity.UserCategoryId userCategoryId = new UserCategoryEntity.UserCategoryId();

    userToSave.setEmail("CategoryControllerIntegration" + UUID.randomUUID() + "@autozone.com");
    userToSave.setActive(true);
    userToSave.setRole(UserRoleEnum.ADMIN);
    userRepository.save(userToSave);

    System.out.println("My user ID is " + userToSave.getUserId());

    categoryToSave.setName("CategoryControllerIntegration" + UUID.randomUUID());
    categoryToSave.setColor("FFFFFF");
    categoryRepository.save(categoryToSave);

    userCategoryId.setCategoryId(categoryToSave.getCategoryId());
    userCategoryId.setUserId(userToSave.getUserId());
    userCategoryToSave.setCategory(categoryToSave);
    userCategoryToSave.setUser(userToSave);
    userCategoryToSave.setId(userCategoryId);
    userCategoryRepository.save(userCategoryToSave);

    endpointToSave.setCategory(categoryToSave);
    endpointToSave.setRequestBody(null);
    endpointToSave.setResponses(null);
    endpointToSave.setMethod(EndpointMethodEnum.GET);
    endpointToSave.setActive(true);
    endpointToSave.setUser(userToSave);
    endpointToSave.setUrl("categorycontrollerurl" + UUID.randomUUID());
    endpointToSave.setName("Category Controller Integration" + UUID.randomUUID());
    endpointToSave.setDescription("Category controller integration" + UUID.randomUUID());
    endpointsRepository.save(endpointToSave);

    Optional<CategoryEntity> foundCategory =
        categoryRepository.findByCategoryId(categoryToSave.getCategoryId());

    assertTrue(foundCategory.isPresent());

    System.out.println("The category to search is " + categoryToSave.getCategoryId());

    mockMvc
        .perform(
            delete("/categories/{categoryId}", categoryToSave.getCategoryId())
                .header("userId", userToSave.getUserId()))
        .andExpect(status().isOk());

    Optional<EndpointsEntity> postDeleteFoundEndpoint =
        endpointsRepository.findById(endpointToSave.getEndpointId());

    assertTrue(postDeleteFoundEndpoint.isPresent());

    Optional<UserCategoryEntity> postDeleteFoundUserCategory =
        userCategoryRepository.findByCategory_CategoryIdAndUser_UserId(
            userToSave.getUserId(), categoryToSave.getCategoryId());

    Optional<CategoryEntity> postDeleteCategoryEntity =
        categoryRepository.findById(categoryToSave.getCategoryId());

    assertTrue(postDeleteCategoryEntity.isEmpty());

    assertTrue(postDeleteFoundUserCategory.isEmpty());
  }
}
