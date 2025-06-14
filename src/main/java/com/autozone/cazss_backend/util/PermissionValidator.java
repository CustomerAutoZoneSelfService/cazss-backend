package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.UserCategoryEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.UserCategoryRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionValidator {
  @Autowired UserRepository userRepository;
  @Autowired UserCategoryRepository userCategoryRepository;
  @Autowired EndpointsRepository endpointsRepository;

  private UserRoleEnum getUserRole(Integer userId) {
    return userRepository.findByUserId(userId).get().getRole();
  }

  public boolean isAdmin(Integer userId) {
    return getUserRole(userId) == UserRoleEnum.ADMIN;
  }

  public boolean isAdminOrConfigurator(Integer userId) {
    UserRoleEnum userRole = getUserRole(userId);
    return userRole == UserRoleEnum.ADMIN | userRole == UserRoleEnum.CONFIG;
  }

  public boolean isAdminOrAuditor(Integer userId) {
    UserRoleEnum userRole = getUserRole(userId);
    return userRole == UserRoleEnum.ADMIN | userRole == UserRoleEnum.AUDITOR;
  }

  public boolean isConfigurator(Integer userId) {
    return getUserRole(userId) == UserRoleEnum.CONFIG;
  }

  public boolean canUserExecuteService(Integer userId, Integer endpointId) {
    Optional<EndpointsEntity> foundEndpoint = endpointsRepository.findById(endpointId);
    if (foundEndpoint.isPresent()) {
      if (isAdmin(userId)) {
        return true;
      }
      CategoryEntity categoryOfFoundEndpoint = foundEndpoint.get().getCategory();
      System.out.println(
          "Category Id of found endpoint is: " + categoryOfFoundEndpoint.getCategoryId());
      System.out.println("Category of found endpoint is:" + categoryOfFoundEndpoint.getName());
      if (categoryOfFoundEndpoint == null) {
        return isConfigurator(userId); // If the user is a configurator, they have access
      } else {
        Optional<UserCategoryEntity> foundUserPermission =
            userCategoryRepository.findByUser_UserIdAndCategory_CategoryId(
                userId, categoryOfFoundEndpoint.getCategoryId());
        System.out.println("User id of user is:" + userId);
        System.out.println(foundUserPermission.isPresent());
        // System.out.println(foundUserPermission.get().getId());
        return foundUserPermission.isPresent();
      }
    } else {
      throw new ServiceNotFoundException("Service not found.");
    }
  }

  // public boolean isConfigurator(Integer userId) {
  //    return getUserRole(userId) == // UserRoleEnum.CONFIGURATOR;
  // }

  // public boolean isAdminOrConfigurator(Integer userId) {
  //    return getUserRole(userId) == UserRoleEnum.ADMIN || getUserRole(userId) ==
  // UserRoleEnum.CONFIGURATOR;
  // }
}
