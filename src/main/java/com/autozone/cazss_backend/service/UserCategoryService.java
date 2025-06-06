package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.UserCategoryDTO;
import com.autozone.cazss_backend.entity.UserCategoryEntity;
import com.autozone.cazss_backend.exceptions.CategoryNotFoundException;
import com.autozone.cazss_backend.exceptions.UnauthorizedUserException;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.util.PermissionValidator;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCategoryService {
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private UserCategoryRepository userCategoryRepository;
  @Autowired private PermissionValidator permissionValidator;

  public List<UserCategoryDTO> getUsersWithAccessToCategory(Integer userId, Integer categoryId) {
    // Verify if user is admin
    if (!permissionValidator.isAdmin(userId)) {
      throw new UnauthorizedUserException("User does not have permission to access this resource.");
    }

    // Verify if category exists
    if (!categoryRepository.existsById(categoryId)) {
      throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
    }

    // Obtain users with access to the specified category
    List<UserCategoryEntity> userCategoryEntities =
        userCategoryRepository.findByCategory_CategoryId(categoryId);

    // Map UserCategoryEntity to UserCategoryDTO
    return userCategoryEntities.stream()
        .map(
            entity ->
                new UserCategoryDTO(
                    entity.getUser().getUserId(), entity.getCategory().getCategoryId()))
        .collect(Collectors.toList());
  }

  @Transactional
  public List<UserCategoryDTO> addPermissionToAccessCategoryToUsers(
      Integer userId, Integer categoryId, List<Integer> userIds) {
    return new ArrayList<UserCategoryDTO>();
  }

  @Transactional
  public String deleteAccessToCategoryForUsers(
      Integer userId, Integer categoryId, List<Integer> userIds) {
    return "Users lost access to categories.";
  }
}
