package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.EndpointServiceDTO;
import com.autozone.cazss_backend.DTO.ServiceDTO;
import com.autozone.cazss_backend.DTO.ServiceInfoDTO;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import com.autozone.cazss_backend.service.EndpointService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/services")
public class ServiceController {

  @Autowired EndpointService endpointService;

  @GetMapping("")
  public ResponseEntity<List<ServiceDTO>> getAllServices() {
    return ResponseEntity.status(200).body(endpointService.getAllServices());
  }

  @PostMapping("/{id}/execute")
  public ResponseEntity<EndpointServiceDTO> executeService(
      @PathVariable Integer id, @RequestBody ServiceInfoRequestModel serviceInfoRequestModel) {
    return ResponseEntity.status(200)
        .body(endpointService.executeService(id, serviceInfoRequestModel));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ServiceInfoDTO> getServiceById(@PathVariable Integer id) {
    ServiceInfoDTO serviceData = endpointService.getServiceById(id);
    return new ResponseEntity<>(serviceData, HttpStatus.OK);
  }
}
