package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CreateResponseDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.ResponseEntity;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.ResponseRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {
  @Autowired private ResponseRepository responseRepository;
  @Autowired private EndpointsRepository endpointsRepository;

  public ResponseEntity createResponse(EndpointsEntity endpoint, CreateResponseDTO responseDTO) {
    ResponseEntity responseEntity = new ResponseEntity();
    responseEntity.setEndpoint(endpoint);
    responseEntity.setStatusCode(responseDTO.getStatusCode());
    responseEntity.setDescription(responseDTO.getDescription());

    return responseRepository.save(responseEntity);
  }

  @Transactional
  public void updateResponses(Integer endpointId, List<CreateResponseDTO> dtos) {
    EndpointsEntity endpoint =
        endpointsRepository
            .findById(endpointId)
            .orElseThrow(
                () -> new ServiceNotFoundException("Endpoint not found with id: " + endpointId));

    List<ResponseEntity> existingResponses =
        responseRepository.findByEndpoint_EndpointId(endpointId);
    Map<Integer, ResponseEntity> existingMap =
        existingResponses.stream().collect(Collectors.toMap(ResponseEntity::getStatusCode, r -> r));
    Set<Integer> incomingCodes =
        dtos.stream().map(CreateResponseDTO::getStatusCode).collect(Collectors.toSet());

    for (CreateResponseDTO dto : dtos) {
      ResponseEntity existing = existingMap.get(dto.getStatusCode());
      if (existing != null) {
        existing.setDescription(dto.getDescription());
        responseRepository.save(existing);
      } else {
        createResponse(endpoint, dto);
      }
    }
    for (ResponseEntity existingRes : existingResponses) {
      if (!incomingCodes.contains(existingRes.getStatusCode())) {
        responseRepository.delete(existingRes);
      }
    }
  }
}
