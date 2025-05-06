package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.EndpointsEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EndpointsRepository extends JpaRepository<EndpointsEntity, Integer> {
  List<EndpointsEntity> findAll();

  Optional<EndpointsEntity> findByName(String name);

  Optional<EndpointsEntity> findByEndpointId(Integer id);
}
