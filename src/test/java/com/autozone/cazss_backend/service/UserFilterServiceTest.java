// package com.autozone.cazss_backend.service;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// import com.autozone.cazss_backend.DTO.RequestUserFilterDTO;
// import com.autozone.cazss_backend.DTO.UserFilterListDTO;
// import com.autozone.cazss_backend.entity.EndpointsEntity;
// import com.autozone.cazss_backend.entity.ResponseEntity;
// import com.autozone.cazss_backend.entity.ResponsePatternEntity;
// import com.autozone.cazss_backend.entity.UserEntity;
// import com.autozone.cazss_backend.entity.UserFilterEntity;
// import com.autozone.cazss_backend.entity.UserFilterEntity.UserFilterId;
// import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
// import com.autozone.cazss_backend.exceptions.ValidationException;
// import com.autozone.cazss_backend.repository.ResponsePatternRepository;
// import com.autozone.cazss_backend.repository.UserFilterRepository;
// import com.autozone.cazss_backend.repository.UserRepository;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.Optional;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// /**
// * Test class for UserFilterService. Uses Mockito for mocking dependencies and
// JUnit 5 for testing.
// */
// @ExtendWith(MockitoExtension.class)
// class UserFilterServiceTest {

// // Mock all repository dependencies that UserFilterService needs
// @Mock private UserFilterRepository userFilterRepository;
// @Mock private ResponsePatternRepository responsePatternRepository;
// @Mock private UserRepository userRepository;

// // Inject the mocks into the service we're testing
// @InjectMocks private UserFilterService userFilterService;

// // Test objects that will be reused across test methods
// private UserEntity mockUser;
// private ResponsePatternEntity mockPattern1;
// private ResponsePatternEntity mockPattern2;
// private UserFilterEntity mockUserFilter;
// private EndpointsEntity mockEndpoint;
// private ResponseEntity mockResponse;

// /**
// * Set up test data before each test method. This creates fresh instances of
// all mock objects to
// * ensure test isolation.
// */
// @BeforeEach
// void setUp() {
// // Create a mock user with ID 90 (hardcoded in service)
// mockUser = new UserEntity();
// mockUser.setUserId(90);

// // Create a mock endpoint with ID 1
// mockEndpoint = new EndpointsEntity();
// mockEndpoint.setEndpointId(1);

// // Create a mock response linked to the endpoint
// mockResponse = new ResponseEntity();
// mockResponse.setResponseId(1);
// mockResponse.setEndpoint(mockEndpoint);

// // Create two mock response patterns linked to the response
// mockPattern1 = new ResponsePatternEntity();
// mockPattern1.setResponsePatternId(1);
// mockPattern1.setResponse(mockResponse);

// mockPattern2 = new ResponsePatternEntity();
// mockPattern2.setResponsePatternId(2);
// mockPattern2.setResponse(mockResponse);

// // Create a mock user filter linking user and pattern1
// mockUserFilter = new UserFilterEntity();
// mockUserFilter.setId(new UserFilterId(90, 1));
// mockUserFilter.setUser(mockUser);
// mockUserFilter.setResponsePattern(mockPattern1);
// }

// /**
// * Test successful retrieval of user filters by service ID. Should return a
// list containing one
// * filter.
// */
// @Test
// void getUserFiltersByServiceId_Success() {
// // Arrange: Set up mock repository responses
// when(userRepository.findById(90)).thenReturn(Optional.of(mockUser));
// when(userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
// 90, 1))
// .thenReturn(Arrays.asList(mockUserFilter));

// // Act: Call the service method
// UserFilterListDTO result = userFilterService.getUserFiltersByServiceId(1);

// // Assert: Verify the results
// assertNotNull(result);
// assertEquals(1, result.getUserFilters().size());
// assertEquals(1, result.getUserFilters().get(0).getResponsePatternId());
// }

// /** Test validation when endpoint ID is null. Should throw
// ValidationException. */
// @Test
// void getUserFiltersByServiceId_NullEndpointId() {
// assertThrows(
// ValidationException.class, () ->
// userFilterService.getUserFiltersByServiceId(null));
// }

