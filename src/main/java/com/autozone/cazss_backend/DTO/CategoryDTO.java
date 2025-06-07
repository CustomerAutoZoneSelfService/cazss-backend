package com.autozone.cazss_backend.DTO;

import com.autozone.cazss_backend.entity.CategoryEntity;
import jakarta.validation.constraints.NotBlank;

public class CategoryDTO {
  private Integer categoryId;

  @NotBlank(message = "Category name is required")
  private String name;

  @NotBlank(message = "Category color is required")
  private String color;

  public CategoryDTO() {}

  public CategoryDTO(Integer categoryId, String name, String color) {
    this.categoryId = categoryId;
    this.name = name;
    this.color = color;
  }

  public CategoryDTO(CategoryEntity entity) {
    this.categoryId = entity.getCategoryId();
    this.name = entity.getName();
    this.color = entity.getColor();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }
}
