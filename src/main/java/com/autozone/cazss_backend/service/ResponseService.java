package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CreateResponseDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.ResponseEntity;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.ResponseRepository;
import jakarta.transaction.Transactional;
import java.util.List;
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

    responseRepository.deleteByEndpoint_EndpointId(endpointId);

    for (CreateResponseDTO dto : dtos) {
      createResponse(endpoint, dto);
    }
  }
}
