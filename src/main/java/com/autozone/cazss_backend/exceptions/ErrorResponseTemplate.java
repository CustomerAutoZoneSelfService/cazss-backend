package com.autozone.cazss_backend.exceptions;

import java.time.LocalDateTime;

/**
 * La clase ErrorResponse se utiliza para representar la estructura del error que se devuelve en la
 * respuesta de una API cuando ocurre una excepción. Esta clase encapsula la información relevante
 * sobre el error, que será enviada al cliente en el formato adecuado para facilitar la comprensión
 * del problema y su resolución.
 *
 * <p>Atributos: - `code`: Un código único que representa el tipo de error. Se utiliza para
 * identificar rápidamente la naturaleza del error (por ejemplo, "VALIDATION_ERROR",
 * "SERVICE_ERROR"). - `message`: Descripción breve del error, proporcionando más detalles sobre qué
 * salió mal. - `details`: Información adicional sobre el error, como un mensaje más detallado que
 * explica las causas posibles o las acciones que el usuario debe tomar. - `timestamp`: La fecha y
 * hora en que ocurrió el error. Este campo es útil para el registro de eventos y la depuración. -
 * `traceId`: Un identificador único generado para rastrear y correlacionar la solicitud de este
 * error en el sistema, útil para diagnósticos y logs distribuidos.
 */
public class ErrorResponseTemplate {

  private String code;
  private String message;
  private String details;
  private LocalDateTime timestamp;
  private String traceId;

  public ErrorResponseTemplate(
      String code, String message, String details, LocalDateTime timestamp, String traceId) {
    this.code = code;
    this.message = message;
    this.details = details;
    this.timestamp = timestamp;
    this.traceId = traceId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(String traceId) {
    this.traceId = traceId;
  }
}
