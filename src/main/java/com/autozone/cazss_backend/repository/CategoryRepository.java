package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.CategoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
  Optional<CategoryEntity> findByName(String name);
}
