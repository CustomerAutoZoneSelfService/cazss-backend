package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.UserCategoryDTO;
import com.autozone.cazss_backend.repository.CategoryRepository;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.util.PermissionValidator;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCategoryService {
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private UserCategoryRepository userCategoryRepository;
  @Autowired private PermissionValidator permissionValidator;

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
    return "Users lost access to categories.";
  }
}
