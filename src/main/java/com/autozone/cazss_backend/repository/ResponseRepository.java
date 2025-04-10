package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.ResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<ResponseEntity, Integer> {
}
