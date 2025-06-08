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

  Optional<EndpointsEntity> findByName(String name);

  Optional<EndpointsEntity> findByEndpointId(Integer id);

  List<EndpointsEntity> findByCategory_CategoryId(Integer categoryId);

  @Query(
      "SELECT DISTINCT NEW com.autozone.cazss_backend.DTO.ServiceDTO(e.endpointId, e.name, e.description) "
          + "FROM EndpointsEntity e "
          + "JOIN UserCategoryEntity uc ON e.endpointId = uc.category.categoryId "
          + // Join Endpoints with UserCategory on categoryId
          "WHERE uc.user.userId = :userId" // Filter by the user's ID
  )
  List<ServiceDTO> findServicesByUserIdAccess(Integer userId);
}
