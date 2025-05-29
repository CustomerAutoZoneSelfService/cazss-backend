package com.autozone.cazss_backend.DTO;

/**
 * Data Transfer Object for user filter information. Represents a single filter criterion based on a
 * response pattern.
 */
public class UserFilterDTO {
  private Integer responsePatternId;

  /**
   * Constructs a new UserFilterDTO with the specified response pattern ID.
   *
   * @param responsePatternId The ID of the response pattern.
   */
  public UserFilterDTO(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
  }

  /** Default constructor. */
  public UserFilterDTO() {}

  /**
   * Gets the ID of the response pattern.
   *
   * @return The response pattern ID.
   */
  public Integer getResponsePatternId() {
    return responsePatternId;
  }

  /**
   * Sets the ID of the response pattern.
   *
   * @param responsePatternId The new response pattern ID.
   */
  public void setResponsePatternId(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
  }
}
