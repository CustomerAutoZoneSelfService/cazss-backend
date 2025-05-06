package com.autozone.cazss_backend.entity;

import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Users", schema = "cazss")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Integer userId;

  @Column(unique = true)
  private String email;

  @Column(nullable = false)
  private Boolean active;

  @Enumerated(EnumType.STRING)
  private UserRoleEnum role;

  public UserEntity(String email, Boolean active, UserRoleEnum role) {
    this.email = email;
    this.active = active;
    this.role = role;
  }

  public UserEntity() {}

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public UserRoleEnum getRole() {
    return role;
  }

  public void setRole(UserRoleEnum role) {
    this.role = role;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserEntity that)) return false;
    return Objects.equals(userId, that.userId)
        && Objects.equals(email, that.email)
        && Objects.equals(active, that.active)
        && role == that.role;
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, email, active, role);
  }
}
