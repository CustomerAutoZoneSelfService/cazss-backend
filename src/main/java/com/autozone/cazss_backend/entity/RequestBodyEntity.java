package com.autozone.cazss_backend.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Request_body", schema = "cazss")
public class RequestBodyEntity {
  @Id
  @Column(name = "endpoint_id")
  private Integer endpointId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "endpoint_id")
  private EndpointsEntity endpoint;

  @Lob
  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String template;

  public RequestBodyEntity(EndpointsEntity endpoint, String template) {
    this.endpoint = endpoint;
    this.template = template;
    if (endpoint != null) {
      endpoint.setRequestBody(this);
    }
  }

  public RequestBodyEntity() {}

  public Integer getEndpointId() {
    return endpointId;
  }

  public void setEndpointId(Integer endpointId) {
    this.endpointId = endpointId;
  }

  public EndpointsEntity getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(EndpointsEntity endpoint) {
    // Remove this entity from the old endpoint's relationship if it exists
    if (this.endpoint != null && this.endpoint != endpoint) {
      this.endpoint.setRequestBody(null);
    }

    this.endpoint = endpoint;

    // Add this entity to the new endpoint's relationship
    if (endpoint != null && endpoint.getRequestBody() != this) {
      endpoint.setRequestBody(this);
    }
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof RequestBodyEntity that)) return false;
    return Objects.equals(endpointId, that.endpointId)
        && Objects.equals(endpoint, that.endpoint)
        && Objects.equals(template, that.template);
  }

  @Override
  public int hashCode() {
    return Objects.hash(endpointId, endpoint, template);
  }
}
