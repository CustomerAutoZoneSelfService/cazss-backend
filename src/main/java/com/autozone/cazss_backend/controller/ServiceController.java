package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.CreateRequestVariableDTO;
import com.autozone.cazss_backend.DTO.CreateResponseDTO;
import com.autozone.cazss_backend.DTO.CreateServiceDTO;
import com.autozone.cazss_backend.DTO.EndpointServiceDTO;
import com.autozone.cazss_backend.DTO.ServiceDTO;
import com.autozone.cazss_backend.DTO.ServiceInfoDTO;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import com.autozone.cazss_backend.service.EndpointService;
import com.autozone.cazss_backend.service.RequestVariableService;
import com.autozone.cazss_backend.service.ResponseService;
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

  @Autowired private RequestVariableService requestVariableService;

  @Autowired private ResponseService responseService;

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
   * /services Creates a new endpoint
   *
   * @param service Contains the complete server DTO
   * @return Returns the endpoint id, name, and description
   */
  @PostMapping("")
  public ResponseEntity<ServiceDTO> createNewService(@RequestBody CreateServiceDTO service) {
    return new ResponseEntity<>(endpointService.createCompleteService(service), HttpStatus.CREATED);
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

  @PutMapping("/{id}/request-variables")
  public ResponseEntity<Void> updateRequestVariables(
      @PathVariable Integer id, @RequestBody List<CreateRequestVariableDTO> requestVariableDTOs) {
    requestVariableService.updateRequestVariables(id, requestVariableDTOs);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{id}/responses")
  public ResponseEntity<Void> updateResponses(
      @PathVariable Integer id, @RequestBody List<CreateResponseDTO> responseDTOs) {
    responseService.updateResponses(id, responseDTOs);
    return ResponseEntity.ok().build();
  }
}
