package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.HistoryDTO;
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
}
