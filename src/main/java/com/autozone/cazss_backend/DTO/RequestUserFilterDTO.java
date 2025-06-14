package com.autozone.cazss_backend.DTO;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Data Transfer Object for requesting the creation of user filters. It contains a list of response
 * pattern IDs and the endpoint ID to which they apply.
 */
public class RequestUserFilterDTO {
  @NotNull private List<Integer> responsePatternIds;
  @NotNull private Integer endpointId;

  /** Default constructor. */
  public RequestUserFilterDTO() {}

  /**
   * Constructs a new RequestUserFilterDTO with the specified response pattern IDs and endpoint ID.
   *
   * @param responsePatternIds A list of IDs for the response patterns.
   * @param endpointId The ID of the endpoint.
   */
  public RequestUserFilterDTO(List<Integer> responsePatternIds, Integer endpointId) {
    this.responsePatternIds = responsePatternIds;
    this.endpointId = endpointId;
  }

  /**
   * Gets the list of response pattern IDs.
   *
   * @return A list of response pattern IDs.
   */
  public List<Integer> getResponsePatternIds() {
    return responsePatternIds;
  }

  /**
   * Sets the list of response pattern IDs.
   *
   * @param responsePatternIds A new list of response pattern IDs.
   */
  public void setResponsePatternIds(List<Integer> responsePatternIds) {
    this.responsePatternIds = responsePatternIds;
  }

  /**
   * Gets the endpoint ID.
   *
   * @return The endpoint ID.
   */
  public Integer getEndpointId() {
    return endpointId;
  }

  /**
   * Sets the endpoint ID.
   *
   * @param endpointId The new endpoint ID.
   */
  public void setEndpointId(Integer endpointId) {
    this.endpointId = endpointId;
  }
}
