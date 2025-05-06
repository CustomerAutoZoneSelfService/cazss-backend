package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.RequestVariableEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestVariableRepository extends JpaRepository<RequestVariableEntity, Integer> {
  List<RequestVariableEntity> findByEndpoint_EndpointId(Integer endpointId);
}
