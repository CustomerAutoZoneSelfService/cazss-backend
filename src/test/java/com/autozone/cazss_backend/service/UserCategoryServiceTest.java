package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.autozone.cazss_backend.DTO.UserCategoryDTO;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.UserCategoryEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.exceptions.CategoryNotFoundException;
import com.autozone.cazss_backend.exceptions.UnauthorizedUserException;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.util.PermissionValidator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserCategoryServiceTest {

  @Mock private CategoryRepository categoryRepository;
  @Mock private UserCategoryRepository userCategoryRepository;
  @Mock private PermissionValidator permissionValidator;

  @InjectMocks private UserCategoryService userCategoryService;

  private final Integer adminUserId = 1;
  private final Integer categoryId = 100;

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
