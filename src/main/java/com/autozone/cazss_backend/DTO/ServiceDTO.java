package com.autozone.cazss_backend.DTO;

public class ServiceDTO {
  private Integer endpointId;
  private String name;
  private String description;
  private Integer categoryId;

  public ServiceDTO(Integer endpointId, String name, String description, Integer categoryId) {
    this.endpointId = endpointId;
    this.name = name;
    this.description = description;
    this.categoryId = categoryId;
  }

  public ServiceDTO() {}

  public Integer getEndpointId() {
    return endpointId;
  }

  public void setEndpointId(Integer endpointId) {
    this.endpointId = endpointId;
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

  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }
}
