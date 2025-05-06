package com.autozone.cazss_backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Responses", schema = "cazss")
public class ResponseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "response_id")
  private Integer responseId;

  @ManyToOne
  @JoinColumn(name = "endpoint_id")
  private EndpointsEntity endpoint;

  @OneToMany(mappedBy = "response")
  private List<ResponsePatternEntity> responsePatterns;

  @Column(name = "status_code", nullable = false)
  private Integer statusCode;

  @Column(nullable = false)
  private String description;

  public ResponseEntity() {}

  public ResponseEntity(
      Integer responseId, EndpointsEntity endpoint, Integer statusCode, String description) {
    this.responseId = responseId;
    this.endpoint = endpoint;
    this.statusCode = statusCode;
    this.description = description;
  }

  public Integer getResponseId() {
    return responseId;
  }

  public void setResponseId(Integer responseId) {
    this.responseId = responseId;
  }

  public EndpointsEntity getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(EndpointsEntity endpoint) {
    this.endpoint = endpoint;
  }

  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
