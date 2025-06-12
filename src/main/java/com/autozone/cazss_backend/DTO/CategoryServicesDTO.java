package com.autozone.cazss_backend.DTO;

import java.util.List;

public class CategoryServicesDTO {
  private CategoryDTO category;
  private List<ServiceDTO> services;

  public CategoryServicesDTO(CategoryDTO category, List<ServiceDTO> services) {
    this.category = category;
    this.services = services;
  }

  public CategoryDTO getCategory() {
    return category;
  }

  public void setCategory(CategoryDTO category) {
    this.category = category;
  }

  public List<ServiceDTO> getServices() {
    return services;
  }

  public void setServices(List<ServiceDTO> services) {
    this.services = services;
  }
}
