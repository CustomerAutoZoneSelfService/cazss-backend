package com.autozone.cazss_backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

  @Transactional
  @Test
  public void testGetCategoriesByUser() throws Exception {
    // Crea usuario admin
    UserEntity adminUser = new UserEntity();
    adminUser.setEmail("adminGetTest@autozone.com");
    adminUser.setActive(true);
    adminUser.setRole(UserRoleEnum.ADMIN);
    userRepository.save(adminUser);

    // Crea usuario normal
    UserEntity normalUser = new UserEntity();
    normalUser.setEmail("normalGetTest@autozone.com");
    normalUser.setActive(true);
    normalUser.setRole(UserRoleEnum.USER);
    userRepository.save(normalUser);

    // Crea categorías y asigna a usuario normal
    CategoryEntity cat1 = new CategoryEntity();
    cat1.setName("Category1");
    cat1.setColor("#FF0000");
    categoryRepository.save(cat1);

    CategoryEntity cat2 = new CategoryEntity();
    cat2.setName("Category2");
    cat2.setColor("#00FF00");
    categoryRepository.save(cat2);

    UserCategoryEntity userCat = new UserCategoryEntity();
    UserCategoryEntity.UserCategoryId userCategoryId = new UserCategoryEntity.UserCategoryId();
    userCategoryId.setUserId(normalUser.getUserId());
    userCategoryId.setCategoryId(cat1.getCategoryId());
    userCat.setId(userCategoryId);
    userCat.setUser(normalUser);
    userCat.setCategory(cat1);
    userCategoryRepository.save(userCat);

    // Test para admin - debería obtener ambas categorías
    mockMvc
        .perform(get("/categories").header("userId", adminUser.getUserId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));

    // Test para usuario normal - debería obtener solo una categoría
    mockMvc
        .perform(get("/categories").header("userId", normalUser.getUserId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1));
  }
}
