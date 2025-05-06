package com.autozone.cazss_backend.DTO;

import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import java.util.List;

public class ServiceInfoDTO {
  private Integer id;
  private String name;
  private String description;
  private boolean active;
  private EndpointMethodEnum method;
  private String url;
  private List<ServiceResponseDTO> responses;
  private List<FilterDTO> filters;
  private List<RequestVariableDTO> variables;
  private String template;

  public ServiceInfoDTO(
      String name, String description, boolean active, EndpointMethodEnum method, String url) {
    this.name = name;
    this.description = description;
    this.active = active;
    this.method = method;
    this.url = url;
  }

  public ServiceInfoDTO() {}

  public ServiceInfoDTO(
      String name,
      String description,
      boolean active,
      EndpointMethodEnum method,
      String url,
      List<ServiceResponseDTO> responses,
      List<FilterDTO> filters,
      List<RequestVariableDTO> variables,
      String template) {
    this.name = name;
    this.description = description;
    this.active = active;
    this.method = method;
    this.url = url;
    this.responses = responses;
    this.filters = filters;
    this.variables = variables;
    this.template = template;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
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

  public List<ServiceResponseDTO> getResponses() {
    return responses;
  }

  public void setResponses(List<ServiceResponseDTO> responses) {
    this.responses = responses;
  }

  public List<FilterDTO> getFilters() {
    return filters;
  }

  public void setFilters(List<FilterDTO> filters) {
    this.filters = filters;
  }

  public List<RequestVariableDTO> getVariables() {
    return variables;
  }

  public void setVariables(List<RequestVariableDTO> variables) {
    this.variables = variables;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }
}
