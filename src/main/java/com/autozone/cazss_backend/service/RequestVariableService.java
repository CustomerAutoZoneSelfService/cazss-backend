package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CreateRequestVariableDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.RequestVariableEntity;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.RequestVariableRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestVariableService {
  @Autowired private RequestVariableRepository requestVariableRepository;
  @Autowired private EndpointsRepository endpointsRepository;

  public RequestVariableEntity createRequestVariable(
      EndpointsEntity endpoint, CreateRequestVariableDTO requestVariableDTO) {
    RequestVariableEntity requestVariableEntity = new RequestVariableEntity();
    requestVariableEntity.setEndpoint(endpoint);
    requestVariableEntity.setType(requestVariableDTO.getType());
    requestVariableEntity.setKeyName(requestVariableDTO.getKey());
    requestVariableEntity.setDefaultValue(requestVariableDTO.getDefaultValue());
    requestVariableEntity.setCustomizable(requestVariableDTO.getCustomizable());
    requestVariableEntity.setDescription(requestVariableDTO.getDescription());

    return requestVariableRepository.save(requestVariableEntity);
  }

  @Transactional
  public void updateRequestVariables(Integer endpointId, List<CreateRequestVariableDTO> dtos) {
    EndpointsEntity endpoint =
        endpointsRepository
            .findById(endpointId)
            .orElseThrow(
                () -> new ServiceNotFoundException("Endpoint not found with id: " + endpointId));

    requestVariableRepository.deleteByEndpoint_EndpointId(endpointId);

    for (CreateRequestVariableDTO dto : dtos) {
      createRequestVariable(endpoint, dto);
    }
  }
}
