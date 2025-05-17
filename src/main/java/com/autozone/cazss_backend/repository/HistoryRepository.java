package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.HistoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Integer> {
  List<HistoryEntity> findAll();
}
