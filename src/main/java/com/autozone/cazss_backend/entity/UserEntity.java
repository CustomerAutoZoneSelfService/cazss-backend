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

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "username", unique = true, nullable = false)
  private String username;

  public UserEntity(
      String email, Boolean active, UserRoleEnum role, String password, String username) {
    this.email = email;
    this.active = active;
    this.role = role;
    this.password = password;
    this.username = username;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserEntity that)) return false;
    return Objects.equals(userId, that.userId)
        && Objects.equals(email, that.email)
        && Objects.equals(active, that.active)
        && role == that.role
        && Objects.equals(password, that.password)
        && Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, email, active, role, password, username);
  }
}
