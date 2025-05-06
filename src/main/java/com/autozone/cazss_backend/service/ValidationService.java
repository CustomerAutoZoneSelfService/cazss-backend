package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.RequestVariableEntity;
import com.autozone.cazss_backend.enumerator.RequestVariableTypeEnum;
import com.autozone.cazss_backend.model.ValidationDataModel;
import com.autozone.cazss_backend.model.ValidationResultModel;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.RequestVariableRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
  private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);

  @Autowired private EndpointsRepository endpointsRepository;

  @Autowired private RequestVariableRepository requestVariableRepository;

  /**
   * Método principal para validar headers contra la definición de la BD
   *
   * @param endpointId ID del endpoint a validar
   * @param headers Headers recibidos
   * @return ValidationResult con el resultado de la validación
   */
  public ValidationResultModel validateRequest(Integer endpointId, Map<String, String> headers) {
    logger.debug("=== INICIO VALIDACIÓN SERVICE ===");
    logger.debug("Validando endpoint ID: {} con headers: {}", endpointId, headers);

    ValidationDataModel validationData = getValidationData(endpointId);
    ValidationResultModel result = new ValidationResultModel();

    logger.debug("Verificando si el endpoint está activo...");
    if (!validationData.getEndpoint().getActive()) {
      logger.error("Endpoint {} no está activo", endpointId);
      result.setStatus("error");
      Map<String, Map<String, String>> errors = new HashMap<>();
      Map<String, String> generalError = new HashMap<>();
      generalError.put("endpoint", "El endpoint no está activo");
      errors.put("general", generalError);
      result.setErrors(errors);
      return result;
    }

    logger.debug("Validando headers contra variables definidas...");
    Map<String, String> headerErrors =
        validateHeaders(validationData.getHeaderVariables(), headers);
    if (!headerErrors.isEmpty()) {
      logger.error("Errores en headers encontrados: {}", headerErrors);
      result.setStatus("error");
      result.getErrors().put("header", headerErrors);
    }

    if (result.getErrors().isEmpty()) {
      logger.debug("Validación exitosa");
      result.setStatus("true");
    } else {
      logger.warn("Errores de validación encontrados: {}", result.getErrors());
    }

    logger.debug("=== FIN VALIDACIÓN SERVICE ===");
    return result;
  }

  /**
   * Obtiene toda la información necesaria para validar un endpoint por su ID
   *
   * @param endpointId El ID del endpoint a validar
   * @return ValidationData conteniendo toda la información necesaria para la validación
   */
  public ValidationDataModel getValidationData(Integer endpointId) {
    logger.debug("Obteniendo datos de validación para endpoint ID: {}", endpointId);

    Optional<EndpointsEntity> endpointOpt = endpointsRepository.findById(endpointId);
    if (endpointOpt.isEmpty()) {
      logger.error("Endpoint no encontrado con ID: {}", endpointId);
      throw new RuntimeException("Endpoint no encontrado con ID: " + endpointId);
    }

    EndpointsEntity endpoint = endpointOpt.get();
    logger.debug("Endpoint encontrado: {}", endpoint);

    List<RequestVariableEntity> requestVariables =
        requestVariableRepository.findByEndpoint_EndpointId(endpointId);
    logger.debug("Variables de request encontradas: {}", requestVariables);

    Map<RequestVariableTypeEnum, List<RequestVariableEntity>> variablesByType =
        requestVariables.stream().collect(Collectors.groupingBy(RequestVariableEntity::getType));
    logger.debug("Variables agrupadas por tipo: {}", variablesByType);

    ValidationDataModel validationData = new ValidationDataModel();
    validationData.setEndpoint(endpoint);
    validationData.setHeaderVariables(
        variablesByType.getOrDefault(RequestVariableTypeEnum.HEADER, List.of()));

    return validationData;
  }

  /**
   * Valida los headers contra la definición de la BD
   *
   * @param headerVariables Lista de variables de header definidas en la BD
   * @param headers Headers recibidos
   * @return Map con los errores encontrados, vacío si no hay errores
   */
  private Map<String, String> validateHeaders(
      List<RequestVariableEntity> headerVariables, Map<String, String> headers) {
    logger.debug("Validando headers específicos...");
    Map<String, String> errors = new HashMap<>();

    for (RequestVariableEntity variable : headerVariables) {
      String headerName = variable.getKeyName();
      String headerValue = headers.get(headerName);
      logger.debug("Validando header {} con valor {}", headerName, headerValue);

      if (headerValue == null || headerValue.isEmpty()) {
        logger.error("Header {} no encontrado o vacío", headerName);
        errors.put(headerName, "falta");
      }
    }

    return errors;
  }
}
