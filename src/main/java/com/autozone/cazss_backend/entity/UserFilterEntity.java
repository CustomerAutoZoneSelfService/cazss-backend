package com.autozone.cazss_backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "User_filters", schema = "cazss")
public class UserFilterEntity {

  @EmbeddedId private UserFilterId id;

  @ManyToOne
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @ManyToOne
  @MapsId("responsePatternId")
  @JoinColumn(name = "response_pattern_id")
  private ResponsePatternEntity responsePattern;

  // Getters and setters
  public UserFilterId getId() {
    return id;
  }

  public void setId(UserFilterId id) {
    this.id = id;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public ResponsePatternEntity getResponsePattern() {
    return responsePattern;
  }

  public void setResponsePattern(ResponsePatternEntity responsePattern) {
    this.responsePattern = responsePattern;
  }

  /** Embedded composite key class for UserFilterEntity */
  @Embeddable
  public static class UserFilterId implements Serializable {

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "response_pattern_id")
    private Integer responsePatternId;

    // Default constructor
    public UserFilterId() {}

    public UserFilterId(Integer userId, Integer responsePatternId) {
      this.userId = userId;
      this.responsePatternId = responsePatternId;
    }

    // Getters and setters
    public Integer getUserId() {
      return userId;
    }

    public void setUserId(Integer userId) {
      this.userId = userId;
    }

    public Integer getResponsePatternId() {
      return responsePatternId;
    }

    public void setResponsePatternId(Integer responsePatternId) {
      this.responsePatternId = responsePatternId;
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      UserFilterId that = (UserFilterId) o;

      if (!userId.equals(that.userId)) return false;
      return responsePatternId.equals(that.responsePatternId);
    }

    @Override
    public int hashCode() {
      int result = userId.hashCode();
      result = 31 * result + responsePatternId.hashCode();
      return result;
    }
  }
}
