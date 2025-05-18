package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.projections.HistoryProjection;
import com.autozone.cazss_backend.service.HistoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/services")
public class HistoryController {

  @Autowired HistoryService historyService;

  /**
   * /services/history
   *
   * @return List<HistoryDTO> which contains information about History logs
   */
  @GetMapping("/history")
  public ResponseEntity<List<HistoryProjection>> getAllHistory() {
    return ResponseEntity.status(200).body(historyService.getAllHistory());
  }
}
