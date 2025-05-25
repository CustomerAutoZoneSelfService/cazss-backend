package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.RequestUserFilterDTO;
import com.autozone.cazss_backend.DTO.UserFilterDTO;
import com.autozone.cazss_backend.DTO.UserFilterListDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.exceptions.ValidationException;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.service.UserFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** REST controller for managing user filters related to response patterns in endpoints. */
@RestController
@RequestMapping("/responses")
public class UserFilterController {

  private final UserFilterService userFilterService;
  private final EndpointsRepository endpointRepository;

  /**
   * Constructs a new UserFilterController with the required dependencies.
   *
   * @param userFilterService the service used to manage user filters
   * @param endpointRepository the repository used to access endpoint data
   */
  @Autowired
  public UserFilterController(
      UserFilterService userFilterService, EndpointsRepository endpointRepository) {
    this.userFilterService = userFilterService;
    this.endpointRepository = endpointRepository;
  }

  /**
   * Retrieves all user filters for a given endpoint.
   *
   * @param endpointId the ID of the endpoint
   * @return a ResponseEntity containing the user filters or an error message
   */
  @GetMapping("/{endpointId}/user-filters")
  public ResponseEntity<?> getUserFilters(@PathVariable Integer endpointId) {
    try {
      EndpointsEntity endpoint =
          endpointRepository
              .findById(endpointId)
              .orElseThrow(() -> new ServiceNotFoundException("Endpoint not found"));
      if (!endpoint.getActive()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Endpoint is not active");
      }
      UserFilterListDTO response = userFilterService.getUserFiltersByServiceId(endpointId);
      return ResponseEntity.ok(response);
    } catch (ValidationException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (ServiceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  /**
   * Creates user filters for a specific endpoint.
   *
   * @param endpointId the ID of the endpoint from the path
   * @param request the request body containing the user filters to create
   * @return a ResponseEntity indicating success or failure
   */
  @PostMapping("/{endpointId}/user-filters")
  public ResponseEntity<String> createUserFilters(
      @PathVariable Integer endpointId, @RequestBody RequestUserFilterDTO request) {
    try {
      if (request.getEndpointId() == null || !request.getEndpointId().equals(endpointId)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Endpoint ID in path and body must match.");
      }

      // Note: removed check to allow overwrite since service deletes existing filters before
      // creating
      userFilterService.createUserFilters(request);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (ValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (ServiceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Unexpected error: " + e.getMessage());
    }
  }

  /**
   * Deletes a specific user filter from an endpoint based on the response pattern ID.
   *
   * @param endpointId the ID of the endpoint
   * @param request the request body containing the responsePatternId to delete
   * @return a ResponseEntity indicating the result of the operation
   */
  @DeleteMapping("/{endpointId}/user-filters")
  public ResponseEntity<String> deleteUserFilter(
      @PathVariable Integer endpointId, @RequestBody UserFilterDTO request) {

    try {
      EndpointsEntity endpoint =
          endpointRepository
              .findById(endpointId)
              .orElseThrow(() -> new ServiceNotFoundException("Endpoint not found"));

      if (!endpoint.getActive()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Endpoint is not active");
      }

      Integer userId = 90; // Replace with authenticated user ID if available
      Integer responsePatternId = request.getResponsePatternId();

      userFilterService.deleteUserFilter(userId, responsePatternId);
      return ResponseEntity.noContent().build();

    } catch (ValidationException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (ServiceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Unexpected error: " + e.getMessage());
    }
  }
}
