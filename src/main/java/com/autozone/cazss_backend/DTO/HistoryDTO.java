package com.autozone.cazss_backend.DTO;

import java.time.LocalDateTime;

public class HistoryDTO { // history_id, endpointDTO & created_at
  private Integer historyId;
  private ServiceDTO endpoint;
  private LocalDateTime createdAt;

  public HistoryDTO() {}

  public HistoryDTO(Integer historyId, ServiceDTO endpoint, LocalDateTime createdAt) {
    this.historyId = historyId;
    this.endpoint = endpoint;
    this.createdAt = createdAt;
  }

  public Integer getHistoryId() {
    return historyId;
  }

  public void setHistoryId(Integer historyId) {
    this.historyId = historyId;
  }

  public ServiceDTO getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(ServiceDTO endpoint) {
    this.endpoint = endpoint;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
