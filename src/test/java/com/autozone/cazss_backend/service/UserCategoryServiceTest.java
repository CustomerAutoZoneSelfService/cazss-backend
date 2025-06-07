package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.autozone.cazss_backend.DTO.*;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.UserCategoryEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.exceptions.CategoryNotFoundException;
import com.autozone.cazss_backend.exceptions.UnauthorizedUserException;
import com.autozone.cazss_backend.repository.*;
import com.autozone.cazss_backend.util.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
public class UserCategoryServiceTest {
  @InjectMocks private UserCategoryService userCategoryService;

  @Mock private UserRepository userRepository;

  @Mock private CategoryRepository categoryRepository;

  @Mock private UserCategoryRepository userCategoryRepository;

  @Mock private PermissionValidator permissionValidator;

  private final Integer adminUserId = 1;
  private UserEntity adminUser;
  private UserEntity normalUser;
  private Integer categoryId;

  @BeforeEach
  public void setup() {
    adminUser = new UserEntity();
    adminUser.setUserId(1);
    adminUser.setRole(UserRoleEnum.ADMIN);

    normalUser = new UserEntity();
    normalUser.setUserId(2);
    normalUser.setRole(UserRoleEnum.USER);

    categoryId = 100;
  }

  @Test
  public void whenValidInputs_thenDeleteAccessSuccess() {
    // Arrange
    when(userRepository.findByUserId(1)).thenReturn(Optional.of(adminUser));
    when(permissionValidator.isAdmin(1)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(true);
    when(userRepository.existsById(2)).thenReturn(true);
    when(userCategoryRepository.deleteByUser_UserIdAndCategory_CategoryId(2, categoryId))
        .thenReturn(1L);

    // Act
    String result = userCategoryService.deleteAccessToCategoryForUsers(1, categoryId, List.of(2));

    // Assert
    assertNotNull(result);
    verify(userCategoryRepository, times(1))
        .deleteByUser_UserIdAndCategory_CategoryId(2, categoryId);
  }

  @Test
  public void whenUserIdsListIsEmpty_thenThrowIllegalArgumentException() {
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              userCategoryService.deleteAccessToCategoryForUsers(1, categoryId, new ArrayList<>());
            });
    assertEquals("List of userIds cannot be empty", thrown.getMessage());
  }

  @Test
  public void whenUserNotFound_thenThrowUnauthorized() {
    when(userRepository.findByUserId(1)).thenReturn(Optional.empty());

    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              userCategoryService.deleteAccessToCategoryForUsers(1, categoryId, List.of(2));
            });

    assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatusCode());
    assertEquals("User not found", thrown.getReason());
  }

  @Test
  public void whenUserIsNotAdmin_thenThrowUnauthorizedUserException() {
    when(userRepository.findByUserId(2)).thenReturn(Optional.of(normalUser));
    when(permissionValidator.isAdmin(2)).thenReturn(false);

    UnauthorizedUserException thrown =
        assertThrows(
            UnauthorizedUserException.class,
            () -> {
              userCategoryService.deleteAccessToCategoryForUsers(2, categoryId, List.of(3));
            });

    assertEquals("This feature is only available to administrators.", thrown.getMessage());
  }

  @Test
  public void whenCategoryNotFound_thenThrowEntityNotFoundException() {
    when(userRepository.findByUserId(1)).thenReturn(Optional.of(adminUser));
    when(permissionValidator.isAdmin(1)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(false);

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          userCategoryService.deleteAccessToCategoryForUsers(1, categoryId, List.of(2));
        });
  }

  @Test
  public void whenTargetUserNotFound_thenSkipUser() {
    when(userRepository.findByUserId(1)).thenReturn(Optional.of(adminUser));
    when(permissionValidator.isAdmin(1)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(true);
    when(userRepository.existsById(2)).thenReturn(false); // User doesn't exist

    String result = userCategoryService.deleteAccessToCategoryForUsers(1, categoryId, List.of(2));

    assertNotNull(result);
    verify(userCategoryRepository, never())
        .deleteByUser_UserIdAndCategory_CategoryId(anyInt(), anyInt());
  }

  @Test
  public void whenDeleteFailsForUser_thenContinueWithOthers() {
    when(userRepository.findByUserId(1)).thenReturn(Optional.of(adminUser));
    when(permissionValidator.isAdmin(1)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(true);
    when(userRepository.existsById(2)).thenReturn(true);
    when(userRepository.existsById(3)).thenReturn(true);

    // Simular que falla el delete para userId 2
    when(userCategoryRepository.deleteByUser_UserIdAndCategory_CategoryId(2, categoryId))
        .thenThrow(new RuntimeException("DB error"));

    // Simular que el siguiente sí se elimina bien
    when(userCategoryRepository.deleteByUser_UserIdAndCategory_CategoryId(3, categoryId))
        .thenReturn(1L);

    String result =
        userCategoryService.deleteAccessToCategoryForUsers(1, categoryId, List.of(2, 3));

    assertNotNull(result);

    verify(userCategoryRepository, times(1))
        .deleteByUser_UserIdAndCategory_CategoryId(2, categoryId);
    verify(userCategoryRepository, times(1))
        .deleteByUser_UserIdAndCategory_CategoryId(3, categoryId);
  }

  @Test
  public void testGetUsersWithAccessToCategory_ReturnsList() {
    // Arrange
    when(permissionValidator.isAdmin(adminUserId)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(true);

    UserEntity user = new UserEntity();
    user.setUserId(2);

    CategoryEntity category = new CategoryEntity();
    category.setCategoryId(categoryId);

    UserCategoryEntity entity = new UserCategoryEntity();
    entity.setUser(user);
    entity.setCategory(category);

    List<UserCategoryEntity> entities = List.of(entity);
    when(userCategoryRepository.findByCategory_CategoryId(categoryId)).thenReturn(entities);

    // Act
    List<UserCategoryDTO> result =
        userCategoryService.getUsersWithAccessToCategory(adminUserId, categoryId);

    // Assert
    assertEquals(1, result.size());
    assertEquals(2, result.get(0).getUserId());
    assertEquals(categoryId, result.get(0).getEndpointId());
  }

  // When user is not admin
  @Test
  public void testGetUsersWithAccessToCategory_UserNotAdmin_ThrowsException() {
    // Arrange
    when(permissionValidator.isAdmin(adminUserId)).thenReturn(false);

    // Act & Assert
    var exception =
        org.junit.jupiter.api.Assertions.assertThrows(
            UnauthorizedUserException.class,
            () -> userCategoryService.getUsersWithAccessToCategory(adminUserId, categoryId));

    assertEquals("User does not have permission to access this resource.", exception.getMessage());
  }

  // When category does not exist
  @Test
  public void testGetUsersWithAccessToCategory_CategoryNotFound_ThrowsException() {
    // Arrange
    when(permissionValidator.isAdmin(adminUserId)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(false);

    // Act & Assert
    var exception =
        org.junit.jupiter.api.Assertions.assertThrows(
            CategoryNotFoundException.class,
            () -> userCategoryService.getUsersWithAccessToCategory(adminUserId, categoryId));

    assertEquals("Category not found with ID: " + categoryId, exception.getMessage());
  }

  // When no users have access to the category
  @Test
  public void testGetUsersWithAccessToCategory_NoUsers_ReturnsEmptyList() {
    // Arrange
    when(permissionValidator.isAdmin(adminUserId)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(true);
    when(userCategoryRepository.findByCategory_CategoryId(categoryId)).thenReturn(List.of());

    // Act
    List<UserCategoryDTO> result =
        userCategoryService.getUsersWithAccessToCategory(adminUserId, categoryId);

    // Assert
    assertEquals(0, result.size());
  }

  // When multiple users have access to the category
  @Test
  public void testGetUsersWithAccessToCategory_MultipleUsers_ReturnsList() {
    // Arrange
    when(permissionValidator.isAdmin(adminUserId)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(true);

    UserEntity user1 = new UserEntity();
    user1.setUserId(2);

    UserEntity user2 = new UserEntity();
    user2.setUserId(3);

    CategoryEntity category = new CategoryEntity();
    category.setCategoryId(categoryId);

    UserCategoryEntity entity1 = new UserCategoryEntity();
    entity1.setUser(user1);
    entity1.setCategory(category);

    UserCategoryEntity entity2 = new UserCategoryEntity();
    entity2.setUser(user2);
    entity2.setCategory(category);

    List<UserCategoryEntity> entities = List.of(entity1, entity2);
    when(userCategoryRepository.findByCategory_CategoryId(categoryId)).thenReturn(entities);

    // Act
    List<UserCategoryDTO> result =
        userCategoryService.getUsersWithAccessToCategory(adminUserId, categoryId);

    // Assert
    assertEquals(2, result.size());
    assertEquals(2, result.get(0).getUserId());
    assertEquals(3, result.get(1).getUserId());
  }
}
