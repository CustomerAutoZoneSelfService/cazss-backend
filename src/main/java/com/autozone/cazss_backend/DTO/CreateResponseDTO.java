package com.autozone.cazss_backend.DTO;

import java.util.List;

public class CreateResponseDTO {
  private Integer statusCode;
  private String description;
  private List<CreateResponsePatternDTO> patterns;

  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<CreateResponsePatternDTO> getPatterns() {
    return patterns;
  }

  public void setPatterns(List<CreateResponsePatternDTO> patterns) {
    this.patterns = patterns;
  }

  @Override
  public String toString() {
    return "CreateResponseDTO{"
        + "statusCode="
        + statusCode
        + ", description='"
        + description
        + '\''
        + ", patterns="
        + patterns
        + '}';
  }
}
