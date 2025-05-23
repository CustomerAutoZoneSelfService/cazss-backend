package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.RequestUserFilterDTO;
import com.autozone.cazss_backend.DTO.UserFilterDTO;
import com.autozone.cazss_backend.DTO.UserFilterListDTO;
import com.autozone.cazss_backend.entity.UserFilterEntity;
import com.autozone.cazss_backend.entity.UserFilterEntity.UserFilterId;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.exceptions.ValidationException;
import com.autozone.cazss_backend.repository.UserFilterRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

  public UserFilterListDTO getUserFiltersByServiceId(Integer endpointId) {

    Integer userId = 1; // Replace with actual user ID retrieval logic

    if (userId == null) {
      throw new ValidationException("User cannot be null");
    }
    if (endpointId == null) {
      throw new ValidationException("EndpointId cannot be null");
    }

    List<UserFilterEntity> entities =
        userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            userId, endpointId);

    List<UserFilterDTO> userFilterDTOList =
        entities.stream()
            .map(entity -> new UserFilterDTO(entity.getId().getResponsePatternId()))
            .collect(Collectors.toList());

    return new UserFilterListDTO(userFilterDTOList);
  }

  @Transactional
  public void createUserFilters(RequestUserFilterDTO request) {
    Integer userId = 1; // Hardcoded user for MBI I
    Integer endpointId = request.getEndpointId();
    List<Integer> responsePatternIds = request.getResponsePatternIds();

    if (endpointId == null || responsePatternIds == null || responsePatternIds.isEmpty()) {
      throw new ValidationException("EndpointId and patternIds must be provided");
    }

    // Remove duplicates
    Set<Integer> uniquePatternIds = new HashSet<>(responsePatternIds);
    if (uniquePatternIds.size() != responsePatternIds.size()) {
      throw new ValidationException("Duplicate responsePatternIds are not allowed");
    }

    // Validate that all patternIds belong to the endpoint
    List<Integer> validPatternIds =
        userFilterRepository.findByResponsePattern_Response_Endpoint_EndpointId(endpointId).stream()
            .map(rp -> rp.getResponsePatternId())
            .collect(Collectors.toList());
    if (!validPatternIds.containsAll(uniquePatternIds)) {
      throw new ServiceNotFoundException(
          "Some responsePatternIds do not belong to the given endpoint");
    }

    // Delete old filters
    userFilterRepository.deleteByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
        userId, endpointId);

    // Insert new ones
    List<UserFilterEntity> toSave =
        uniquePatternIds.stream()
            .map(
                pid -> {
                  UserFilterEntity entity = new UserFilterEntity();
                  entity.setId(new UserFilterId(userId, pid));
                  return entity;
                })
            .collect(Collectors.toList());

    userFilterRepository.saveAll(toSave);
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
