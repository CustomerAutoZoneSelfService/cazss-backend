package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.HistoryEntity;
import com.autozone.cazss_backend.projections.HistoryDetailedProjection;
import com.autozone.cazss_backend.projections.HistoryProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Integer> {
  @Query(
      """
      SELECT
        h.historyId AS historyId,
        u.email AS email,
        e.name AS endpointName,
        e.description AS endpointDescription,
        h.createdAt AS createdAt
      FROM HistoryEntity h
      JOIN h.user u
      JOIN h.endpoint e
      """)
  List<HistoryProjection> findAllProjected();

  @Query(
      """
      SELECT
        h.historyId AS historyId,
        u.email AS email,
        e.name AS endpointName,
        e.description AS endpointDescription,
        h.createdAt AS createdAt
      FROM HistoryEntity h
      JOIN h.user u
      JOIN h.endpoint e
      WHERE u.userId = :id
      """)
  List<HistoryProjection> findByUserId(Integer id);

  @Query(
      """
      SELECT
        h.historyId AS historyId,
        h.statusCode AS statusCode,
        e.endpointId AS endpointId,
        e.name AS name,
        e.description AS description,
        hd.historyDataId AS historyDataId,
        hd.content AS content
      FROM HistoryEntity h
      JOIN h.historyData hd
      JOIN h.endpoint e
      WHERE h.historyId = :id AND hd.type = 'REQUEST'
      """)
  Optional<HistoryDetailedProjection> findHistoryRequestByHistoryId(Integer id);

  @Query(
      """
      SELECT
        h.historyId AS historyId,
        h.statusCode AS statusCode,
        e.endpointId AS endpointId,
        e.name AS name,
        e.description AS description,
        hd.content AS content
      FROM HistoryEntity h
      JOIN h.historyData hd
      JOIN h.endpoint e
      WHERE h.historyId = :id AND hd.type = 'RESPONSE'
      """)
  Optional<HistoryDetailedProjection> findHistoryResponseByHistoryId(Integer id);
}
