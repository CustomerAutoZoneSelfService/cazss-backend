package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.UserCategoryDTO;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserCategoryService {
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private UserCategoryRepository userCategoryRepository;
  @Autowired private PermissionValidator permissionValidator;
  @Autowired private UserRepository userRepository;

  private static final Logger log = LoggerFactory.getLogger(UserCategoryService.class);

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
      Integer userId, Integer categoryId, List<Integer> usersToAdd) {

    if (usersToAdd.isEmpty()) {
      throw new IllegalArgumentException("List of userIds cannot be empty");
    }

    userRepository
        .findByUserId(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

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

        Optional<UserCategoryEntity> existing =
            userCategoryRepository.findByCategory_CategoryIdAndUser_UserId(
                categoryId, targetUserId);
        if (existing.isPresent()) {
          log.info("User {} already has access to category {}", targetUserId, categoryId);
          continue;
        }

        UserCategoryEntity relation = new UserCategoryEntity();
        relation.setUser(userOpt.get());
        relation.setCategory(categoryRepository.findById(categoryId).get());
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
  public String deleteAccessToCategoryForUsers(
      Integer userId, Integer categoryId, List<Integer> userIds) {
    return "Users lost access to categories.";
  }
}
