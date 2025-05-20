package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.HistoryDTO;
import com.autozone.cazss_backend.DTO.HistoryDataDTO;
import com.autozone.cazss_backend.DTO.HistoryDetailedDTO;
import com.autozone.cazss_backend.DTO.ServiceDTO;
import com.autozone.cazss_backend.exceptions.HistoryNotFoundException;
import com.autozone.cazss_backend.projections.HistoryDetailedProjection;
import com.autozone.cazss_backend.projections.HistoryProjection;
import com.autozone.cazss_backend.repository.HistoryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

  @Autowired private HistoryRepository historyRepository;

  public List<HistoryDTO> getAllHistory() {
    List<HistoryProjection> entities = historyRepository.findAllProjected();

    return entities.stream()
        .map(
            entity ->
                new HistoryDTO(
                    entity.getHistoryId(),
                    entity.getEmail(),
                    entity.getEndpointName(),
                    entity.getEndpointDescription(),
                    entity.getCreatedAt()))
        .toList();
  }

  public List<HistoryDTO> getHistoryByUserId(Integer id) {
    List<HistoryProjection> entities = historyRepository.findByUserId(id);

    return entities.stream()
        .map(
            entity ->
                new HistoryDTO(
                    entity.getHistoryId(),
                    entity.getEmail(),
                    entity.getEndpointName(),
                    entity.getEndpointDescription(),
                    entity.getCreatedAt()))
        .toList();
  }

  public HistoryDetailedDTO getHistoryById(Integer id) {
    HistoryDetailedProjection historyRequest =
        historyRepository
            .findHistoryRequestByHistoryId(id)
            .orElseThrow(() -> new HistoryNotFoundException("History not found with id: " + id));
    System.out.println(historyRequest);
    HistoryDetailedProjection historyResponse =
        historyRepository
            .findHistoryResponseByHistoryId(id)
            .orElseThrow(() -> new HistoryNotFoundException("History not found with id: " + id));

    ServiceDTO endpoint =
        new ServiceDTO(
            historyRequest.getEndpointId(),
            historyRequest.getName(),
            historyRequest.getDescription());

    HistoryDataDTO historyData =
        new HistoryDataDTO(historyRequest.getContent(), historyResponse.getContent());

    return new HistoryDetailedDTO(
        historyRequest.getHistoryId(), historyRequest.getStatusCode(), endpoint, historyData);
  }
}
