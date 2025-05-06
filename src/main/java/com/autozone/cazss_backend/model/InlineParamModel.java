package com.autozone.cazss_backend.model;

public class InlineParamModel {
  private String key;
  private String value;

  public InlineParamModel() {}

  public InlineParamModel(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