// /** Test case when user is not found. Should throw ServiceNotFoundException.
// */
// @Test
// void getUserFiltersByServiceId_UserNotFound() {
// // Arrange: Mock user repository to return empty
// when(userRepository.findById(90)).thenReturn(Optional.empty());

// // Act & Assert: Verify exception is thrown
// assertThrows(
// ServiceNotFoundException.class, () ->
// userFilterService.getUserFiltersByServiceId(1));
// }

// /**
// * Test successful creation of user filters. Should save new filters without
// throwing exceptions.
// */
// @Test
// void createUserFilters_Success() {
// // Arrange: Create request DTO and set up mock responses
// RequestUserFilterDTO request = new RequestUserFilterDTO();
// request.setEndpointId(1);
// request.setResponsePatternIds(Arrays.asList(1, 2));

// when(userRepository.findById(90)).thenReturn(Optional.of(mockUser));
// when(responsePatternRepository.findByResponse_Endpoint_EndpointId(1))
// .thenReturn(Arrays.asList(mockPattern1, mockPattern2));
// when(responsePatternRepository.findById(1)).thenReturn(Optional.of(mockPattern1));
// when(responsePatternRepository.findById(2)).thenReturn(Optional.of(mockPattern2));
// when(userFilterRepository.findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
// 90, 1))
// .thenReturn(Collections.emptyList());

// // Act & Assert: Verify no exceptions and saveAll is called
// assertDoesNotThrow(() -> userFilterService.createUserFilters(request));
// verify(userFilterRepository).saveAll(any());
// }

// /** Test validation when request contains null values. Should throw
// ValidationException. */
// @Test
// void createUserFilters_ValidationFailure() {
// RequestUserFilterDTO request = new RequestUserFilterDTO();
// request.setEndpointId(null);
// request.setResponsePatternIds(null);

// assertThrows(ValidationException.class, () ->
// userFilterService.createUserFilters(request));
// }

// /**
// * Test validation when request contains duplicate pattern IDs. Should throw
// ValidationException.
// */
// @Test
// void createUserFilters_DuplicatePatternIds() {
// RequestUserFilterDTO request = new RequestUserFilterDTO();
// request.setEndpointId(1);
// request.setResponsePatternIds(Arrays.asList(1, 1)); // Duplicate IDs

// assertThrows(ValidationException.class, () ->
// userFilterService.createUserFilters(request));
// }

// /**
// * Test validation when request contains invalid pattern IDs. Should throw
// * ServiceNotFoundException.
// */
// @Test
// void createUserFilters_InvalidPatternIds() {
// // Arrange: Create request with pattern IDs where one doesn't exist
// RequestUserFilterDTO request = new RequestUserFilterDTO();
// request.setEndpointId(1);
// request.setResponsePatternIds(Arrays.asList(1, 2));

// when(userRepository.findById(90)).thenReturn(Optional.of(mockUser));
// // Return only one pattern, making the second ID invalid
// when(responsePatternRepository.findByResponse_Endpoint_EndpointId(1))
// .thenReturn(Collections.singletonList(mockPattern1));

// assertThrows(
// ServiceNotFoundException.class, () ->
// userFilterService.createUserFilters(request));
// }

// /**
// * Test successful deletion of a user filter. Should delete filter without
// throwing exceptions.
// */
// @Test
// void deleteUserFilter_Success() {
// when(userFilterRepository.existsById(any(UserFilterId.class))).thenReturn(true);

// assertDoesNotThrow(() -> userFilterService.deleteUserFilter(90, 1));
// verify(userFilterRepository).deleteById(any(UserFilterId.class));
// }

// /** Test validation when delete request contains null values. Should throw
// ValidationException. */
// @Test
// void deleteUserFilter_ValidationFailure() {
// assertThrows(ValidationException.class, () ->
// userFilterService.deleteUserFilter(null, null));
// }

// /** Test deletion when filter doesn't exist. Should throw
// ServiceNotFoundException. */
// @Test
// void deleteUserFilter_NotFound() {
// when(userFilterRepository.existsById(any(UserFilterId.class))).thenReturn(false);

// assertThrows(ServiceNotFoundException.class, () ->
// userFilterService.deleteUserFilter(90, 1));
// }
// }
