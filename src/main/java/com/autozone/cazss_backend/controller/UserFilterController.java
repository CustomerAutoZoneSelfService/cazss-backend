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
   * @param userFilterService The service used to manage user filters.
   * @param endpointRepository The repository used to access endpoint data.
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
   * @param endpointId The ID of the endpoint.
   * @return A {@link ResponseEntity} containing the {@link UserFilterListDTO} with user filters or
   *     an error message. It returns HTTP 200 (OK) on success, HTTP 400 (Bad Request) for
   *     validation errors, and HTTP 404 (Not Found) if the endpoint or user is not found, or if the
   *     endpoint is inactive.
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
   * @param endpointId The ID of the endpoint from the path.
   * @param request The request body containing the {@link RequestUserFilterDTO} with user filters
   *     to create.
   * @return A {@link ResponseEntity} indicating success (HTTP 201 Created) or failure. Returns HTTP
   *     400 (Bad Request) if the endpoint ID in the path and body do not match or for other
   *     validation errors. Returns HTTP 404 (Not Found) if required entities are not found. Returns
   *     HTTP 500 (Internal Server Error) for unexpected errors.
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
   * @param endpointId The ID of the endpoint.
   * @param request The request body {@link UserFilterDTO} containing the responsePatternId of the
   *     filter to delete.
   * @return A {@link ResponseEntity} indicating the result of the operation. Returns HTTP 204 (No
   *     Content) on successful deletion. Returns HTTP 400 (Bad Request) for validation errors.
   *     Returns HTTP 404 (Not Found) if the endpoint or filter is not found, or if the endpoint is
   *     inactive. Returns HTTP 500 (Internal Server Error) for unexpected errors.
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
