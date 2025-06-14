package com.autozone.cazss_backend.entity;

import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Endpoints", schema = "cazss")
public class EndpointsEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "endpoint_id")
  private Integer endpointId;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private CategoryEntity category;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Column(nullable = false)
  private Boolean active = true;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EndpointMethodEnum method;

  @Column(nullable = false, length = 2048)
  private String url;

  @ManyToOne
  @JoinColumn(name = "auth_strategy_id")
  private AuthenticationStrategyEntity authStrategy;

  // Add bidirectional relationship
  @OneToOne(mappedBy = "endpoint", cascade = CascadeType.ALL, orphanRemoval = true)
  private RequestBodyEntity requestBody;

  @OneToMany(mappedBy = "endpoint", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ResponseEntity> responses;

  public List<ResponseEntity> getResponses() {
    return responses;
  }

  public void setResponses(List<ResponseEntity> responses) {
    this.responses = responses;
  }

  public EndpointsEntity(
      CategoryEntity category,
      UserEntity user,
      Boolean active,
      String name,
      String description,
      EndpointMethodEnum method,
      String url,
      AuthenticationStrategyEntity authStrategy) {
    this.category = category;
    this.user = user;
    this.active = active;
    this.name = name;
    this.description = description;
    this.method = method;
    this.url = url;
    this.authStrategy = authStrategy;
  }

  public EndpointsEntity() {}

  public Integer getEndpointId() {
    return endpointId;
  }

  public void setEndpointId(Integer endpointId) {
    this.endpointId = endpointId;
  }

  public CategoryEntity getCategory() {
    return category;
  }

  public void setCategory(CategoryEntity category) {
    this.category = category;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public EndpointMethodEnum getMethod() {
    return method;
  }

  public void setMethod(EndpointMethodEnum method) {
    this.method = method;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public AuthenticationStrategyEntity getAuthStrategy() {
    return authStrategy;
  }

  public void setAuthStrategy(AuthenticationStrategyEntity authStrategyId) {
    this.authStrategy = authStrategyId;
  }

  public RequestBodyEntity getRequestBody() {
    return requestBody;
  }

  public void setRequestBody(RequestBodyEntity requestBody) {
    this.requestBody = requestBody;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof EndpointsEntity endpoints)) return false;
    return Objects.equals(endpointId, endpoints.endpointId)
        && Objects.equals(category, endpoints.category)
        && Objects.equals(user, endpoints.user)
        && Objects.equals(active, endpoints.active)
        && Objects.equals(name, endpoints.name)
        && Objects.equals(description, endpoints.description)
        && method == endpoints.method
        && Objects.equals(url, endpoints.url)
        && Objects.equals(authStrategy, endpoints.authStrategy)
        && Objects.equals(requestBody, endpoints.requestBody)
        && Objects.equals(responses, endpoints.responses);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        endpointId,
        category,
        user,
        active,
        name,
        description,
        method,
        url,
        authStrategy,
        requestBody,
        responses);
  }
}
