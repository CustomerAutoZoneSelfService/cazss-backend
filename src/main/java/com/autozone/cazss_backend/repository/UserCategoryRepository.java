package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.UserCategoryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryRepository
    extends JpaRepository<UserCategoryEntity, UserCategoryEntity.UserCategoryId> {
  List<UserCategoryEntity> findByUser_UserId(Integer userId);

  List<UserCategoryEntity> findByUser_Email(String userEmail);

  Optional<UserCategoryEntity> findByCategory_CategoryIdAndUser_UserId(
      Integer userId, Integer categoryId);

  List<UserCategoryEntity> findByCategory_CategoryId(Integer categoryId);

  Long deleteByUser_UserIdAndCategory_CategoryId(Integer userId, Integer categoryId);

  Long deleteByCategory_CategoryId(Integer categoryId);
}
