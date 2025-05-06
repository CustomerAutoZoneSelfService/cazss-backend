package com.autozone.cazss_backend.entity;

import com.autozone.cazss_backend.enumerator.AuthStrategyEnum;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Authentication_strategies", schema = "cazss")
public class AuthenticationStrategyEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "auth_strategy_id")
  private Integer authStrategyId;

  @ManyToOne
  @JoinColumn(name = "endpoint_id")
  private EndpointsEntity endpoint;

  @Enumerated(EnumType.STRING)
  private AuthStrategyEnum strategy;

  @OneToMany(mappedBy = "authStrategy", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AuthenticationStrategyAttributeEntity> attributes;

  public AuthenticationStrategyEntity(
      EndpointsEntity endpoint,
      AuthStrategyEnum strategy,
      List<AuthenticationStrategyAttributeEntity> attributes) {
    this.endpoint = endpoint;
    this.strategy = strategy;
    this.attributes = attributes;
  }

  public AuthenticationStrategyEntity() {}

  public Integer getAuthStrategyId() {
    return authStrategyId;
  }

  public void setAuthStrategyId(Integer authStrategyId) {
    this.authStrategyId = authStrategyId;
  }

  public EndpointsEntity getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(EndpointsEntity endpoint) {
    this.endpoint = endpoint;
  }

  public AuthStrategyEnum getStrategy() {
    return strategy;
  }

  public void setStrategy(AuthStrategyEnum strategy) {
    this.strategy = strategy;
  }

  public List<AuthenticationStrategyAttributeEntity> getAttributes() {
    return attributes;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AuthenticationStrategyEntity that)) return false;
    return Objects.equals(authStrategyId, that.authStrategyId)
        && Objects.equals(endpoint, that.endpoint)
        && strategy == that.strategy
        && Objects.equals(attributes, that.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authStrategyId, endpoint, strategy, attributes);
  }

  public void setAttributes(List<AuthenticationStrategyAttributeEntity> attributes) {
    this.attributes = attributes;
  }
}
