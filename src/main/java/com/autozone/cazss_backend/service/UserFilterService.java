package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.UserFilterDTO;
import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.entity.UserFilterEntity;
import com.autozone.cazss_backend.entity.UserFilterEntity.UserFilterId;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.exceptions.UserNotFoundException;
import com.autozone.cazss_backend.exceptions.ValidationException;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.ResponsePatternRepository;
import com.autozone.cazss_backend.repository.UserFilterRepository;
import com.autozone.cazss_backend.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for managing user filters. */
@Service
public class UserFilterService {

  @Autowired private UserFilterRepository userFilterRepository;
  @Autowired private ResponsePatternRepository responsePatternRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private EndpointsRepository endpointsRepository;

  /** Helper method to validate request parameters. */
  private void validateInputs(Integer serviceId, List<Integer> patternIds) {
    if (serviceId == null || patternIds == null || patternIds.isEmpty()) {
      throw new ValidationException("Service ID and response pattern IDs must be provided.");
    }

    Set<Integer> unique = new HashSet<>(patternIds);
    if (unique.size() != patternIds.size()) {
      throw new ValidationException("Duplicate response pattern IDs are not allowed.");
    }
  }

  /**
   * Retrieves all user filters for a given service (endpoint) ID and a hardcoded user ID.
   *
   * @param endpointId The ID of the endpoint for which to retrieve filters.
   * @return A {@link UserFilterListDTO} containing the list of user filters.
   * @throws ValidationException If the endpointId is null.
   * @throws ServiceNotFoundException If the user is not found.
   */
  public List<UserFilterDTO> getUserFiltersByServiceId(Integer serviceId) {
    Integer userId = 90; // Replace with actual user retrieval
    if (serviceId == null) {
      throw new ValidationException("EndpointId cannot be null");
    }

    List<UserFilterEntity> entities =
        userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            userId, serviceId);

    if (entities.isEmpty()) {
      throw new UserNotFoundException(String.format("User with id %s not found", serviceId));
    }

    return entities.stream()
        .map(e -> new UserFilterDTO(e.getId().getResponsePatternId()))
        .collect(Collectors.toList());
  }

  /**
   * Creates new user filters based on the provided request. This method is transactional and will
   * roll back in case of errors.
   *
   * @param serviceId The id of the service
   * @param responsePatternIds The list containing serviceId and response pattern IDs.
   * @throws ValidationException If serviceId or responsePatternIds are null/empty, or if duplicate
   *     responsePatternIds are provided, or if all provided responsePatternIds already exist.
   * @throws ServiceNotFoundException If the any of the specified response patterns are not found,
   *     or if any responsePatternIds are invalid for the given endpoint.
   * @throws UserNotFoundException If the user is not found
   */
  @Transactional
  public List<UserFilterDTO> createUserFilters(
      Integer serviceId, List<Integer> responsePatternIds) {
    Integer userId = 90;

    validateInputs(serviceId, responsePatternIds);

    // Load user reference
    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    // Validate existance of endpoint
    endpointsRepository
        .findById(serviceId)
        .orElseThrow(
            () -> new ServiceNotFoundException("Service with id " + serviceId + " not found"));

    Set<Integer> uniquePatternIds = new HashSet<>(responsePatternIds);

    // Get existing filters for the user and endpoint
    List<UserFilterEntity> existingFilters =
        userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            userId, serviceId);

    Set<Integer> existingPatternIds =
        existingFilters.stream()
            .map(e -> e.getId().getResponsePatternId())
            .collect(Collectors.toSet());

    // Filter only new patterns that are not already assigned
    Set<Integer> toInsert =
        uniquePatternIds.stream()
            .filter(id -> !existingPatternIds.contains(id))
            .collect(Collectors.toSet());

    if (toInsert.isEmpty()) {
      throw new ValidationException(
          "All provided responsePatternIds already exist for this endpoint.");
    }

    // Create new entities for the new filters
    List<UserFilterEntity> toSave =
        toInsert.stream()
            .map(
                pid -> {
                  ResponsePatternEntity pattern =
                      responsePatternRepository
                          .findById(pid)
                          .orElseThrow(
                              () ->
                                  new ServiceNotFoundException(
                                      "ResponsePattern with id " + pid + " not found"));
                  UserFilterEntity entity = new UserFilterEntity();
                  entity.setId(new UserFilterId(userId, pid));
                  entity.setUser(user);
                  entity.setResponsePattern(pattern);
                  return entity;
                })
            .collect(Collectors.toList());

    userFilterRepository.saveAll(toSave);

    return getUserFiltersByServiceId(serviceId);
  }

  /**
   * Replaces user filters based on the provided request. This method is transactional and will roll
   * back in case of errors. @
   *
   * @param serviceId The id of the service
   * @param responsePatternIds The list containing serviceId and response pattern IDs.
   * @throws ValidationException If endpointId or responsePatternIds are null/empty, or if duplicate
   *     responsePatternIds are provided, or if all provided responsePatternIds already exist.
   * @throws ServiceNotFoundException If the any of the specified response patterns are not found,
   *     or if any responsePatternIds are invalid for the given endpoint.
   */
  @Transactional
  public List<UserFilterDTO> updateUserFilters(
      Integer serviceId, List<Integer> responsePatternIds) {
    Integer userId = 90;

    validateInputs(serviceId, responsePatternIds);

    // Validate existance of endpoint
    endpointsRepository
        .findById(serviceId)
        .orElseThrow(
            () -> new ServiceNotFoundException("Service with id " + serviceId + " not found"));

    // Delete all current filters
    List<UserFilterEntity> existingFilters =
        userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            userId, serviceId);
    userFilterRepository.deleteAll(existingFilters);

    // Re-create new ones
    return createUserFilters(serviceId, responsePatternIds);
  }

  /**
   * Deletes a user filter identified by the user ID and response pattern ID. This method is
   * transactional.
   *
   * @param userId The ID of the user.
   * @param responsePatternId The ID of the response pattern associated with the filter.
   * @throws ValidationException If userId or responsePatternId is null.
   * @throws ServiceNotFoundException If the user filter to be deleted is not found.
   */
  @Transactional
  public void deleteUserFilter(Integer serviceId, Integer responsePatternId) {
    Integer userId = 90;

    if (serviceId == null || responsePatternId == null) {
      throw new ValidationException("Service ID and Pattern ID cannot be null.");
    }

    // Validate existance of endpoint
    endpointsRepository
        .findById(serviceId)
        .orElseThrow(
            () -> new ServiceNotFoundException("Service with id " + serviceId + " not found"));

    UserFilterId id = new UserFilterId(userId, responsePatternId);
    if (!userFilterRepository.existsById(id)) {
      throw new ServiceNotFoundException("User filter not found.");
    }

    userFilterRepository.deleteById(id);
  }
}
