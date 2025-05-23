package com.autozone.cazss_backend.projections;

import java.time.LocalDateTime;

public interface HistoryProjection {
  Integer getHistoryId();

  String getEmail();

  String getEndpointName();

  String getEndpointDescription();

  LocalDateTime getCreatedAt();
}
