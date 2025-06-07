package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.UserFilterDTO;
import com.autozone.cazss_backend.service.UserFilterService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** REST controller for managing user filters related to response patterns in endpoints. */
@RestController
@RequestMapping("/responses")
public class UserFilterController {

  @Autowired private UserFilterService userFilterService;

  /**
   * Retrieves all user filters for a given endpoint.
   *
   * @param serviceId The ID of the endpoint.
   * @return A {@link ResponseEntity} containing a list of {@link UserFilterDTO} with user filters
   *     or an error message. It returns HTTP 200 (OK) on success, HTTP 400 (Bad Request) for
   *     validation errors, and HTTP 404 (Not Found) if the endpoint or user is not found, or if the
   *     endpoint is inactive.
   */
  @GetMapping("/{serviceId}/user-filters")
  public ResponseEntity<List<UserFilterDTO>> getUserFilters(@PathVariable Integer serviceId) {
    return ResponseEntity.ok(userFilterService.getUserFiltersByServiceId(serviceId));
  }

  /**
   * Creates new user filters for the given endpoint.
   *
   * @param serviceId The ID of the endpoint.
   * @param patternIds List of response pattern IDs to associate with the user.
   * @return The created user filters with HTTP 201 Created.
   */
  @PostMapping("/{serviceId}/user-filters")
  public ResponseEntity<List<UserFilterDTO>> createUserFilters(
      @PathVariable Integer serviceId, @RequestBody List<Integer> patternIds) {
    return new ResponseEntity<>(
        userFilterService.createUserFilters(serviceId, patternIds), HttpStatus.CREATED);
  }

  /**
   * Updates user filters for a specific endpoint.
   *
   * @param serviceId The ID of the endpoint.
   * @param request The list of updated filter IDs.
   * @return A {@link ResponseEntity} indicating success (HTTP 200 OK) or failure.
   */
  @PutMapping("/{serviceId}/user-filters")
  public ResponseEntity<List<UserFilterDTO>> updateUserFilters(
      @PathVariable Integer serviceId, @RequestBody List<Integer> patternIds) {
    return new ResponseEntity<>(
        userFilterService.updateUserFilters(serviceId, patternIds), HttpStatus.OK);
  }

  /**
   * Replaces user filters for the given endpoint with a new set.
   *
   * @param serviceId The ID of the endpoint.
   * @param patternIds New list of response pattern IDs to associate with the user.
   * @return The updated list with HTTP 200 OK.
   */
  @DeleteMapping("/{serviceId}/user-filters")
  public ResponseEntity<Void> deleteUserFilter(
      @PathVariable Integer serviceId, @RequestBody Integer patternIds) {
    userFilterService.deleteUserFilter(serviceId, patternIds);
    return ResponseEntity.noContent().build();
  }
}
