package com.autozone.cazss_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResponsePatternDTO {

  private Integer responsePatternId;

  @NotNull(message = "responseId es obligatorio")
  private Integer responseId;

  @NotBlank(message = "pattern no debe estar vacio")
  @Size(max = 1024, message = "pattern no debe exceder 1024 caracteres")
  private String pattern;

  @NotBlank(message = "name no debe estar vacio")
  private String name;

  @NotBlank(message = "description no debe estar vacio")
  private String description;

  private Integer parentId;

  @NotNull(message = "isLeaf debe especificarse")
  private Boolean isLeaf;

  public ResponsePatternDTO() {}

  public Integer getResponsePatternId() {
    return responsePatternId;
  }

  public void setResponsePatternId(Integer responsePatternId) {
    this.responsePatternId = responsePatternId;
  }

  public Integer getResponseId() {
    return responseId;
  }

  public void setResponseId(Integer responseId) {
    this.responseId = responseId;
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
