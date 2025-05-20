package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.CreateServiceRequestDTO;
import com.autozone.cazss_backend.DTO.EndpointServiceDTO;
import com.autozone.cazss_backend.DTO.ServiceDTO;
import com.autozone.cazss_backend.DTO.ServiceInfoDTO;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import com.autozone.cazss_backend.service.EndpointService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/services")
public class ServiceController {

  @Autowired EndpointService endpointService;

  /**
   * /services
   *
   * @return List<ServiceDTO> which contains endpoint id, name and description
   */
  @GetMapping("")
  public ResponseEntity<List<ServiceDTO>> getAllServices() {
    return ResponseEntity.status(200).body(endpointService.getAllServices());
  }

  /**
   * /services/{id}/execute Executes a registered AutoZone service with the received variables
   *
   * @param id
   * @param serviceInfoRequestModel
   * @return EndpointServiceDTO which status and response of the azClient request
   */
  @PostMapping("/{id}/execute")
  public ResponseEntity<EndpointServiceDTO> executeService(
      @PathVariable Integer id, @RequestBody ServiceInfoRequestModel serviceInfoRequestModel) {
    return ResponseEntity.status(200)
        .body(endpointService.executeService(id, serviceInfoRequestModel));
  }

  /**
   * /services/{id}
   *
   * @param id
   * @return ServiceInfoDTO which contains all of the register info from the endpoint
   */
  @GetMapping("/{id}")
  public ResponseEntity<ServiceInfoDTO> getServiceById(@PathVariable Integer id) {
    ServiceInfoDTO serviceData = endpointService.getServiceById(id);
    return new ResponseEntity<>(serviceData, HttpStatus.OK);
  }

  /**
   * POST /services Creates a new AutoZone service definition
   *
   * @param requestDTO Data required to register a new service
   * @return Created service details
   */
  @PostMapping("")
  public ResponseEntity<ServiceDTO> createService(
      @Valid @RequestBody CreateServiceRequestDTO requestDTO) {
    ServiceDTO createdService = endpointService.createService(requestDTO);
    return new ResponseEntity<>(createdService, HttpStatus.CREATED);
  }
}
