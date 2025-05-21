package com.autozone.cazss_backend.DTO;

public class UserFilterDTO {
  private Integer responsePatternId;
  private String name;
  private String description;

  public UserFilterDTO(Integer responsePatternId, String name, String description) {
    this.responsePatternId = responsePatternId;
    this.name = name;
    this.description = description;
  }

  public UserFilterDTO() {}

  public Integer getResponsePatternId() {
    return responsePatternId;
  }

  public void setResponsePatternId(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
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
