package com.autozone.cazss_backend.DTO;

import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import java.util.List;

public class CreateServiceDTO {
  // Endpoint
  private Integer categoryId;
  private Boolean active;
  private String name;
  private String description;
  private EndpointMethodEnum method;
  private String url;
  private Integer authenticationStrategy;

  // Template
  private String template;

  // Request variables
  private List<CreateRequestVariableDTO> requestVariables;

  // Responses
  private List<CreateResponseDTO> responses;

  // Getters and setters
  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
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

  public Integer getAuthenticationStrategy() {
    return authenticationStrategy;
  }

  public void setAuthenticationStrategy(Integer authenticationStrategy) {
    this.authenticationStrategy = authenticationStrategy;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public List<CreateRequestVariableDTO> getRequestVariables() {
    return requestVariables;
  }

  public void setRequestVariables(List<CreateRequestVariableDTO> requestVariables) {
    this.requestVariables = requestVariables;
  }

  public List<CreateResponseDTO> getResponses() {
    return responses;
  }

  public void setResponses(List<CreateResponseDTO> responses) {
    this.responses = responses;
  }

  // To string
  @Override
  public String toString() {
    return "CreateServiceDTO{"
        + "categoryId="
        + categoryId
        + ", active="
        + active
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", method="
        + method
        + ", url='"
        + url
        + '\''
        + ", authenticationStrategy="
        + authenticationStrategy
        + ", template='"
        + template
        + '\''
        + ", requestVariables="
        + requestVariables
        + ", responses="
        + responses
        + '}';
  }
}
