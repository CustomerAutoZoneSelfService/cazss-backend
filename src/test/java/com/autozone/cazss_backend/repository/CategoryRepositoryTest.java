package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.CategoryEntity;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
public class CategoryRepositoryTest {
  @Autowired CategoryRepository categoryRepository;

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

  @Test
  public void givenCategoryRepository_whenDeleteCategory_thenOK() {
    CategoryEntity category = categoryRepository.save(new CategoryEntity("AUTOZONE", "#FFFFF"));

    Integer categoryId = category.getCategoryId();

    categoryRepository.deleteById(categoryId);

    Optional<CategoryEntity> foundCategoryOptional = categoryRepository.findById(categoryId);

    assertFalse(foundCategoryOptional.isPresent(), "Category should be deleted");
  }
}
