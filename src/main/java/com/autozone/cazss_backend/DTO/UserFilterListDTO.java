package com.autozone.cazss_backend.DTO;

import java.util.List;

/**
 * Data Transfer Object for a list of user filters. This is typically used to represent all active
 * filters for a user on a specific endpoint.
 */
public class UserFilterListDTO {
  private List<UserFilterDTO> userFilters;

  /** Default constructor. */
  public UserFilterListDTO() {}

  /**
   * Constructs a new UserFilterListDTO with the given list of user filters.
   *
   * @param userFilters A list of {@link UserFilterDTO} objects.
   */
  public UserFilterListDTO(List<UserFilterDTO> userFilters) {
    this.userFilters = userFilters;
  }

  /**
   * Gets the list of user filters.
   *
   * @return A list of {@link UserFilterDTO} objects.
   */
  public List<UserFilterDTO> getUserFilters() {
    return userFilters;
  }

  /**
   * Sets the list of user filters.
   *
   * @param userFilters A new list of {@link UserFilterDTO} objects.
   */
  public void setUserFilters(List<UserFilterDTO> userFilters) {
    this.userFilters = userFilters;
  }
}
