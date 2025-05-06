package com.autozone.cazss_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Authentication_strategy_attributes", schema = "cazss")
public class AuthenticationStrategyAttributeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "auth_strategy_attribute_id")
  private Integer authStrategyAttributeId;

  @ManyToOne
  @JoinColumn(name = "auth_strategy_id")
  private AuthenticationStrategyEntity authStrategy;

  @Column(name = "key_name", nullable = false)
  private String keyName;

  @Column(nullable = false)
  private String value;

  public AuthenticationStrategyAttributeEntity(
      Integer authStrategyAttributeId,
      AuthenticationStrategyEntity authStrategy,
      String keyName,
      String value) {
    this.authStrategyAttributeId = authStrategyAttributeId;
    this.authStrategy = authStrategy;
    this.keyName = keyName;
    this.value = value;
  }

  public AuthenticationStrategyAttributeEntity() {}

  public Integer getAuthStrategyAttributeId() {
    return authStrategyAttributeId;
  }

  public void setAuthStrategyAttributeId(Integer authStrategyAttributeId) {
    this.authStrategyAttributeId = authStrategyAttributeId;
  }

  public AuthenticationStrategyEntity getAuthStrategy() {
    return authStrategy;
  }

  public void setAuthStrategy(AuthenticationStrategyEntity authStrategy) {
    this.authStrategy = authStrategy;
  }

  public String getKeyName() {
    return keyName;
  }

  public void setKeyName(String keyName) {
    this.keyName = keyName;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
