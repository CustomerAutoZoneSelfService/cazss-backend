package com.autozone.cazss_backend.DTO;

public class HistoryDetailedDTO {
  private Integer historyId;
  private Integer statusCode;
  private ServiceDTO endpoint;
  private HistoryDataDTO historyData;

  public HistoryDetailedDTO() {}

  public HistoryDetailedDTO(
      Integer historyId, Integer statusCode, ServiceDTO endpoint, HistoryDataDTO historyData) {
    this.historyId = historyId;
    this.statusCode = statusCode;
    this.endpoint = endpoint;
    this.historyData = historyData;
  }

  public Integer getHistoryId() {
    return historyId;
  }

  public void setHistoryId(Integer historyId) {
    this.historyId = historyId;
  }

  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public ServiceDTO getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(ServiceDTO endpoint) {
    this.endpoint = endpoint;
  }

  public HistoryDataDTO getHistoryData() {
    return historyData;
  }

  public void setHistoryData(HistoryDataDTO historyData) {
    this.historyData = historyData;
  }
}
