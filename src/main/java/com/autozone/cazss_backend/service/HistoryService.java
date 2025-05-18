package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.projections.HistoryProjection;
import com.autozone.cazss_backend.repository.HistoryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

  @Autowired private HistoryRepository historyRepository;

  public List<HistoryProjection> getAllHistory() {
    return historyRepository.findAllProjected();
  }
}
