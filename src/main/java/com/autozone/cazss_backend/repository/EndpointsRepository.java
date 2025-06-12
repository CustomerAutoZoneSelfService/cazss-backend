package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.DTO.ServiceDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EndpointsRepository extends JpaRepository<EndpointsEntity, Integer> {
  List<EndpointsEntity> findAll();

  @Query(
      "SELECT new com.autozone.cazss_backend.DTO.ServiceDTO(e.endpointId, e.name, e.description, e.category.categoryId) FROM EndpointsEntity e")
  List<ServiceDTO> findAllServiceDTOs();

  Optional<EndpointsEntity> findByName(String name);

  Optional<EndpointsEntity> findByEndpointId(Integer id);

  List<EndpointsEntity> findByCategory_CategoryId(Integer categoryId);
}
