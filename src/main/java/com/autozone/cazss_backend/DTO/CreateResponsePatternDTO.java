package com.autozone.cazss_backend.DTO;

public class CreateResponsePatternDTO {
  private Integer responsePatternId;
  private Integer parentId;
  private String name;
  private String description;
  private String pattern;
  private Boolean isLeaf;

  public Integer getResponsePatternId() {
    return responsePatternId;
  }

  public void setResponsePatternId(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
  }

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
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

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public Boolean getIsLeaf() {
    return isLeaf;
  }

  public void setIsLeaf(Boolean leaf) {
    isLeaf = leaf;
  }

  @Override
  public String toString() {
    return "CreateResponsePatternDTO{"
        + "responsePatternId="
        + responsePatternId
        + ", parentId="
        + parentId
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", isLeaf="
        + isLeaf
        + '}';
  }
}
