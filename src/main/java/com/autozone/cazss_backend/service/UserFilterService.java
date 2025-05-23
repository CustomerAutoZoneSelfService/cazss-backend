package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.entity.UserFilterEntity;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.exceptions.ValidationException;
import com.autozone.cazss_backend.repository.UserFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserFilterService {

  private final UserFilterRepository userFilterRepository;

  @Autowired
  public UserFilterService(UserFilterRepository userFilterRepository) {
    this.userFilterRepository = userFilterRepository;
  }

  /**
   * Deletes a user filter based on the user ID and response pattern ID.
   *
   * @param userId The ID of the user.
   * @param responsePatternId The ID of the response pattern.
   * @throws ValidationException if userId or responsePatternId are null.
   * @throws ServiceNotFoundException if the user filter does not exist.
   */
  @Transactional
  public void deleteUserFilter(Integer userId, Integer responsePatternId) {
    if (userId == null || responsePatternId == null) {
      throw new ValidationException("User ID and Response Pattern ID cannot be null.");
    }

    UserFilterEntity.UserFilterId userFilterId =
        new UserFilterEntity.UserFilterId(userId, responsePatternId);

    if (!userFilterRepository.existsById(userFilterId)) {
      throw new ServiceNotFoundException("User filter not found.");
    }

    userFilterRepository.deleteById(userFilterId);
  }
}
