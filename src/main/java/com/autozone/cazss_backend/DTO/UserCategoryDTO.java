package com.autozone.cazss_backend.DTO;

public class UserCategoryDTO {
  private Integer userId;
  private Integer endpointId;

  public UserCategoryDTO() {}

  public UserCategoryDTO(Integer userId, Integer endpointId) {
    this.userId = userId;
    this.endpointId = endpointId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getEndpointId() {
    return endpointId;
  }

  public void setEndpointId(Integer endpointId) {
    this.endpointId = endpointId;
  }
}
