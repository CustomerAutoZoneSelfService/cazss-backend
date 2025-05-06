package com.autozone.cazss_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
    name = "History",
    schema = "cazss",
    indexes = {@Index(name = "idx_user_endpoint", columnList = "user_id, endpoint_id")})
public class HistoryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "history_id")
  private Integer historyId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @ManyToOne
  @JoinColumn(name = "endpoint_id")
  private EndpointsEntity endpoint;

  @Column(name = "status_code", nullable = false)
  private Integer statusCode;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<HistoryDataEntity> historyData;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public HistoryEntity() {}

  public HistoryEntity(
      Integer historyId,
      UserEntity user,
      EndpointsEntity endpoint,
      Integer statusCode,
      LocalDateTime createdAt,
      List<HistoryDataEntity> historyData) {
    this.historyId = historyId;
    this.user = user;
    this.endpoint = endpoint;
    this.statusCode = statusCode;
    this.createdAt = createdAt;
    this.historyData = historyData;
  }

  public Integer getHistoryId() {
    return historyId;
  }

  public void setHistoryId(Integer historyId) {
    this.historyId = historyId;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public EndpointsEntity getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(EndpointsEntity endpoint) {
    this.endpoint = endpoint;
  }

  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public List<HistoryDataEntity> getHistoryData() {
    return historyData;
  }

  public void setHistoryData(List<HistoryDataEntity> historyData) {
    this.historyData = historyData;
  }
}
