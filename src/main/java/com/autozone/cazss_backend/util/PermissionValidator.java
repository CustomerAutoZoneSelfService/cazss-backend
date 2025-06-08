package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.entity.UserCategoryEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionValidator {
  @Autowired UserRepository userRepository;
  @Autowired private UserCategoryRepository userCategoryRepository;

  private UserRoleEnum getUserRole(Integer userId) {
    return userRepository.findByUserId(userId).get().getRole();
  }

  public boolean isAdmin(Integer userId) {
    return getUserRole(userId) == UserRoleEnum.ADMIN;
  }

  public Set<Integer> getUserAccessibleCategoriesIds(Integer userId) {
    HashSet<Integer> userAccessibleCategoryIds = new HashSet<Integer>();
    List<UserCategoryEntity> foundUserAccessibleCategories =
        userCategoryRepository.findByUser_UserId(userId);

    for (UserCategoryEntity foundUserAccessibleCategory : foundUserAccessibleCategories) {
      userAccessibleCategoryIds.add(foundUserAccessibleCategory.getCategory().getCategoryId());
    }

    return userAccessibleCategoryIds;
  }

  // public boolean isConfigurator(Integer userId) {
  //    return getUserRole(userId) == // UserRoleEnum.CONFIGURATOR;
  // }

  // public boolean isAdminOrConfigurator(Integer userId) {
  //    return getUserRole(userId) == UserRoleEnum.ADMIN || getUserRole(userId) ==
  // UserRoleEnum.CONFIGURATOR;
  // }
}
