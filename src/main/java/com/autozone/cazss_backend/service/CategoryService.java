package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CategoryDTO;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.UserCategoryEntity;
import com.autozone.cazss_backend.exceptions.CategoryAlreadyExistsException;
import com.autozone.cazss_backend.exceptions.CategoryNotFoundException;
import com.autozone.cazss_backend.exceptions.UnauthorizedUserException;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.util.PermissionValidator;
import jakarta.transaction.Transactional;
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
    System.out.println("User is admin, returning all categories");
    return categoryRepository.findAllCategoryDTOs();
  }

  public List<CategoryDTO> getUserSpecificCategories(String userEmail) {
    System.out.println("Getting available categories for userEmail: " + userEmail);
    List<UserCategoryEntity> foundUserCategoryPermissions =
        userCategoryRepository.findByUser_Email(userEmail);
    System.out.println("Categories found: " + foundUserCategoryPermissions.size());
    List<CategoryEntity> foundUserAccessibleCategories =
        foundUserCategoryPermissions.stream()
            .map(UserCategoryEntity::getCategory)
            .filter(Objects::nonNull)
            .toList();
    return foundUserAccessibleCategories.stream()
        .map(CategoryDTO::new)
        .collect(Collectors.toList());
  }

  public List<CategoryDTO> getAvailableCategories(String userEmail) {
    System.out.println("Checking categories for userEmail: " + userEmail);
    if (permissionValidator.isAdmin(userEmail)) {
      return getAllCategories();
    } else {
      return getUserSpecificCategories(userEmail);
    }
  }

  @Transactional
  public CategoryDTO createCategory(String userEmail, CategoryDTO categoryDTO) {
    // Check if category name already exists
    if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
      throw new CategoryAlreadyExistsException(
          "Category with name '" + categoryDTO.getName() + "' already exists.");
    }

    CategoryEntity categoryEntity =
        new CategoryEntity(categoryDTO.getName(), categoryDTO.getColor());
    CategoryEntity savedCategory = categoryRepository.save(categoryEntity);
    return new CategoryDTO(
        savedCategory.getCategoryId(), savedCategory.getName(), savedCategory.getColor());
  }

  @Transactional
  public CategoryDTO updateCategory(String userEmail, Integer categoryId, CategoryDTO categoryDTO) {
    if (!permissionValidator.isAdmin(userEmail)) {
      throw new UnauthorizedUserException("This feature is only available to administrators.");
    }

    CategoryEntity category =
        categoryRepository
            .findByCategoryId(categoryId)
            .orElseThrow(
                () -> new CategoryNotFoundException("No category found with id: " + categoryId));

    category.setName(categoryDTO.getName());
    category.setColor(categoryDTO.getColor());

    CategoryEntity saved = categoryRepository.save(category);

    return new CategoryDTO(saved.getCategoryId(), saved.getName(), saved.getColor());
  }

  @Transactional
  public String deleteCategory(String userEmail, Integer categoryId) {
    if (permissionValidator.isAdmin(userEmail)) {
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
