package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
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
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import com.autozone.cazss_backend.util.*;
import com.autozone.cazss_backend.util.PermissionValidator;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

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
    when(permissionValidator.isAdmin(adminUser.getUserId())).thenReturn(false);

    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    given(authentication.getName()).willReturn(String.valueOf(adminUser.getUserId()));
    given(securityContext.getAuthentication()).willReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    // Arrange
    when(permissionValidator.isAdmin(1)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(true);
    when(userRepository.existsById(2)).thenReturn(true);
    when(userCategoryRepository.deleteByUser_UserIdAndCategory_CategoryId(2, categoryId))
        .thenReturn(1L);

    // Act
    String result = userCategoryService.deleteAccessToCategoryForUsers(categoryId, List.of(2));

    // Assert
    assertNotNull(result);
    verify(userCategoryRepository, times(1))
        .deleteByUser_UserIdAndCategory_CategoryId(2, categoryId);
  }

  @Test
  public void whenUserIdsListIsEmpty_thenThrowIllegalArgumentException() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    given(authentication.getName()).willReturn(String.valueOf(adminUser.getUserId()));
    given(securityContext.getAuthentication()).willReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              userCategoryService.deleteAccessToCategoryForUsers(categoryId, new ArrayList<>());
            });
    assertEquals("List of userIds cannot be empty", thrown.getMessage());
  }

  @Test
  public void whenUserNotFound_thenThrowUnauthorized() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    given(authentication.getName()).willReturn(String.valueOf(adminUser.getUserId()));
    given(securityContext.getAuthentication()).willReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    UnauthorizedUserException thrown =
        assertThrows(
            UnauthorizedUserException.class,
            () -> {
              userCategoryService.deleteAccessToCategoryForUsers(categoryId, List.of(2));
            });

    assertEquals("This feature is only available to administrators.", thrown.getMessage());
  }

  @Test
  public void whenUserIsNotAdmin_thenThrowUnauthorizedUserException() {
    when(permissionValidator.isAdmin(2)).thenReturn(false);
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    given(authentication.getName()).willReturn(String.valueOf(normalUser.getUserId()));
    given(securityContext.getAuthentication()).willReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    UnauthorizedUserException thrown =
        assertThrows(
            UnauthorizedUserException.class,
            () -> {
              userCategoryService.deleteAccessToCategoryForUsers(categoryId, List.of(3));
            });

    assertEquals("This feature is only available to administrators.", thrown.getMessage());
  }

  @Test
  public void whenCategoryNotFound_thenThrowEntityNotFoundException() {
    when(permissionValidator.isAdmin(1)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(false);

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          userCategoryService.deleteAccessToCategoryForUsers(categoryId, List.of(2));
        });
  }

  @Test
  public void whenTargetUserNotFound_thenSkipUser() {
    when(permissionValidator.isAdmin(1)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(true);
    when(userRepository.existsById(2)).thenReturn(false); // User doesn't exist

    String result = userCategoryService.deleteAccessToCategoryForUsers(categoryId, List.of(2));

    assertNotNull(result);
    verify(userCategoryRepository, never())
        .deleteByUser_UserIdAndCategory_CategoryId(anyInt(), anyInt());
  }

  @Test
  public void whenDeleteFailsForUser_thenContinueWithOthers() {
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

    String result = userCategoryService.deleteAccessToCategoryForUsers(categoryId, List.of(2, 3));

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
    List<UserCategoryDTO> result = userCategoryService.getUsersWithAccessToCategory(categoryId);

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
            () -> userCategoryService.getUsersWithAccessToCategory(categoryId));

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
            () -> userCategoryService.getUsersWithAccessToCategory(categoryId));

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
    List<UserCategoryDTO> result = userCategoryService.getUsersWithAccessToCategory(categoryId);

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
    List<UserCategoryDTO> result = userCategoryService.getUsersWithAccessToCategory(categoryId);

    // Assert
    assertEquals(2, result.size());
    assertEquals(2, result.get(0).getUserId());
    assertEquals(3, result.get(1).getUserId());
  }

  @Test
  public void testAddPermissionToAccessCategoryToUsers_ReturnsOneUser() {
    // Arrange
    Integer adminId = 1;
    Integer targetUserId = 2;
    Integer categoryId = 100;

    UserEntity admin = new UserEntity();
    admin.setUserId(adminId);

    UserEntity targetUser = new UserEntity();
    targetUser.setUserId(targetUserId);

    CategoryEntity category = new CategoryEntity();
    category.setCategoryId(categoryId);

    when(permissionValidator.isAdmin(adminId)).thenReturn(true);
    when(categoryRepository.existsById(categoryId)).thenReturn(true);
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
    when(userRepository.findByUserId(targetUserId)).thenReturn(Optional.of(targetUser));
    // when(userCategoryRepository.findByCategory_CategoryIdAndUser_UserId(categoryId,
    // targetUserId))
    //     .thenReturn(Optional.empty());
    when(userCategoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    List<UserCategoryDTO> result =
        userCategoryService.addPermissionToAccessCategoryToUsers(categoryId, List.of(targetUserId));

    // Assert
    assertEquals(1, result.size());
    assertEquals(targetUserId, result.get(0).getUserId());
    assertEquals(categoryId, result.get(0).getEndpointId());
  }

  @Test
  public void whenAdminAddsMultipleUsers_thenAllAreGrantedAccess() {
    UserEntity admin = new UserEntity();
    admin.setUserId(1);

    CategoryEntity category = new CategoryEntity();
    category.setCategoryId(100);

    UserEntity user1 = new UserEntity();
    user1.setUserId(2);
    UserEntity user2 = new UserEntity();
    user2.setUserId(3);

    // Mock admin check and category existence
    when(permissionValidator.isAdmin(1)).thenReturn(true);
    when(categoryRepository.existsById(100)).thenReturn(true);
    when(categoryRepository.findById(100)).thenReturn(Optional.of(category));

    // Mock user 1
    when(userRepository.findByUserId(2)).thenReturn(Optional.of(user1));
    //    when(userCategoryRepository.findByCategory_CategoryIdAndUser_UserId(100, 2))
    //        .thenReturn(Optional.empty());

    // Mock user 2
    when(userRepository.findByUserId(3)).thenReturn(Optional.of(user2));
    //    when(userCategoryRepository.findByCategory_CategoryIdAndUser_UserId(100, 3))
    //        .thenReturn(Optional.empty());

    // Mock save
    when(userCategoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    List<UserCategoryDTO> result =
        userCategoryService.addPermissionToAccessCategoryToUsers(100, List.of(2, 3));

    assertEquals(2, result.size());
    assertEquals(2, result.get(0).getUserId());
    assertEquals(3, result.get(1).getUserId());
  }

  @Test
  public void whenAdminUserNotFound_thenThrowUnauthorized() {
    Exception exception =
        assertThrows(
            UnauthorizedUserException.class,
            () -> userCategoryService.addPermissionToAccessCategoryToUsers(100, List.of(2)));

    assertEquals("This feature is only available to administrators.", exception.getMessage());
  }

  @Test
  public void whenUserAlreadyHasAccess_thenSkipAdding() {
    UserEntity admin = new UserEntity();
    admin.setUserId(1);

    UserEntity existingUser = new UserEntity();
    existingUser.setUserId(2);

    // Stubs necesarios
    when(permissionValidator.isAdmin(1)).thenReturn(true);
    when(categoryRepository.existsById(100)).thenReturn(true);
    when(userRepository.findByUserId(2)).thenReturn(Optional.of(existingUser));
    //    when(userCategoryRepository.findByCategory_CategoryIdAndUser_UserId(100, 2))
    //        .thenReturn(Optional.of(new UserCategoryEntity()));

    // Ejecuta
    List<UserCategoryDTO> result =
        userCategoryService.addPermissionToAccessCategoryToUsers(100, List.of(2));

    // No se añadió nada
    assertEquals(0, result.size());
  }

  @Test
  public void whenEmptyUserList_thenThrowsIllegalArgumentException() {
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> userCategoryService.addPermissionToAccessCategoryToUsers(100, List.of()));

    assertEquals("List of userIds cannot be empty", ex.getMessage());
  }

  @Test
  public void whenAdminUserDoesNotExist_thenThrowsUnauthorized() {
    UnauthorizedUserException ex =
        assertThrows(
            UnauthorizedUserException.class,
            () -> userCategoryService.addPermissionToAccessCategoryToUsers(100, List.of(2)));

    assertEquals("This feature is only available to administrators.", ex.getMessage());
  }

  @Test
  public void whenCategoryDoesNotExist_thenThrowsEntityNotFound() {
    UserEntity admin = new UserEntity();
    admin.setUserId(1);
    when(permissionValidator.isAdmin(1)).thenReturn(true);
    when(categoryRepository.existsById(999)).thenReturn(false);

    EntityNotFoundException ex =
        assertThrows(
            EntityNotFoundException.class,
            () -> userCategoryService.addPermissionToAccessCategoryToUsers(999, List.of(2)));

    assertEquals("Category with ID 999 not found", ex.getMessage());
  }

  @Test
  public void whenUserAlreadyHasAccess_thenSkipSavingAgain() {
    UserEntity admin = new UserEntity();
    admin.setUserId(1);
    UserEntity targetUser = new UserEntity();
    targetUser.setUserId(2);

    CategoryEntity category = new CategoryEntity();
    category.setCategoryId(100);

    UserCategoryEntity existing = new UserCategoryEntity();
    existing.setUser(targetUser);
    existing.setCategory(category);

    when(permissionValidator.isAdmin(1)).thenReturn(true);
    when(categoryRepository.existsById(100)).thenReturn(true);
    when(userRepository.findByUserId(2)).thenReturn(Optional.of(targetUser));
    //    when(userCategoryRepository.findByCategory_CategoryIdAndUser_UserId(100, 2))
    //       .thenReturn(Optional.of(existing));

    List<UserCategoryDTO> result =
        userCategoryService.addPermissionToAccessCategoryToUsers(100, List.of(2));

    assertEquals(0, result.size()); // No new user was added
  }
}
