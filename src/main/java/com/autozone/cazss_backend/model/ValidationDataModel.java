package com.autozone.cazss_backend.model;

import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.RequestVariableEntity;
import java.util.List;

public class ValidationDataModel {
  private EndpointsEntity endpoint;
  private List<RequestVariableEntity> headerVariables;

  public EndpointsEntity getEndpoint() {
    return endpoint;
  }

  public ValidationDataModel(
      EndpointsEntity endpoint, List<RequestVariableEntity> headerVariables) {
    this.endpoint = endpoint;
    this.headerVariables = headerVariables;
  }

  public void setEndpoint(EndpointsEntity endpoint) {
    this.endpoint = endpoint;
  }

  public List<RequestVariableEntity> getHeaderVariables() {
    return headerVariables;
  }

  public void setHeaderVariables(List<RequestVariableEntity> headerVariables) {
    this.headerVariables = headerVariables;
  }

  public ValidationDataModel() {}
}
