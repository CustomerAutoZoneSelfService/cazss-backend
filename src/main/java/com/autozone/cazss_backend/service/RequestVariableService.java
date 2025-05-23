package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CreateRequestVariableDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.RequestVariableEntity;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.RequestVariableRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    List<RequestVariableEntity> existingVariables =
        requestVariableRepository.findByEndpoint_EndpointId(endpointId);
    Map<String, RequestVariableEntity> existingMap =
        existingVariables.stream()
            .collect(Collectors.toMap(RequestVariableEntity::getKeyName, v -> v));

    for (CreateRequestVariableDTO dto : dtos) {
      RequestVariableEntity existing = existingMap.get(dto.getKey());
      if (existing != null) {
        existing.setType(dto.getType());
        existing.setDefaultValue(dto.getDefaultValue());
        existing.setCustomizable(dto.getCustomizable());
        existing.setDescription(dto.getDescription());
        requestVariableRepository.save(existing);
      } else {
        createRequestVariable(endpoint, dto);
      }
    }
  }
}
