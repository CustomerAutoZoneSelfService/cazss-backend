package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.autozone.cazss_backend.DTO.UserFilterDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.ResponseEntity;
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
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserFilterServiceTest {

  @Mock private UserFilterRepository userFilterRepository;
  @Mock private ResponsePatternRepository responsePatternRepository;
  @Mock private UserRepository userRepository;
  @Mock private EndpointsRepository endpointsRepository;

  @InjectMocks private UserFilterService userFilterService;

  private UserEntity mockUser;
  private ResponsePatternEntity mockPattern1;
  private ResponsePatternEntity mockPattern2;
  private UserFilterEntity mockUserFilter;
  private EndpointsEntity mockEndpoint;
  private ResponseEntity mockResponse;

  @BeforeEach
  void setUp() {
    mockUser = new UserEntity();
    mockUser.setUserId(90);

    mockEndpoint = new EndpointsEntity();
    mockEndpoint.setEndpointId(1);

    mockResponse = new ResponseEntity();
    mockResponse.setResponseId(1);
    mockResponse.setEndpoint(mockEndpoint);

    mockPattern1 = new ResponsePatternEntity();
    mockPattern1.setResponsePatternId(1);
    mockPattern1.setResponse(mockResponse);

    mockPattern2 = new ResponsePatternEntity();
    mockPattern2.setResponsePatternId(2);
    mockPattern2.setResponse(mockResponse);

    mockUserFilter = new UserFilterEntity();
    mockUserFilter.setId(new UserFilterId(90, 1));
    mockUserFilter.setUser(mockUser);
    mockUserFilter.setResponsePattern(mockPattern1);
  }

  @Test
  void getUserFiltersByServiceId_Success() {
    when(userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            90, 1))
        .thenReturn(List.of(mockUserFilter));

    List<UserFilterDTO> result = userFilterService.getUserFiltersByServiceId(1);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1, result.get(0).getResponsePatternId());
  }

  @Test
  void getUserFiltersByServiceId_NullServiceId() {
    assertThrows(
        ValidationException.class, () -> userFilterService.getUserFiltersByServiceId(null));
  }

  @Test
  void createUserFilters_Success() {
    List<Integer> patternIds = List.of(1, 2);

    when(userRepository.findById(90)).thenReturn(Optional.of(mockUser));
    when(endpointsRepository.findById(1)).thenReturn(Optional.of(mockEndpoint));
    when(userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            90, 1))
        .thenReturn(Collections.emptyList());
    when(responsePatternRepository.findById(1)).thenReturn(Optional.of(mockPattern1));
    when(responsePatternRepository.findById(2)).thenReturn(Optional.of(mockPattern2));

    List<UserFilterDTO> result = userFilterService.createUserFilters(1, patternIds);

    assertNotNull(result);
    verify(userFilterRepository).saveAll(any());
  }

  @Test
  void createUserFilters_NullInputs() {
    assertThrows(ValidationException.class, () -> userFilterService.createUserFilters(null, null));
  }

  @Test
  void createUserFilters_DuplicatePatternIds() {
    List<Integer> duplicateIds = List.of(1, 1);
    assertThrows(
        ValidationException.class, () -> userFilterService.createUserFilters(1, duplicateIds));
  }

  @Test
  void createUserFilters_AllAlreadyExist() {
    List<Integer> patternIds = List.of(1);

    when(userRepository.findById(90)).thenReturn(Optional.of(mockUser));
    when(endpointsRepository.findById(1)).thenReturn(Optional.of(mockEndpoint));
    when(userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            90, 1))
        .thenReturn(List.of(mockUserFilter));

    assertThrows(
        ValidationException.class, () -> userFilterService.createUserFilters(1, patternIds));
  }

  @Test
  void createUserFilters_PatternNotFound() {
    List<Integer> patternIds = List.of(99);

    when(userRepository.findById(90)).thenReturn(Optional.of(mockUser));
    when(endpointsRepository.findById(1)).thenReturn(Optional.of(mockEndpoint));
    when(userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            90, 1))
        .thenReturn(Collections.emptyList());
    when(responsePatternRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(
        ServiceNotFoundException.class, () -> userFilterService.createUserFilters(1, patternIds));
  }

  @Test
  void createUserFilters_UserNotFound() {
    List<Integer> patternIds = List.of(1);

    when(userRepository.findById(90)).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class, () -> userFilterService.createUserFilters(1, patternIds));
  }

  @Test
  void createUserFilters_ServiceNotFound() {
    List<Integer> patternIds = List.of(1);

    when(userRepository.findById(90)).thenReturn(Optional.of(mockUser));
    when(endpointsRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(
        ServiceNotFoundException.class, () -> userFilterService.createUserFilters(1, patternIds));
  }
}
