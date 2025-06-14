package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.autozone.cazss_backend.DTO.*;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.exceptions.CategoryNotFoundException;
import com.autozone.cazss_backend.exceptions.UnauthorizedUserException;
import com.autozone.cazss_backend.repository.*;
import com.autozone.cazss_backend.util.*;
import java.util.*;
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
public class CategoryServiceTest {
  @Mock private EndpointsRepository endpointsRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private UserCategoryRepository userCategoryRepository;
  @Mock private PermissionValidator permissionValidator;
  @InjectMocks private CategoryService categoryService;

  // DELETE Tests
  @Test
  void deleteCategory_withValidAdminUserIdAndValidCategoryID_shouldWork() {
    // Mock an endpoint because fucking mockito
    EndpointsEntity mockEndpointEntity = new EndpointsEntity();
    mockEndpointEntity.setCategory(new CategoryEntity());

    List<EndpointsEntity> endpoints = new ArrayList<>();
    endpoints.add(mockEndpointEntity);

    when(categoryRepository.existsById(anyInt())).thenReturn(true);
    when(endpointsRepository.findByCategory_CategoryId(anyInt())).thenReturn(endpoints);
    when(permissionValidator.isAdmin(anyInt())).thenReturn(true);

    assertDoesNotThrow(() -> categoryService.deleteCategory(1));

    // Verify that these functions were called
    verify(categoryRepository).deleteByCategoryId(anyInt());
    verify(endpointsRepository).save(mockEndpointEntity);
    verify(userCategoryRepository).deleteByCategory_CategoryId(anyInt());
  }

  @Test
  void deleteCategory_withValidAdminUserIdButInvalidCategoryID_shouldThrowCategoryNotFound() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    given(authentication.getName()).willReturn(String.valueOf(1));
    given(securityContext.getAuthentication()).willReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(categoryRepository.existsById(anyInt())).thenReturn(false);
    when(permissionValidator.isAdmin(anyInt())).thenReturn(true);

    assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(1));
  }

  @Test
  void deleteCategory_withNonAdminUserId_shouldThrowUnauthorizedUserException() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    given(authentication.getName()).willReturn(String.valueOf(1));
    given(securityContext.getAuthentication()).willReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(permissionValidator.isAdmin(anyInt())).thenReturn(false);

    assertThrows(UnauthorizedUserException.class, () -> categoryService.deleteCategory(1));
  }

  @Test
  void updateCategory_AdminUser_UpdatesCategorySuccessfully() {
    Integer userId = 1;
    Integer categoryId = 10;
    CategoryDTO categoryDTO = new CategoryDTO(categoryId, "Updated Name", "#ABCDEF");

    CategoryEntity existingCategory = new CategoryEntity();
    existingCategory.setCategoryId(categoryId);
    existingCategory.setName("Old Name");
    existingCategory.setColor("#123456");

    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    given(authentication.getName()).willReturn(String.valueOf(userId));
    given(securityContext.getAuthentication()).willReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    when(permissionValidator.isAdmin(userId)).thenReturn(true);
    when(categoryRepository.findByCategoryId(categoryId)).thenReturn(Optional.of(existingCategory));
    when(categoryRepository.save(any(CategoryEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    CategoryDTO result = categoryService.updateCategory(categoryId, categoryDTO);

    assertEquals("Updated Name", result.getName());
    assertEquals("#ABCDEF", result.getColor());
    verify(categoryRepository).save(existingCategory);
  }

  @Test
  void updateCategory_NonAdminUser_ThrowsUnauthorizedUserException() {
    Integer userId = 2;
    Integer categoryId = 11;
    CategoryDTO categoryDTO = new CategoryDTO(categoryId, "New Name", "#111111");

    when(permissionValidator.isAdmin(userId)).thenReturn(false);

    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    given(authentication.getName()).willReturn(String.valueOf(userId));
    given(securityContext.getAuthentication()).willReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    UnauthorizedUserException exception =
        assertThrows(
            UnauthorizedUserException.class,
            () -> categoryService.updateCategory(categoryId, categoryDTO));

    assertEquals("This feature is only available to administrators.", exception.getMessage());
    verify(categoryRepository, never()).save(any());
  }

  @Test
  void updateCategory_CategoryNotFound_ThrowsCategoryNotFoundException() {
    Integer userId = 1;
    Integer categoryId = 99;
    CategoryDTO categoryDTO = new CategoryDTO(categoryId, "Ghost Category", "#FAFAFA");

    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);

    when(permissionValidator.isAdmin(userId)).thenReturn(true);
    when(categoryRepository.findByCategoryId(categoryId)).thenReturn(Optional.empty());

    CategoryNotFoundException exception =
        assertThrows(
            CategoryNotFoundException.class,
            () -> categoryService.updateCategory(categoryId, categoryDTO));

    assertEquals("No category found with id: " + categoryId, exception.getMessage());
    verify(categoryRepository, never()).save(any());
  }
}
