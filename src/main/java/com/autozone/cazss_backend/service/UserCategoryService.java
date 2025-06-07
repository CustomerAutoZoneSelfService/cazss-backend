package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.UserCategoryDTO;
import com.autozone.cazss_backend.exceptions.UnauthorizedUserException;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import com.autozone.cazss_backend.util.PermissionValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserCategoryService {
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(UserCategoryService.class);
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private UserCategoryRepository userCategoryRepository;
  @Autowired private PermissionValidator permissionValidator;
  @Autowired private UserRepository userRepository;

  public List<UserCategoryDTO> getUsersWithAccessToCategory(Integer userId, Integer categoryId) {
    return new ArrayList<UserCategoryDTO>();
  }

  @Transactional
  public List<UserCategoryDTO> addPermissionToAccessCategoryToUsers(
      Integer userId, Integer categoryId, List<Integer> userIds) {
    return new ArrayList<UserCategoryDTO>();
  }

  @Transactional
  public String deleteAccessToCategoryForUsers(
      Integer userId, Integer categoryId, List<Integer> userIds) {
    // verify that userId, categoryId, and userIds are not null or empty
    if (userIds.isEmpty()) {
      throw new IllegalArgumentException("List of userIds cannot be empty");
    }
    // verify that user exists
    userRepository
        .findByUserId(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    // verify that user has admin permissions
    if (!permissionValidator.isAdmin(userId)) {
      throw new UnauthorizedUserException("This feature is only available to administrators.");
    }
    // verify that category exists
    if (!categoryRepository.existsById(categoryId)) {
      throw new EntityNotFoundException("Category with ID " + categoryId + " not found");
    }
    // delete access for each user in userIds
    int deletedCount = 0;
    List<Integer> failedUserIds = new ArrayList<>();
    for (Integer targetUserId : userIds) {
      try {
        // Verificar que el usuario objetivo existe
        if (!userRepository.existsById(targetUserId)) {
          log.warn("User with ID {} not found, skipping deletion", targetUserId);
          failedUserIds.add(targetUserId);
          continue;
        }

        Long deletedRows =
            userCategoryRepository.deleteByUser_UserIdAndCategory_CategoryId(
                targetUserId, categoryId);

        if (deletedRows > 0) {
          deletedCount++;
          log.info("Removed access to category {} for user {}", categoryId, targetUserId);
        } else {
          log.info("User {} already had no access to category {}", targetUserId, categoryId);
        }
      } catch (Exception e) {
        log.error("Failed to remove access for user {}: {}", targetUserId, e.getMessage());
        failedUserIds.add(targetUserId);
      }
    }
    return "Users lost access to categories.";
  }
}
