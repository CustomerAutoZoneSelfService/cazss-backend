package com.autozone.cazss_backend.DTO;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class RequestUserFilterDTO {
  @NotNull private List<Integer> responsePatternIds;
  @NotNull private Integer endpointId;

  public RequestUserFilterDTO() {}

  public RequestUserFilterDTO(List<Integer> responsePatternIds, Integer endpointId) {
    this.responsePatternIds = responsePatternIds;
    this.endpointId = endpointId;
  }

  public List<Integer> getResponsePatternIds() {
    return responsePatternIds;
  }

  public void setResponsePatternIds(List<Integer> responsePatternIds) {
    this.responsePatternIds = responsePatternIds;
  }

  public Integer getEndpointId() {
    return endpointId;
  }

  public void setEndpointId(Integer endpointId) {
    this.endpointId = endpointId;
  }
}
