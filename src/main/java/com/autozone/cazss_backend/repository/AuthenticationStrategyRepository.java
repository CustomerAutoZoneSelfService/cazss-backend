package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.AuthenticationStrategyEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationStrategyRepository
    extends JpaRepository<AuthenticationStrategyEntity, Integer> {
  Optional<AuthenticationStrategyEntity> findByName(String name);
}
