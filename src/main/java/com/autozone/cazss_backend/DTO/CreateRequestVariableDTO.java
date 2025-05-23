package com.autozone.cazss_backend.DTO;

import com.autozone.cazss_backend.enumerator.RequestVariableTypeEnum;

public class CreateRequestVariableDTO {
  private RequestVariableTypeEnum type;
  private String key;
  private String defaultValue;
  private Boolean customizable;
  private String description;

  public RequestVariableTypeEnum getType() {
    return type;
  }

  public void setType(RequestVariableTypeEnum type) {
    this.type = type;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
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

  @Override
  public String toString() {
    return "CreateRequestVariableDTO{"
        + "type="
        + type
        + ", key='"
        + key
        + '\''
        + ", defaultValue='"
        + defaultValue
        + '\''
        + ", customizable="
        + customizable
        + ", description='"
        + description
        + '\''
        + '}';
  }
}
