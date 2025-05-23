package com.autozone.cazss_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResponsePatternDTO {

  private Integer responsePatternId;

  @NotNull(message = "responseId is required")
  private Integer responseId;

  @NotBlank(message = "pattern shouldn't be blank")
  @Size(max = 1024, message = "pattern must not exceed 1024 characters")
  private String pattern;

  @NotBlank(message = "name shouldn't be blank")
  private String name;

  @NotBlank(message = "description shouldn't be blank")
  private String description;

  private Integer parentId;

  @NotNull(message = "isLeaf is required")
  private Boolean isLeaf;

  public ResponsePatternDTO() {}

  public ResponsePatternDTO(
      Integer responsePatternId,
      Integer responseId,
      String pattern,
      String name,
      String description,
      Integer parentId,
      Boolean isLeaf) {
    this.responsePatternId = responsePatternId;
    this.responseId = responseId;
    this.pattern = pattern;
    this.name = name;
    this.description = description;
    this.parentId = parentId;
    this.isLeaf = isLeaf;
  }

  public Integer getResponsePatternId() {
    return responsePatternId;
  }

  public void setResponsePatternId(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
  }

  public Integer getResponseId() {
    return responseId;
  }

  public void setResponseId(Integer responseId) {
    this.responseId = responseId;
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public Boolean getIsLeaf() {
    return isLeaf;
  }

  public void setIsLeaf(Boolean isLeaf) {
    this.isLeaf = isLeaf;
  }

  @Override
  public String toString() {
    return "ResponsePatternDTO{"
        + "responseId="
        + responseId
        + ", responsePatternid='"
        + responsePatternId
        + '\''
        + ", pattern='"
        + pattern
        + '\''
        + ", name="
        + name
        + ", description='"
        + description
        + '\''
        + ", parentId"
        + parentId
        + '\''
        + ", isLeaf"
        + isLeaf
        + '\''
        + '}';
  }
}
