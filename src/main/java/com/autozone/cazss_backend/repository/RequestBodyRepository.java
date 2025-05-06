package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.RequestBodyEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestBodyRepository extends JpaRepository<RequestBodyEntity, Integer> {
  Optional<RequestBodyEntity> findByEndpoint_EndpointId(Integer endpointId);
}
