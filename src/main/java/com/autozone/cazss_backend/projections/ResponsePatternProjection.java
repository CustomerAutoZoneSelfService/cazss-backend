package com.autozone.cazss_backend.projections;

public interface ResponsePatternProjection {
  Integer getResponsePatternId();

  Integer getResponseId();

  String getPattern();

  String getName();

  String getDescription();

  Integer getParentId();

  Boolean getIsLeaf();
}
