package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.CategoryEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
  Optional<CategoryEntity> findByName(String name);

  // @Query()
  // Optional<CategoryDTO> findAllCategoryDTOs();
  Optional<CategoryEntity> findByCategoryId(Integer categoryId);

  List<CategoryEntity> findByCategoryIdIn(Set<Integer> categoryIds);

  Long deleteByCategoryId(Integer categoryId);
}
