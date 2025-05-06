package com.autozone.cazss_backend.DTO;

import com.autozone.cazss_backend.enumerator.RequestVariableTypeEnum;

public class RequestVariableDTO {
  private Integer requestVariableId;
  private RequestVariableTypeEnum type;
  private String keyName;
  private String defaultValue;
  private Boolean customizable;
  private String description;

  public RequestVariableDTO() {}

  public RequestVariableDTO(
      Integer requestVariableId,
      RequestVariableTypeEnum type,
      String keyName,
      String defaultValue,
      Boolean customizable,
      String description) {
    this.requestVariableId = requestVariableId;
    this.type = type;
    this.keyName = keyName;
    this.defaultValue = defaultValue;
    this.customizable = customizable;
    this.description = description;
  }

  public Integer getRequestVariableId() {
    return requestVariableId;
  }

  public void setRequestVariableId(Integer requestVariableId) {
    this.requestVariableId = requestVariableId;
  }

  public RequestVariableTypeEnum getType() {
    return type;
  }

  public void setType(RequestVariableTypeEnum type) {
    this.type = type;
  }

  public String getKeyName() {
    return keyName;
  }

  public void setKeyName(String keyName) {
    this.keyName = keyName;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Boolean getCustomizable() {
    return customizable;
  }

  public void setCustomizable(Boolean customizable) {
    this.customizable = customizable;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
