package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.AuthenticationStrategyAttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationStrategyAttributeRepository
    extends JpaRepository<AuthenticationStrategyAttributeEntity, Integer> {}
