package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.UserFilterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFilterRepository
    extends JpaRepository<UserFilterEntity, UserFilterEntity.UserFilterId> {}
