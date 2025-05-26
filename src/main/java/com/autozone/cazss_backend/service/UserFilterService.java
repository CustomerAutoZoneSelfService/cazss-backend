package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.RequestUserFilterDTO;
import com.autozone.cazss_backend.DTO.UserFilterDTO;
import com.autozone.cazss_backend.DTO.UserFilterListDTO;
import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.entity.UserFilterEntity;
import com.autozone.cazss_backend.entity.UserFilterEntity.UserFilterId;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.exceptions.ValidationException;
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

  private final UserFilterRepository userFilterRepository;
  private final ResponsePatternRepository responsePatternRepository;
  private final UserRepository userRepository;

  /**
   * Constructs a new UserFilterService with the required repositories.
   *
   * @param userFilterRepository Repository for user filter data.
   * @param responsePatternRepository Repository for response pattern data.
   * @param userRepository Repository for user data.
   */
  @Autowired
  public UserFilterService(
      UserFilterRepository userFilterRepository,
      ResponsePatternRepository responsePatternRepository,
      UserRepository userRepository) {
    this.userFilterRepository = userFilterRepository;
    this.responsePatternRepository = responsePatternRepository;
    this.userRepository = userRepository;
  }

  /**
   * Retrieves all user filters for a given service (endpoint) ID and a hardcoded user ID.
   *
   * @param endpointId The ID of the endpoint for which to retrieve filters.
   * @return A {@link UserFilterListDTO} containing the list of user filters.
   * @throws ValidationException If the endpointId is null.
   * @throws ServiceNotFoundException If the user is not found.
   */
  public UserFilterListDTO getUserFiltersByServiceId(Integer endpointId) {
    Integer userId = 90; // Replace with actual user retrieval
    if (endpointId == null) {
      throw new ValidationException("EndpointId cannot be null");
    }
    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ServiceNotFoundException("User not found"));

    List<UserFilterEntity> entities =
        userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            userId, endpointId);

    List<UserFilterDTO> dtos =
        entities.stream()
            .map(e -> new UserFilterDTO(e.getId().getResponsePatternId()))
            .collect(Collectors.toList());
    return new UserFilterListDTO(dtos);
  }

  /**
   * Creates new user filters based on the provided request. This method is transactional and will
   * roll back in case of errors.
   *
   * @param request The {@link RequestUserFilterDTO} containing endpoint ID and response pattern
   *     IDs.
   * @throws ValidationException If endpointId or responsePatternIds are null/empty, or if duplicate
   *     responsePatternIds are provided, or if all provided responsePatternIds already exist.
   * @throws ServiceNotFoundException If the user or any of the specified response patterns are not
   *     found, or if any responsePatternIds are invalid for the given endpoint.
   */
  @Transactional
  public void createUserFilters(RequestUserFilterDTO request) {
    Integer userId = 90;
    Integer endpointId = request.getEndpointId();
    List<Integer> responsePatternIds = request.getResponsePatternIds();

    if (endpointId == null || responsePatternIds == null || responsePatternIds.isEmpty()) {
      throw new ValidationException("EndpointId and patternIds must be provided");
    }

    Set<Integer> uniquePatternIds = new HashSet<>(responsePatternIds);
    if (uniquePatternIds.size() != responsePatternIds.size()) {
      throw new ValidationException("Duplicate responsePatternIds are not allowed");
    }

    // Load user reference
    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ServiceNotFoundException("User not found"));

    // Fetch valid patterns for endpoint
    List<ResponsePatternEntity> patterns =
        responsePatternRepository.findByResponse_Endpoint_EndpointId(endpointId);
    List<Integer> validPatternIds =
        patterns.stream()
            .map(ResponsePatternEntity::getResponsePatternId)
            .collect(Collectors.toList());
    if (!validPatternIds.containsAll(uniquePatternIds)) {
      Set<Integer> invalid = new HashSet<>(uniquePatternIds);
      invalid.removeAll(validPatternIds);
      throw new ServiceNotFoundException(
          "Invalid responsePatternIds for endpoint " + endpointId + ": " + invalid);
    }

    // Get existing filters for the user and endpoint
    List<UserFilterEntity> existingFilters =
        userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            userId, endpointId);
    Set<Integer> existingPatternIds =
        existingFilters.stream()
            .map(e -> e.getId().getResponsePatternId())
            .collect(Collectors.toSet());

    // Filter only new patterns that are not already assigned
    Set<Integer> filtersToAdd =
        uniquePatternIds.stream()
            .filter(id -> !existingPatternIds.contains(id))
            .collect(Collectors.toSet());

    if (filtersToAdd.isEmpty()) {
      throw new ValidationException(
          "All provided responsePatternIds already exist for this endpoint.");
    }

    // Create new entities for the new filters
    List<UserFilterEntity> toSave =
        filtersToAdd.stream()
            .map(
                pid -> {
                  UserFilterEntity entity = new UserFilterEntity();
                  UserFilterId id = new UserFilterId(userId, pid);
                  entity.setId(id);
                  entity.setUser(user);
                  ResponsePatternEntity pattern =
                      responsePatternRepository
                          .findById(pid)
                          .orElseThrow(
                              () ->
                                  new ServiceNotFoundException(
                                      "ResponsePattern with id " + pid + " not found"));
                  entity.setResponsePattern(pattern);
                  return entity;
                })
            .collect(Collectors.toList());

    userFilterRepository.saveAll(toSave);
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
  public void deleteUserFilter(Integer userId, Integer responsePatternId) {
    if (userId == null || responsePatternId == null) {
      throw new ValidationException("User ID and Response Pattern ID cannot be null.");
    }
    UserFilterId id = new UserFilterId(userId, responsePatternId);
    if (!userFilterRepository.existsById(id)) {
      throw new ServiceNotFoundException("User filter not found.");
    }
    userFilterRepository.deleteById(id);
  }
}
