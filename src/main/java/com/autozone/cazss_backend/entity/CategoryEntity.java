package com.autozone.cazss_backend.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Categories", schema = "cazss")
public class CategoryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  private Integer categoryId;

  @Column(unique = true, nullable = false)
  private String name;

  @Column(nullable = false)
  private String color;

  public CategoryEntity(String name, String color) {
    this.name = name;
    this.color = color;
  }

  public CategoryEntity() {}

  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof CategoryEntity that)) return false;
    return Objects.equals(categoryId, that.categoryId)
        && Objects.equals(name, that.name)
        && Objects.equals(color, that.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(categoryId, name, color);
  }
}
