package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsePatternRepository extends JpaRepository<ResponsePatternEntity, Integer> {
  List<ResponsePatternEntity> findByResponse_ResponseIdIn(Set<Integer> responseIds);

  List<ResponsePatternEntity> findByResponse_ResponseId(Integer id);

  // Added to fetch by endpoint
  List<ResponsePatternEntity> findByResponse_Endpoint_EndpointId(Integer endpointId);
}
