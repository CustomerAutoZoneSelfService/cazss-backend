package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.*;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.exceptions.ValidationException;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import com.autozone.cazss_backend.model.StatusModel;
import com.autozone.cazss_backend.repository.*;
import com.autozone.cazss_backend.util.*;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class EndpointService {
  private static final Logger logger = LoggerFactory.getLogger(EndpointService.class);

  @Autowired private EndpointsRepository endpointsRepository;

  @Autowired private RequestVariableRepository requestVariableRepository;

  @Autowired private ResponseRepository responseRepository;

  @Autowired private ResponsePatternRepository responsePatternRepository;

  @Autowired private RequestBodyRepository requestBodyRepository;

  @Autowired private AZClient azClient;

  @Autowired private TemplateFiller templateFiller;

  @Autowired private RequestValidatorUtil requestValidatorUtil;

  @Autowired private ResponsePatternService responsePatternService;

  public List<ServiceDTO> getAllServices() {
    return endpointsRepository.findAllServiceDTOs();
  }

  public ServiceInfoDTO getServiceById(Integer id) {
    System.out.println("ENTERING GET SERVICE BY ID");
    EndpointsEntity endpoint =
        endpointsRepository
            .findByEndpointId(id)
            .orElseThrow(() -> new ServiceNotFoundException("Endpoint not found with id: " + id));

    ServiceInfoDTO serviceInformation =
        new ServiceInfoDTO(
            endpoint.getName(),
            endpoint.getDescription(),
            endpoint.getActive(),
            endpoint.getMethod(),
            endpoint.getUrl());

    serviceInformation.setId(endpoint.getEndpointId());

    List<RequestVariableEntity> requestVariables =
        requestVariableRepository.findByEndpoint_EndpointId(endpoint.getEndpointId());
    serviceInformation.setVariables(
        requestVariables.stream()
            .map(
                requestVaraibleEntity ->
                    new RequestVariableDTO(
                        requestVaraibleEntity.getRequestVariableId(),
                        requestVaraibleEntity.getType(),
                        requestVaraibleEntity.getKeyName(),
                        requestVaraibleEntity.getDefaultValue(),
                        requestVaraibleEntity.getCustomizable(),
                        requestVaraibleEntity.getDescription()))
            .collect(Collectors.toList()));

    List<ResponseEntity> responses =
        responseRepository.findByEndpoint_EndpointId(endpoint.getEndpointId());
    serviceInformation.setResponses(
        responses.stream()
            .map(
                responseEntity ->
                    new ServiceResponseDTO(
                        responseEntity.getStatusCode(), responseEntity.getDescription()))
            .collect(Collectors.toList()));

    Set<Integer> responseIds =
        responses.stream().map(ResponseEntity::getResponseId).collect(Collectors.toSet());

    List<ResponsePatternEntity> responsePatterns =
        responsePatternRepository.findByResponse_ResponseIdIn(responseIds);
    serviceInformation.setFilters(
        responsePatterns.stream()
            .map(
                responsePatternEntity ->
                    new FilterDTO(
                        responsePatternEntity.getResponsePatternId(),
                        responsePatternEntity.getPattern(),
                        responsePatternEntity.getName(),
                        responsePatternEntity.getDescription()))
            .collect(Collectors.toList()));

    RequestBodyEntity requestBody =
        requestBodyRepository.findByEndpoint_EndpointId(id).orElse(null);
    serviceInformation.setTemplate(requestBody != null ? requestBody.getTemplate() : "");

    return serviceInformation;
  }

  // Method to execute the service based on the request model
  public EndpointServiceDTO executeService(
      Integer id, ServiceInfoRequestModel serviceInfoRequestModel) {
    logger.debug("=== INICIO EJECUCIÓN SERVICIO ===");
    logger.debug("Request recibido: {}", serviceInfoRequestModel);

    // Validate request using RequestValidatorUtil
    logger.debug("Iniciando validación del request...");
    RequestValidatorUtil.ValidationResponse validationResponse =
        requestValidatorUtil.validateRequest(serviceInfoRequestModel, id);
    logger.debug("Resultado de la validación: {}", validationResponse);
    if (!"true".equals(validationResponse.getStatus())) {
      logger.error("Validación fallida: {}", validationResponse.getErrores());
      throw new ValidationException("Error de validación: " + validationResponse.getErrores());
    }

    ServiceInfoDTO serviceInfo = getServiceById(id);
    logger.debug("Información del servicio obtenida: {}", serviceInfo);

    String template =
        templateFiller.returnFilledTemplate(serviceInfoRequestModel.getBody(), serviceInfo.getId());

    serviceInfo.setTemplate(template);

    // azclient
    ServiceResponseDTO serviceResponse = azClient.callService(serviceInfo, serviceInfoRequestModel);

    int code = serviceResponse.getStatusCode();
    String description = HttpStatus.valueOf(code).getReasonPhrase(); // OK, Bad Request, etc
    StatusModel status = new StatusModel(code, description);

    // regexparser Lou/edgar
    Map<String, List<String>> parsedResponse = new HashMap<>();
    if (serviceResponse.getResponse() != null && !serviceResponse.getResponse().trim().isEmpty()) {
      parsedResponse =
          responsePatternService.getMatchesForEndpoint(
              serviceInfo.getId(), serviceResponse.getResponse());
    } else {
      logger.warn("Empty or null response description for endpoint {}", serviceInfo.getId());
    }

    return new EndpointServiceDTO(status, parsedResponse);
  }
}
