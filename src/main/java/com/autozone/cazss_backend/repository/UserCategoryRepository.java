package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.UserCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryRepository
    extends JpaRepository<UserCategoryEntity, UserCategoryEntity.UserCategoryId> {}
