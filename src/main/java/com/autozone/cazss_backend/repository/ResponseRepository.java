package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.ResponseEntity;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResponseRepository extends JpaRepository<ResponseEntity, Integer> {
  List<ResponseEntity> findByEndpoint_EndpointId(Integer endpointId);

  @Query("SELECT r FROM ResponseEntity r WHERE r.endpoint.endpointId = :endpointId AND r.statusCode = :statusCode")
  Optional<ResponseEntity> findByEndpointIdAndStatusCode(@Param("endpointId") Integer endpointId, @Param("statusCode") Integer statusCode);

  void deleteByEndpoint_EndpointId(Integer endpointId);
}
