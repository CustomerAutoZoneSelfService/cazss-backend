package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.repository.UserRepository;
import com.autozone.cazss_backend.service.EndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserDataUtil {
  private static final Logger logger = LoggerFactory.getLogger(EndpointService.class);
  @Autowired UserRepository userRepository;

  public UserEntity getUserEntity() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ServiceNotFoundException("No authenticated user found");
    }

    String userIdStr = authentication.getName();
    Integer userId;
    try {
      userId = Integer.parseInt(userIdStr);
    } catch (NumberFormatException e) {
      logger.error("Invalid user ID format in JWT token: {}", userIdStr);
      throw new ServiceNotFoundException("Invalid user ID format in authentication token");
    }

    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  logger.error("User not found with ID: {}", userId);
                  return new ServiceNotFoundException("User not found with ID: " + userId);
                });

    logger.debug("Found user: {}", user.getUserId());

    return user;
  }
}
