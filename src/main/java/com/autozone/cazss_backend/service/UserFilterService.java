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

@Service
public class UserFilterService {

  private final UserFilterRepository userFilterRepository;
  private final ResponsePatternRepository responsePatternRepository;
  private final UserRepository userRepository;

  @Autowired
  public UserFilterService(
      UserFilterRepository userFilterRepository,
      ResponsePatternRepository responsePatternRepository,
      UserRepository userRepository) {
    this.userFilterRepository = userFilterRepository;
    this.responsePatternRepository = responsePatternRepository;
    this.userRepository = userRepository;
  }

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

    // Replace strategy: delete old filters
    userFilterRepository.deleteByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
        userId, endpointId);

    // Create new filter entities
    List<UserFilterEntity> toSave =
        uniquePatternIds.stream()
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
