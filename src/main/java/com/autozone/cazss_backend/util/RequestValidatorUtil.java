package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.model.HeaderModel;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import com.autozone.cazss_backend.model.ValidationResultModel;
import com.autozone.cazss_backend.service.ValidationService;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Utilidad para validar requests contra la definición de la BD */
@Component
public class RequestValidatorUtil {
  private static final Logger logger = LoggerFactory.getLogger(RequestValidatorUtil.class);

  @Autowired private ValidationService validationService;

  /**
   * Valida un request contra la definición de la BD
   *
   * @param request El modelo de request a validar
   * @return Un objeto ValidationResponse con el resultado de la validación
   */
  public ValidationResponse validateRequest(ServiceInfoRequestModel request, Integer id) {
    logger.debug("=== INICIO VALIDACIÓN REQUEST ===");
    logger.debug("Request recibido: {}", request);

    try {
      if (request == null) {
        logger.error("Request nulo recibido");
        throw new IllegalArgumentException("El request no puede ser nulo");
      }

      if (id == null) {
        logger.error("ID de endpoint nulo recibido");
        throw new IllegalArgumentException("El ID del endpoint no puede ser nulo");
      }

      logger.debug("Validando headers...");
      if (request.getHeaders() != null) {
        for (HeaderModel header : request.getHeaders()) {
          logger.debug("Validando header: {}", header);
          if (header == null || header.getKey() == null || header.getKey().trim().isEmpty()) {
            logger.error("Header inválido encontrado: {}", header);
            throw new IllegalArgumentException("Los headers deben tener una clave válida");
          }
        }
      }

      logger.debug("Convirtiendo headers a Map...");
      Map<String, String> headers =
          request.getHeaders() != null
              ? request.getHeaders().stream()
                  .filter(
                      header ->
                          header != null && header.getKey() != null && header.getValue() != null)
                  .collect(
                      Collectors.toMap(
                          HeaderModel::getKey,
                          HeaderModel::getValue,
                          (existing, replacement) -> existing))
              : new HashMap<>();

      logger.debug("Headers convertidos: {}", headers);

      logger.debug("Llamando a validationService...");
      ValidationResultModel result = validationService.validateRequest(id, headers);
      logger.debug("Resultado de validationService: {}", result);

      ValidationResponse response = new ValidationResponse();
      if ("true".equals(result.getStatus())) {
        logger.debug("Validación exitosa");
        response.setStatus("true");
        response.setErrores(null);
      } else {
        logger.error("Validación fallida: {}", result.getErrors());
        response.setStatus("error");
        response.setErrores(result.getErrors());
      }

      logger.debug("=== FIN VALIDACIÓN REQUEST ===");
      return response;
    } catch (Exception e) {
      logger.error("Error durante la validación: {}", e.getMessage(), e);
      ValidationResponse response = new ValidationResponse();
      response.setStatus("error");
      Map<String, Map<String, String>> errores = new HashMap<>();
      Map<String, String> generalError = new HashMap<>();
      generalError.put("general", "Error de validación: " + e.getMessage());
      errores.put("general", generalError);
      response.setErrores(errores);
      return response;
    }
  }

  /**
   * Clase para almacenar el resultado de la validación en un formato adecuado para la respuesta API
   */
  public static class ValidationResponse {
    private String status;
    private Map<String, Map<String, String>> errores;

    public ValidationResponse() {}

    public ValidationResponse(String status, Map<String, Map<String, String>> errores) {
      this.status = status;
      this.errores = errores;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public Map<String, Map<String, String>> getErrores() {
      return errores;
    }

    public void setErrores(Map<String, Map<String, String>> errores) {
      this.errores = errores;
    }
  }
}
