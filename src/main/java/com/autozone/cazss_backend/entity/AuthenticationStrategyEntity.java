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

  @Column(nullable = false, unique = true)
  private String name;

  @Enumerated(EnumType.STRING)
  private AuthStrategyEnum strategy;

  @OneToMany(mappedBy = "authStrategy", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AuthenticationStrategyAttributeEntity> attributes;

  public AuthenticationStrategyEntity(
      String name,
      AuthStrategyEnum strategy,
      List<AuthenticationStrategyAttributeEntity> attributes) {
    this.name = name;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public void setAttributes(List<AuthenticationStrategyAttributeEntity> attributes) {
    this.attributes = attributes;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    AuthenticationStrategyEntity that = (AuthenticationStrategyEntity) o;
    return Objects.equals(authStrategyId, that.authStrategyId)
        && Objects.equals(name, that.name)
        && strategy == that.strategy
        && Objects.equals(attributes, that.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authStrategyId, name, strategy, attributes);
  }
}
