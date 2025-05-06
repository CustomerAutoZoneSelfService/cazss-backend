package com.autozone.cazss_backend.DTO;

public class FilterDTO {
  private Integer responsePatternId;
  private String pattern;
  private String name;
  private String description;

  public FilterDTO(Integer responsePatternId, String pattern, String name, String description) {
    this.responsePatternId = responsePatternId;
    this.pattern = pattern;
    this.name = name;
    this.description = description;
  }

  public FilterDTO() {}

  public Integer getResponsePatternId() {
    return responsePatternId;
  }

  public void setResponsePatternId(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
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
}
