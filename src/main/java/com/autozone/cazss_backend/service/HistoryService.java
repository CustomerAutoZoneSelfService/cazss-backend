package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.HistoryDTO;
import com.autozone.cazss_backend.DTO.ServiceDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.HistoryEntity;
import com.autozone.cazss_backend.repository.HistoryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

  @Autowired private HistoryRepository historyRepository;

  public List<HistoryDTO> getAllHistory() {
    List<HistoryEntity> entities = historyRepository.findAll();
    return entities.stream()
        .map(
            entity -> {
              HistoryDTO historyDTO = new HistoryDTO();
              historyDTO.setHistoryId(entity.getHistoryId());
              historyDTO.setCreatedAt(entity.getCreatedAt());

              EndpointsEntity endpointEntity = entity.getEndpoint();

              ServiceDTO serviceDTO = new ServiceDTO();
              serviceDTO.setEndpointId(endpointEntity.getEndpointId());
              serviceDTO.setName(endpointEntity.getName());
              serviceDTO.setDescription(endpointEntity.getDescription());

              historyDTO.setEndpoint(serviceDTO);

              return historyDTO;
            })
        .toList();
  }
}
