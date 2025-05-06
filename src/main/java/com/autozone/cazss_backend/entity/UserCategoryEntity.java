package com.autozone.cazss_backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "User_categories", schema = "cazss")
public class UserCategoryEntity {
  @EmbeddedId private UserCategoryId id;

  @ManyToOne
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @ManyToOne
  @MapsId("categoryId")
  @JoinColumn(name = "category_id")
  private CategoryEntity category;

  public UserCategoryEntity() {}

  public UserCategoryEntity(UserCategoryId id, UserEntity user, CategoryEntity category) {
    this.id = id;
    this.user = user;
    this.category = category;
  }

  public UserCategoryId getId() {
    return id;
  }

  public void setId(UserCategoryId id) {
    this.id = id;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public CategoryEntity getCategory() {
    return category;
  }

  public void setCategory(CategoryEntity category) {
    this.category = category;
  }

  @Embeddable
  public static class UserCategoryId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "category_id")
    private Integer categoryId;

    public UserCategoryId() {}

    public UserCategoryId(Integer userId, Integer categoryId) {
      this.userId = userId;
      this.categoryId = categoryId;
    }

    public Integer getUserId() {
      return userId;
    }

    public void setUserId(Integer userId) {
      this.userId = userId;
    }

    public Integer getCategoryId() {
      return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
      this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      UserCategoryId that = (UserCategoryId) o;
      return Objects.equals(userId, that.userId) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(userId, categoryId);
    }
  }
}
