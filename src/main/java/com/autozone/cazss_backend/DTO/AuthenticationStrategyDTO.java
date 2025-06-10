package com.autozone.cazss_backend.DTO;

import java.util.List;

public class AuthenticationStrategyDTO {

  private Integer authStrategyId;
  private String name;
  private String type;

  private List<AuthenticationStrategyAttributeDTO> attributes;

  // Constructors
  public AuthenticationStrategyDTO() {}

  public AuthenticationStrategyDTO(
      Integer authStrategyId,
      String name,
      String type,
      List<AuthenticationStrategyAttributeDTO> attributes) {
    this.authStrategyId = authStrategyId;
    this.name = name;
    this.type = type;
    this.attributes = attributes;
  }

  // Getters and Setters
  public Integer getAuthStrategyId() {
    return authStrategyId;
  }

  public void setAuthStrategyId(Integer authStrategyId) {
    this.authStrategyId = authStrategyId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<AuthenticationStrategyAttributeDTO> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<AuthenticationStrategyAttributeDTO> attributes) {
    this.attributes = attributes;
  }
}
