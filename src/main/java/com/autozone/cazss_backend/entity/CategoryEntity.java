package com.autozone.cazss_backend.entity;

import jakarta.persistence.*;

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

    public CategoryEntity(Integer categoryId, String name, String color) {
        this.categoryId = categoryId;
        this.name = name;
        this.color = color;
    }

    public CategoryEntity() {
    }

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
}
