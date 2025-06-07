package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CategoryDTO;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.UserCategoryEntity;
import com.autozone.cazss_backend.exceptions.CategoryNotFoundException;
import com.autozone.cazss_backend.exceptions.UnauthorizedUserException;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.util.PermissionValidator;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private UserCategoryRepository userCategoryRepository;
  @Autowired private EndpointsRepository endpointsRepository;
  @Autowired private PermissionValidator permissionValidator;

  public List<CategoryDTO> getAllCategories() {
    return new ArrayList<CategoryDTO>();
  }

  public List<CategoryDTO> getCategoriesByUserId(Integer userId) {
    System.out.println("Checking categories for userId: " + userId);

    if (permissionValidator.isAdmin(userId)) {
      System.out.println("User is admin, returning all categories");
      return categoryRepository.findAll().stream()
          .map(category -> new CategoryDTO(category))
          .collect(Collectors.toList());
    } else {
      System.out.println("User is NOT admin, fetching user categories");
      List<UserCategoryEntity> userCategories = userCategoryRepository.findByUser_UserId(userId);
      System.out.println("User categories found: " + userCategories.size());
      List<CategoryEntity> categories =
          userCategories.stream()
              .map(UserCategoryEntity::getCategory)
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
      return categories.stream().map(CategoryDTO::new).collect(Collectors.toList());
    }
  }

  public List<CategoryDTO> getUserCategories(Integer userId) {
    return new ArrayList<CategoryDTO>();
  }

  public List<CategoryDTO> getAvailableCategories(Integer userId) {
    System.out.println("Getting available categories for userId: " + userId);
    List<CategoryDTO> categories = getCategoriesByUserId(userId);
    System.out.println("Categories found: " + categories.size());
    return categories;
  }

  @Transactional
  public CategoryDTO createCategory(Integer userId, CategoryDTO categoryDTO) {
    return new CategoryDTO();
  }

  @Transactional
  public CategoryDTO updateCategory(Integer userId, Integer categoryId, CategoryDTO categoryDTO) {
    return new CategoryDTO();
  }

  @Transactional
  public String deleteCategory(Integer userId, Integer categoryId) {
    if (permissionValidator.isAdmin(userId)) {
      if (categoryRepository.existsById(categoryId)) {
        // Delete user access to the categoryId
        userCategoryRepository.deleteByCategory_CategoryId(categoryId);

        // Fetch endpoints with the specified categoryId
        List<EndpointsEntity> foundEndpoints =
            endpointsRepository.findByCategory_CategoryId(categoryId);

        // Replace every found endpoint's categoryId with null
        foundEndpoints.forEach(
            endpointsEntity -> {
              endpointsEntity.setCategory(null);
              endpointsRepository.save(endpointsEntity);
            });

        // Delete the category (finally)
        categoryRepository.deleteByCategoryId(categoryId);
      } else {
        throw new CategoryNotFoundException("No category found");
      }
    } else {
      throw new UnauthorizedUserException("This feature is only available to administrators.");
    }
    return "Category deleted successfully";
  }
}
