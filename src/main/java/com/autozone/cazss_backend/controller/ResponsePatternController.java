package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.ResponsePatternDTO;
import com.autozone.cazss_backend.service.ResponsePatternService;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/response-patterns")
public class ResponsePatternController {

  private final ResponsePatternService service;

  @Autowired
  public ResponsePatternController(ResponsePatternService service) {
    this.service = service;
  }

  @GetMapping("/{response_id}")
  public ResponseEntity<List<ResponsePatternDTO>> getResponsePatternById(
      @PathVariable("response_id") @Min(0) Integer responseId) {
    return ResponseEntity.status(200).body(service.getResponsePatternById(responseId));
    // return ResponseEntity.status(200).body(service.getResponsePatternById(responseId));
  }
}
