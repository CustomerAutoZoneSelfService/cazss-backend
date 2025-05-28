package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.ResponseEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<ResponseEntity, Integer> {
  List<ResponseEntity> findByEndpoint_EndpointId(Integer endpointId);

  void deleteByEndpoint_EndpointId(Integer endpointId);
}
