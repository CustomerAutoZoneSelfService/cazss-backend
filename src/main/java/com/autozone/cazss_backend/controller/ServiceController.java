package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.CreateRequestVariableDTO;
import com.autozone.cazss_backend.DTO.CreateResponseDTO;
import com.autozone.cazss_backend.DTO.CreateServiceDTO;
import com.autozone.cazss_backend.DTO.EndpointServiceDTO;
import com.autozone.cazss_backend.DTO.ServiceDTO;
import com.autozone.cazss_backend.DTO.ServiceInfoDTO;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import com.autozone.cazss_backend.service.EndpointService;
import com.autozone.cazss_backend.service.RequestVariableService;
import com.autozone.cazss_backend.service.ResponseService;
import jakarta.validation.constraints.PositiveOrZero;
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
    return ResponseEntity.status(200).body(endpointService.getAvailableServices());
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

  @PutMapping("/{id}")
  public ResponseEntity<ServiceDTO> updateService(
      @PathVariable Integer id, @RequestBody CreateServiceDTO service) {
    return new ResponseEntity<>(endpointService.updateCompleteService(id, service), HttpStatus.OK);
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
   * @return ServiceInfoDTO which contains most of the registered info from the endpoint
   */
  @GetMapping("/{id}")
  public ResponseEntity<ServiceInfoDTO> getServiceById(@PathVariable Integer id) {
    ServiceInfoDTO serviceData = endpointService.getServiceById(id);
    return new ResponseEntity<>(serviceData, HttpStatus.OK);
  }

  /**
   * /services/{id}
   *
   * @param id
   * @return CreateServiceDTO which contains ALL the registered info from the endpoint (meant for
   *     editing existing services)
   */
  @GetMapping("/{id}/edit")
  public ResponseEntity<CreateServiceDTO> getFullServiceById(
      @PathVariable @PositiveOrZero Integer id) {
    CreateServiceDTO fullServiceData = endpointService.getFullServiceInfoById(id);
    return new ResponseEntity<>(fullServiceData, HttpStatus.OK);
  }

  @PutMapping("/{id}/request-variables")
  public ResponseEntity<Void> updateRequestVariables(
      @PathVariable Integer id, @RequestBody List<CreateRequestVariableDTO> requestVariableDTOs) {
    EndpointsEntity endpoint = endpointService.findEndpointById(id);
    requestVariableService.updateRequestVariables(endpoint, requestVariableDTOs);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{id}/responses")
  public ResponseEntity<Void> updateResponses(
      @PathVariable Integer id, @RequestBody List<CreateResponseDTO> responseDTOs) {
    EndpointsEntity endpoint = endpointService.findEndpointById(id);
    responseService.updateResponses(endpoint, responseDTOs);
    return ResponseEntity.ok().build();
  }
}
