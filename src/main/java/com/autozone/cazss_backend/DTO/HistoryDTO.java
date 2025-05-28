package com.autozone.cazss_backend.DTO;

import java.time.LocalDateTime;

public class HistoryDTO {
  private Integer historyId;
  private String email;
  private String endpointName;
  private String endpointDescription;
  private LocalDateTime createdAt;

  public HistoryDTO() {}

  public HistoryDTO(
      Integer historyId,
      String email,
      String endpointName,
      String endpointDescription,
      LocalDateTime createdAt) {
    this.historyId = historyId;
    this.email = email;
    this.endpointName = endpointName;
    this.endpointDescription = endpointDescription;
    this.createdAt = createdAt;
  }

  public Integer getHistoryId() {
    return historyId;
  }

  public void setHistoryId(Integer historyId) {
    this.historyId = historyId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEndpointName() {
    return endpointName;
  }

  public void setEndpointName(String endpointName) {
    this.endpointName = endpointName;
  }

  public String getEndpointDescription() {
    return endpointDescription;
  }

  public void setEndpointDescription(String endpointDescription) {
    this.endpointDescription = endpointDescription;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
