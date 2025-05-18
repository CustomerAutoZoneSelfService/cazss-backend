package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.HistoryEntity;
import com.autozone.cazss_backend.projections.HistoryProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Integer> {
  @Query(
      """
      SELECT
        h.historyId AS historyId,
        u.email AS email,
        e.name AS endpointName,
        h.createdAt AS createdAt
      FROM HistoryEntity h
      JOIN h.user u
      JOIN h.endpoint e
      """)
  List<HistoryProjection> findAllProjected();
}
