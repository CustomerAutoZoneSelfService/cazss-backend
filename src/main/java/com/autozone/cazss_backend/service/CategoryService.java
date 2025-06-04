package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CategoryDTO;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.util.PermissionValidator;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private PermissionValidator permissionValidator;

  public List<CategoryDTO> getAllCategories() {
    return new ArrayList<CategoryDTO>();
  }

  public List<CategoryDTO> getUserCategories(Integer userId) {
    return new ArrayList<CategoryDTO>();
  }

  public List<CategoryDTO> getAvailableCategories(Integer userId) {
    return new ArrayList<CategoryDTO>();
  }

  @Transactional
  public CategoryDTO createCategory(Integer userId, CategoryDTO categoryDTO) {
    return new CategoryDTO();
  }

  @Transactional
  public CategoryDTO updateCategory(Integer userId, Integer categoryId, CategoryDTO categoryDTO) {
    return new CategoryDTO();
  }

  @Transactional
  public String deleteCategory(Integer userId, Integer categoryId) {
    return "Category deleted successfully";
  }
}
