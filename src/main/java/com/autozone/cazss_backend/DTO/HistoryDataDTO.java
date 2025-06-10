package com.autozone.cazss_backend.DTO;

public class HistoryDataDTO {
  private Object request;
  private Object response;

  public HistoryDataDTO() {}

  public HistoryDataDTO(Object request, Object response) {
    this.request = request;
    this.response = response;
  }

  public Object getRequest() {
    return request;
  }

  public void setRequest(Object request) {
    this.request = request;
  }

  public Object getResponse() {
    return response;
  }

  public void setResponse(Object response) {
    this.response = response;
  }
}
