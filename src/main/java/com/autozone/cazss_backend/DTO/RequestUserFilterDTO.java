package com.autozone.cazss_backend.DTO;

import jakarta.validation.constraints.NotNull;

public class RequestUserFilterDTO {
  @NotNull private Integer responsePatternId;

  public RequestUserFilterDTO() {}

  public RequestUserFilterDTO(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
  }

  public Integer getResponsePatternId() {
    return responsePatternId;
  }

  public void setResponsePatternId(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
  }
}
