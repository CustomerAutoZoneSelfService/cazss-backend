package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionValidator {
  @Autowired UserRepository userRepository;

  private UserRoleEnum getUserRole(Integer userId) {
    return userRepository.findByUserId(userId).get().getRole();
  }

  public boolean isAdmin(Integer userId) {
    return getUserRole(userId) == UserRoleEnum.ADMIN;
  }

  // public boolean isConfigurator(Integer userId) {
  //    return getUserRole(userId) == // UserRoleEnum.CONFIGURATOR;
  // }

  // public boolean isAdminOrConfigurator(Integer userId) {
  //    return getUserRole(userId) == UserRoleEnum.ADMIN || getUserRole(userId) ==
  // UserRoleEnum.CONFIGURATOR;
  // }
}
