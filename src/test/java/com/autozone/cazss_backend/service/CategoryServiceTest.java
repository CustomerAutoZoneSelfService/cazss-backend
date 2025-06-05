package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    assertDoesNotThrow(() -> categoryService.deleteCategory(90, 1));

    // Verify that these functions were called
    verify(categoryRepository).deleteByCategoryId(anyInt());
    verify(endpointsRepository).save(mockEndpointEntity);
    verify(userCategoryRepository).deleteByCategory_CategoryId(anyInt());
  }

  @Test
  void deleteCategory_withValidAdminUserIdButInvalidCategoryID_shouldThrowCategoryNotFound() {
    when(categoryRepository.existsById(anyInt())).thenReturn(false);
    when(permissionValidator.isAdmin(anyInt())).thenReturn(true);

    assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(90, 1));
  }

  @Test
  void deleteCategory_withNonAdminUserId_shouldThrowUnauthorizedUserException() {
    when(permissionValidator.isAdmin(anyInt())).thenReturn(false);

    assertThrows(UnauthorizedUserException.class, () -> categoryService.deleteCategory(90, 1));
  }
}
