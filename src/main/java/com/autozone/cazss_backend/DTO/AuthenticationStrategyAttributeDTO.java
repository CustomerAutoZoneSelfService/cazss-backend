package com.autozone.cazss_backend.DTO;

public class AuthenticationStrategyAttributeDTO {

  private Integer authStrategyAttributeId;
  private String key;
  private String value;

  public AuthenticationStrategyAttributeDTO() {}

  public AuthenticationStrategyAttributeDTO(
      Integer authStrategyAttributeId, String key, String value) {
    this.authStrategyAttributeId = authStrategyAttributeId;
    this.key = key;
    this.value = value;
  }

  public Integer getAuthStrategyAttributeId() {
    return authStrategyAttributeId;
  }

  public void setAuthStrategyAttributeId(Integer authStrategyAttributeId) {
    this.authStrategyAttributeId = authStrategyAttributeId;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
