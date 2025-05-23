package com.autozone.cazss_backend.DTO;

public class UserFilterDTO {
  private Integer responsePatternId;

  public UserFilterDTO(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
  }

  public UserFilterDTO() {}

  public Integer getResponsePatternId() {
    return responsePatternId;
  }

  public void setResponsePatternId(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
  }
}
