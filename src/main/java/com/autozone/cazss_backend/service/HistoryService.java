package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.HistoryDTO;
import com.autozone.cazss_backend.DTO.HistoryDataDTO;
import com.autozone.cazss_backend.DTO.HistoryDetailedDTO;
import com.autozone.cazss_backend.DTO.ServiceDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.HistoryDataEntity;
import com.autozone.cazss_backend.entity.HistoryEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.HistoryDataTypeEnum;
import com.autozone.cazss_backend.exceptions.HistoryNotFoundException;
import com.autozone.cazss_backend.projections.HistoryDetailedProjection;
import com.autozone.cazss_backend.projections.HistoryProjection;
import com.autozone.cazss_backend.repository.HistoryDataRepository;
import com.autozone.cazss_backend.repository.HistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

  @Autowired private HistoryRepository historyRepository;

  @Autowired private HistoryDataRepository historyDataRepository;

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

  public void addHistory(
      UserEntity user,
      EndpointsEntity endpoint,
      Integer statusCode,
      String request,
      String response) {

    HistoryEntity history = new HistoryEntity();
    history.setUser(user);
    history.setEndpoint(endpoint);
    history.setStatusCode(statusCode);
    history.setCreatedAt(LocalDateTime.now());
    history = historyRepository.save(history);

    HistoryDataEntity historyDataRequest = new HistoryDataEntity();
    historyDataRequest.setHistory(history);
    historyDataRequest.setType(HistoryDataTypeEnum.REQUEST);
    historyDataRequest.setContent(request);
    historyDataRepository.save(historyDataRequest);

    HistoryDataEntity historyDataResponse = new HistoryDataEntity();
    historyDataResponse.setHistory(history);
    historyDataResponse.setType(HistoryDataTypeEnum.RESPONSE);
    historyDataResponse.setContent(response);
    historyDataRepository.save(historyDataResponse);
  }
}
