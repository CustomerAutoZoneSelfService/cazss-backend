package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.entity.CacheEntity;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SimpleCache {
  private final Map<String, CacheEntity> cache = new HashMap<>();

  public void put(String key, CacheEntity entity) {
    cache.put(key, entity);
  }

  public void put(String key, String value, LocalDateTime expiresAt) {
    cache.put(key, new CacheEntity(value, expiresAt));
  }

  public String get(String key) {
    CacheEntity entity = cache.get(key);

    if (entity == null) return null;
    if (entity.isExpired()) {
      cache.remove(key);
      return null;
    }
    return entity.getValue();
  }

  public boolean contains(String key) {
    CacheEntity entity = cache.get(key);
    return entity != null && !entity.isExpired();
  }
}
