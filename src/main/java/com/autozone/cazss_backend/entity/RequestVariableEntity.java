package com.autozone.cazss_backend.entity;

import com.autozone.cazss_backend.enumerator.RequestVariableTypeEnum;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Request_variables", schema = "cazss")
public class RequestVariableEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "request_variable_id")
  private Integer requestVariableId;

  @ManyToOne
  @JoinColumn(name = "endpoint_id")
  private EndpointsEntity endpoint;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 16, columnDefinition = "VARCHAR(16)")
  private RequestVariableTypeEnum type;

  @Column(name = "key_name", nullable = false)
  private String keyName;

  @Column(name = "default_value")
  private String defaultValue;

  @Column(nullable = false)
  private Boolean customizable = true;

  @Column(nullable = false)
  private String description;

  public RequestVariableEntity() {}

  public RequestVariableEntity(
      EndpointsEntity endpoint,
      RequestVariableTypeEnum type,
      String keyName,
      String defaultValue,
      Boolean customizable,
      String description) {
    this.endpoint = endpoint;
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

  public EndpointsEntity getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(EndpointsEntity endpoint) {
    this.endpoint = endpoint;
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

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof RequestVariableEntity that)) return false;
    return Objects.equals(requestVariableId, that.requestVariableId)
        && Objects.equals(endpoint, that.endpoint)
        && type == that.type
        && Objects.equals(keyName, that.keyName)
        && Objects.equals(defaultValue, that.defaultValue)
        && Objects.equals(customizable, that.customizable)
        && Objects.equals(description, that.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        requestVariableId, endpoint, type, keyName, defaultValue, customizable, description);
  }
}
