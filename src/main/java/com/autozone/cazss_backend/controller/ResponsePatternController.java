package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.service.ResponsePatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/response-patterns")
public class ResponsePatternController {

  private final ResponsePatternService service;

  @Autowired
  public ResponsePatternController(ResponsePatternService service) {
    this.service = service;
  }

  @GetMapping("/{response_id}")
  public ResponseEntity<?> getResponsePatternById(@PathVariable("response_id") Integer responseId) {
    //
    return ResponseEntity.ok().build();
  }
}
