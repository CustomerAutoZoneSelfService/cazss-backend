package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CreateResponseDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.ResponseEntity;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.ResponseRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
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
  @Autowired private ResponsePatternService responsePatternService;

  public ResponseEntity createResponse(EndpointsEntity endpoint, CreateResponseDTO responseDTO) {
    ResponseEntity responseEntity = new ResponseEntity();
    responseEntity.setEndpoint(endpoint);
    responseEntity.setStatusCode(responseDTO.getStatusCode());
    responseEntity.setDescription(responseDTO.getDescription());

    return responseRepository.save(responseEntity);
  }

  @Transactional
  public void updateResponses(EndpointsEntity endpoint, List<CreateResponseDTO> dtos) {
    Set<Integer> codeSet = new HashSet<>();
    for (CreateResponseDTO dto : dtos) {
      if (!codeSet.add(dto.getStatusCode())) {
        throw new IllegalArgumentException("Duplicate response statusCode: " + dto.getStatusCode());
      }
    }

    List<ResponseEntity> existingResponses = responseRepository.findByEndpoint(endpoint);
    Map<Integer, ResponseEntity> existingMap =
        existingResponses.stream().collect(Collectors.toMap(ResponseEntity::getStatusCode, r -> r));
    Set<Integer> incomingCodes =
        dtos.stream().map(CreateResponseDTO::getStatusCode).collect(Collectors.toSet());

    for (CreateResponseDTO dto : dtos) {
      ResponseEntity responseEntity = existingMap.get(dto.getStatusCode());
      ResponseEntity savedResponseEntity;
      if (responseEntity == null) {
        savedResponseEntity = createResponse(endpoint, dto);
      } else {
        responseEntity.setDescription(dto.getDescription());
        savedResponseEntity = responseRepository.save(responseEntity);
      }

      if (dto.getPatterns() != null && !dto.getPatterns().isEmpty()) {
        responsePatternService.replacePatterns(
            savedResponseEntity.getResponseId(), dto.getPatterns());
      }
    }
    for (ResponseEntity existingRes : existingResponses) {
      if (!incomingCodes.contains(existingRes.getStatusCode())) {
        responseRepository.delete(existingRes);
      }
    }
  }
}
