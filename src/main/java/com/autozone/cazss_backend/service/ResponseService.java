package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CreateResponseDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.ResponseEntity;
import com.autozone.cazss_backend.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {
    @Autowired
    private ResponseRepository responseRepository;

    public ResponseEntity createResponse(EndpointsEntity endpoint, CreateResponseDTO responseDTO) {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setEndpoint(endpoint);
        responseEntity.setStatusCode(responseDTO.getStatusCode());
        responseEntity.setDescription(responseDTO.getDescription());

        return responseRepository.save(responseEntity);
    }
}
