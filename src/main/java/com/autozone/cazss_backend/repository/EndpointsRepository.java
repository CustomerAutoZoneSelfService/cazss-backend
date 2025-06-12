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
      "SELECT new com.autozone.cazss_backend.DTO.ServiceDTO(e.endpointId, e.name, e.description) FROM EndpointsEntity e")
  List<ServiceDTO> findAllServiceDTOs();

  @Query(
      "SELECT new com.autozone.cazss_backend.DTO.ServiceDTO(e.endpointId, e.name, e.description) "
          + "FROM EndpointsEntity e "
          + "WHERE e.user IS NULL OR e.user.userId = :userId")
  List<ServiceDTO> findUserSpecificAndNullCategoryServices(Integer userId);

  @Query(
      "SELECT new com.autozone.cazss_backend.DTO.ServiceDTO(e.endpointId, e.name, e.description) "
          + "FROM EndpointsEntity e "
          + "WHERE e.user.userId = :userId")
  List<ServiceDTO> findUserSpecificServices(Integer userId);

  Optional<EndpointsEntity> findByName(String name);

  Optional<EndpointsEntity> findByEndpointId(Integer id);

  List<EndpointsEntity> findByCategory_CategoryId(Integer categoryId);
}
