package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.AuthenticationStrategyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationStrategyRepository
    extends JpaRepository<AuthenticationStrategyEntity, Integer> {}
