package com.autozone.cazss_backend.entity;

import java.time.LocalDateTime;
import lombok.Getter;

public class CacheEntity {
  @Getter private String value;
  private LocalDateTime expiresAt;

  public CacheEntity(String value, LocalDateTime expiresAt) {
    this.value = value;
    this.expiresAt = expiresAt;
  }

  public Boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }
}
