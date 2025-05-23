package com.autozone.cazss_backend.projections;

public interface HistoryDetailedProjection {
  Integer getHistoryId();

  Integer getStatusCode();

  Integer getEndpointId();

  String getName();

  String getDescription();

  String getContent();
}
