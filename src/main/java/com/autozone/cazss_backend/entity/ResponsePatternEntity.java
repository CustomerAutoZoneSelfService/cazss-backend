package com.autozone.cazss_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Response_patterns", schema = "cazss")
public class ResponsePatternEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "response_pattern_id")
  private Integer responsePatternId;

  @ManyToOne
  @JoinColumn(name = "response_id")
  private ResponseEntity response;

  @Column(nullable = false, length = 1024)
  private String pattern;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(name = "parent_id", nullable = false)
  private Integer parentId;

  @Column(name = "is_leaf", nullable = false)
  private Boolean isLeaf;

  public ResponsePatternEntity() {}

  public ResponsePatternEntity(
      Integer responsePatternId,
      ResponseEntity response,
      String pattern,
      String name,
      String description) {
    this.responsePatternId = responsePatternId;
    this.response = response;
    this.pattern = pattern;
    this.name = name;
    this.description = description;
  }

  public Integer getResponsePatternId() {
    return responsePatternId;
  }

  public void setResponsePatternId(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
  }

  public ResponseEntity getResponse() {
    return response;
  }

  public void setResponse(ResponseEntity response) {
    this.response = response;
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
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

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public Boolean getIsLeaf() {
    return isLeaf;
  }

  public void setIsLeaf(Boolean isLeaf) {
    this.isLeaf = isLeaf;
  }
}
