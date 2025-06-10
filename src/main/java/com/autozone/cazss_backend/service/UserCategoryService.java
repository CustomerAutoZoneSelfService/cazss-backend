package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.UserCategoryDTO;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.UserCategoryEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.exceptions.CategoryNotFoundException;
import com.autozone.cazss_backend.exceptions.UnauthorizedUserException;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import com.autozone.cazss_backend.util.PermissionValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserCategoryService {
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(UserCategoryService.class);
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private UserCategoryRepository userCategoryRepository;
  @Autowired private PermissionValidator permissionValidator;
  @Autowired private UserRepository userRepository;

  public List<UserCategoryDTO> getUsersWithAccessToCategory(Integer categoryId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer userId = Integer.parseInt(authentication.getName());
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
      Integer categoryId, List<Integer> usersToAdd) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer userId = Integer.parseInt(authentication.getName());

    if (usersToAdd.isEmpty()) {
      throw new IllegalArgumentException("List of userIds cannot be empty");
    }

    if (!permissionValidator.isAdmin(userId)) {
      throw new UnauthorizedUserException("This feature is only available to administrators.");
    }

    if (!categoryRepository.existsById(categoryId)) {
      throw new EntityNotFoundException("Category with ID " + categoryId + " not found");
    }

    List<UserCategoryDTO> addedUsers = new ArrayList<>();

    for (Integer targetUserId : usersToAdd) {
      try {
        Optional<UserEntity> userOpt = userRepository.findByUserId(targetUserId);

        if (userOpt.isEmpty()) {
          log.warn("User with ID {} not found, skipping", targetUserId);
          continue;
        }

        log.info(
            "Found user:"
                + userOpt.get().getUserId()
                + " "
                + userOpt.get().getEmail()
                + " "
                + userOpt.get().getActive());

        Optional<UserCategoryEntity> existing =
            userCategoryRepository.findByCategory_CategoryIdAndUser_UserId(
                categoryId, targetUserId);
        if (existing.isPresent()) {
          log.info("User {} already has access to category {}", targetUserId, categoryId);
          continue;
        }

        UserCategoryEntity relation = new UserCategoryEntity();
        relation.setUser(userOpt.get());
        Optional<CategoryEntity> foundCategory = categoryRepository.findById(categoryId);
        log.info(
            "Found category:"
                + foundCategory.get().getCategoryId()
                + " "
                + foundCategory.get().getName()
                + " "
                + foundCategory.get().getColor());
        relation.setCategory(foundCategory.get());

        UserCategoryEntity.UserCategoryId compositeId =
            new UserCategoryEntity.UserCategoryId(
                userOpt.get().getUserId(), foundCategory.get().getCategoryId());

        relation.setId(compositeId);

        userCategoryRepository.save(relation);

        addedUsers.add(new UserCategoryDTO(targetUserId, categoryId));
        log.info("Granted access to category {} for user {}", categoryId, targetUserId);
      } catch (Exception e) {
        log.error("Failed to add access for user {}: {}", targetUserId, e.getMessage());
      }
    }

    return addedUsers;
  }

  @Transactional
  public String deleteAccessToCategoryForUsers(Integer categoryId, List<Integer> userIds) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer userId = Integer.parseInt(authentication.getName());
    // verify that userId, categoryId, and userIds are not null or empty
    if (userIds.isEmpty()) {
      throw new IllegalArgumentException("List of userIds cannot be empty");
    }

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
