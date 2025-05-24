package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.RequestUserFilterDTO;
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

@RestController
@RequestMapping("/responses")
public class UserFilterController {

  private final UserFilterService userFilterService;
  private final EndpointsRepository endpointRepository;

  @Autowired
  public UserFilterController(
      UserFilterService userFilterService, EndpointsRepository endpointRepository) {
    this.userFilterService = userFilterService;
    this.endpointRepository = endpointRepository;
  }

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
  
  @PostMapping("/{endpointId}/user-filters")
  public ResponseEntity<String> createUserFilters(
      @PathVariable Integer endpointId, @RequestBody RequestUserFilterDTO request) {
    try {
      if (request.getEndpointId() == null || !request.getEndpointId().equals(endpointId)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Endpoint ID in path and body must match.");
      }

      if (!userFilterService.getUserFiltersByServiceId(endpointId).getUserFilters().isEmpty()) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body("User filters already exist for this endpoint.");
      }

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
}
