package com.autozone.cazss_backend.model;

import java.util.List;

public class ServiceInfoRequestModel {
  private List<HeaderModel> headers;
  private List<BodyModel> body;
  private List<InlineParamModel> inline;
  private List<QueryStringModel> queryString;

  public ServiceInfoRequestModel(
      List<HeaderModel> headers,
      List<BodyModel> body,
      List<InlineParamModel> inline,
      List<QueryStringModel> queryString) {
    this.headers = headers;
    this.body = body;
    this.inline = inline;
    this.queryString = queryString;
  }

  public ServiceInfoRequestModel() {}

  public List<HeaderModel> getHeaders() {
    return headers;
  }

  public void setHeaders(List<HeaderModel> headers) {
    this.headers = headers;
  }

  public List<BodyModel> getBody() {
    return body;
  }

  public void setBody(List<BodyModel> body) {
    this.body = body;
  }

  public List<InlineParamModel> getInline() {
    return inline;
  }

  public void setInline(List<InlineParamModel> inline) {
    this.inline = inline;
  }

  public List<QueryStringModel> getQueryString() {
    return queryString;
  }

  public void setQueryString(List<QueryStringModel> queryString) {
    this.queryString = queryString;
  }
}
