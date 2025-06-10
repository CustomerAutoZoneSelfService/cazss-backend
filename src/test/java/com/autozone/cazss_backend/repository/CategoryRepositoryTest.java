package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.CategoryEntity;
import jakarta.transaction.Transactional;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
@Transactional
public class CategoryRepositoryTest {
  @Autowired CategoryRepository categoryRepository;
  @Autowired private EndpointsRepository endpointsRepository;

  @Transactional
  @Test
  public void givenCategoryRepository_whenSaveAndRetrieveCategory_thenOK() {
    CategoryEntity category =
        categoryRepository.save(new CategoryEntity("TEST_CATEGORY_NORMAL", "#FFFFF"));

    Optional<CategoryEntity> foundCategoryOptional =
        categoryRepository.findById(category.getCategoryId());

    // Assert
    assertTrue(foundCategoryOptional.isPresent(), "Category should be found");

    CategoryEntity foundCategory = foundCategoryOptional.get();
    assertEquals(category, foundCategory);
  }

  @Transactional
  @Test
  public void givenCategoryRepository_whenUpdateCategory_thenOK() {
    CategoryEntity category = categoryRepository.save(new CategoryEntity("HELLO WORLD", "#FFFFF"));

    category.setName("TEST2");
    category.setColor("#00000");

    CategoryEntity updatedCategory = categoryRepository.save(category);

    Optional<CategoryEntity> foundCategoryOptional =
        categoryRepository.findById(updatedCategory.getCategoryId());

    assertTrue(foundCategoryOptional.isPresent(), "Updated category should be found");

    CategoryEntity foundCategory = foundCategoryOptional.get();
    assertEquals("TEST2", foundCategory.getName());
    assertEquals("#00000", foundCategory.getColor());
  }

  @Transactional
  @Test
  public void givenCategoryRepository_whenDeleteCategory_thenOK() {
    CategoryEntity category = categoryRepository.save(new CategoryEntity("AUTOZONE", "#FFFFF"));

    Integer categoryId = category.getCategoryId();

    categoryRepository.deleteById(categoryId);

    Optional<CategoryEntity> foundCategoryOptional = categoryRepository.findById(categoryId);

    assertFalse(foundCategoryOptional.isPresent(), "Category should be deleted");
  }

  @Test
  @Transactional
  public void givenCategoryRepository_whenFindByCategoryIdIn_thenOK() {
    ArrayList<CategoryEntity> savedCategories = new ArrayList<>();
    Set<Integer> categoryIds = new HashSet<>();

    for (int currentCategoryNumber = 0; currentCategoryNumber < 6; currentCategoryNumber++) {
      CategoryEntity category =
          categoryRepository.save(
              new CategoryEntity("findByCategoryId" + UUID.randomUUID(), "#FFFFF"));
      savedCategories.add(category);
      categoryIds.add(category.getCategoryId());
    }

    List<CategoryEntity> foundCategories =
        categoryRepository.findByCategoryIdIn((Set<Integer>) categoryIds);

    savedCategories.forEach(categoryEntity -> System.out.println(categoryEntity.getCategoryId()));

    assertEquals(savedCategories, foundCategories);

    for (int i = 0; i < savedCategories.size(); i++) {
      assertEquals(savedCategories.get(i), foundCategories.get(i));
    }

    // Optional<CategoryEntity> foundCategoryOptional = categoryRepository.findById(categoryId);

    // assertFalse(foundCategoryOptional.isPresent(), "Category should be deleted");
  }
}
