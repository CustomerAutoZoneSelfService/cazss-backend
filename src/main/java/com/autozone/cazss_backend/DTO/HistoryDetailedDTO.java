package com.autozone.cazss_backend.DTO;

import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.HistoryDataEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryDetailedDTO {
  private Integer historyId;
  private UserEntity userId;
  private EndpointsEntity endpointId;
  private Integer statusCode;
  private LocalDateTime createdAt;
  private List<HistoryDataEntity> historyData;

  public HistoryDetailedDTO() {}

  public HistoryDetailedDTO(
      Integer historyId,
      UserEntity userId,
      EndpointsEntity endpointId,
      Integer statusCode,
      LocalDateTime createdAt,
      List<HistoryDataEntity> historyData) {
    this.historyId = historyId;
    this.userId = userId;
    this.endpointId = endpointId;
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

  public UserEntity getUserId() {
    return userId;
  }

  public void setUserId(UserEntity userId) {
    this.userId = userId;
  }

  public EndpointsEntity getEndpointId() {
    return endpointId;
  }

  public void setEndpointId(EndpointsEntity endpointId) {
    this.endpointId = endpointId;
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
