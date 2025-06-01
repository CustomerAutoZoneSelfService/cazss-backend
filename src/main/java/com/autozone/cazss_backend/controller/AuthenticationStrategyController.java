package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.AuthenticationStrategyDTO;
import com.autozone.cazss_backend.service.AuthenticationStrategyService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication-strategies")
@CrossOrigin(origins = "*")
public class AuthenticationStrategyController {

  @Autowired private AuthenticationStrategyService authStrategyService;

  // Get all strategies
  @GetMapping
  public ResponseEntity<List<AuthenticationStrategyDTO>> getAllStrategies() {
    List<AuthenticationStrategyDTO> strategies = authStrategyService.getAllStrategies();
    return ResponseEntity.ok(strategies);
  }

  // Get strategy by ID
  @GetMapping("/{id}")
  public ResponseEntity<AuthenticationStrategyDTO> getStrategyById(@PathVariable Integer id) {
    Optional<AuthenticationStrategyDTO> strategyOpt = authStrategyService.getStrategyById(id);
    return strategyOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Create a new strategy
  @PostMapping
  public ResponseEntity<AuthenticationStrategyDTO> createStrategy(
      @RequestBody AuthenticationStrategyDTO dto) {
    AuthenticationStrategyDTO created = authStrategyService.createStrategy(dto);
    return ResponseEntity.ok(created);
  }

  // Update an existing strategy by ID
  @PutMapping("/{id}")
  public ResponseEntity<AuthenticationStrategyDTO> updateStrategy(
      @PathVariable Integer id, @RequestBody AuthenticationStrategyDTO dto) {
    Optional<AuthenticationStrategyDTO> updated = authStrategyService.updateStrategy(id, dto);
    return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
