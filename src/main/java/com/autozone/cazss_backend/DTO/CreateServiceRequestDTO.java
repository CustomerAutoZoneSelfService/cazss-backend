package com.autozone.cazss_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** DTO for receiving new service creation requests. */
public class CreateServiceRequestDTO {

  @NotBlank(message = "Service name is required.")
  @Size(max = 100, message = "Service name must be 100 characters or fewer.")
  private String name;

  @Size(max = 255, message = "Description must be 255 characters or fewer.")
  private String description;

  @NotNull(message = "Service type is required.")
  private String type;

  // Getters and setters
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
