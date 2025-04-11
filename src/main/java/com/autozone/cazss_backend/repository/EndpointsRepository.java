package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.EndpointsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EndpointsRepository extends JpaRepository<EndpointsEntity, Integer> {
    Optional<EndpointsEntity> findByName(String name);
}
