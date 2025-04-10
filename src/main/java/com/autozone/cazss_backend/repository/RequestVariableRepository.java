package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.RequestVaraibleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestVariableRepository extends JpaRepository<RequestVaraibleEntity, Integer> {
}
