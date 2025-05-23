package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import com.autozone.cazss_backend.entity.UserFilterEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFilterRepository
        extends JpaRepository<UserFilterEntity, UserFilterEntity.UserFilterId> {

    //Help from Pedro Ruiz de la Peña in structuring the right queries
    List<UserFilterEntity> findByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            Integer userId, Integer endpointId);

    void deleteByUser_UserIdAndResponsePattern_Response_Endpoint_EndpointId(
            Integer userId, Integer endpointId);

    List<ResponsePatternEntity> findByResponsePattern_Response_Endpoint_EndpointId(
            Integer endpointId);
}