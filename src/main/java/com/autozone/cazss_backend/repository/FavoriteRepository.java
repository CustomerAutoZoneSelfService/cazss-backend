package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository
    extends JpaRepository<FavoriteEntity, FavoriteEntity.FavoriteId> {}
