package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.HistoryDTO;
import com.autozone.cazss_backend.DTO.HistoryDetailedDTO;
import com.autozone.cazss_backend.service.HistoryService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public ResponseEntity<List<HistoryDTO>> getAllHistory(@RequestParam Optional<Integer> userId) {
    if (userId.isPresent()) {
      return ResponseEntity.status(200).body(historyService.getHistoryByUserId(userId.get()));
    } else {
      return ResponseEntity.status(200).body(historyService.getAllHistory());
    }
  }

  /**
   * /services/history/{id}
   *
   * @param id
   * @return HistoryDetailedDTO which contains all of the detailed history log information
   */
  @GetMapping("/history/{id}")
  public ResponseEntity<HistoryDetailedDTO> getHistoryById(@PathVariable Integer id) {
    HistoryDetailedDTO historyData = historyService.getHistoryById(id);
    return new ResponseEntity<>(historyData, HttpStatus.OK);
  }
}
