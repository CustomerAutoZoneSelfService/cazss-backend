package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import com.autozone.cazss_backend.projections.ResponsePatternProjection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResponsePatternRepository extends JpaRepository<ResponsePatternEntity, Integer> {
  List<ResponsePatternEntity> findByResponse_ResponseIdIn(Set<Integer> responseIds);

  @Query(
      """
      SELECT
        rpe.responsePatternId AS responsePatternId,
        res.responseId AS responseId,
        rpe.pattern AS pattern,
        rpe.name AS name,
        rpe.description AS description,
        rpe.parentId AS parentId,
        rpe.isLeaf AS isLeaf
      FROM ResponsePatternEntity rpe
      JOIN rpe.response res
      WHERE res.responseId = :id
      """)
  List<ResponsePatternProjection> findByResponseId(Integer id);

  List<ResponsePatternEntity> findByResponse_ResponseId(Integer id);
}
