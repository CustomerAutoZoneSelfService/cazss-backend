package com.autozone.cazss_backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "Favorites", schema = "cazss")
public class FavoriteEntity {
  @EmbeddedId private FavoriteId id;

  @ManyToOne
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @ManyToOne
  @MapsId("endpointId")
  @JoinColumn(name = "endpoint_id")
  private EndpointsEntity endpoint;

  @Embeddable
  public static class FavoriteId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "endpoint_id")
    private Integer endpointId;

    public FavoriteId() {}

    public FavoriteId(Integer userId, Integer endpointId) {
      this.userId = userId;
      this.endpointId = endpointId;
    }

    public Integer getUserId() {
      return userId;
    }

    public void setUserId(Integer userId) {
      this.userId = userId;
    }

    public Integer getEndpointId() {
      return endpointId;
    }

    public void setEndpointId(Integer endpointId) {
      this.endpointId = endpointId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      FavoriteId that = (FavoriteId) o;
      return Objects.equals(userId, that.userId) && Objects.equals(endpointId, that.endpointId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(userId, endpointId);
    }
  }
}
