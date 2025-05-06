package com.autozone.cazss_backend.model;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/*
 * This class is used to represent an endpoint in the system.
 * It contains information about the URL, service name, payload, headers, and HTTP method.
 */

@Getter
@Setter
@Builder
public class EndpointModel {

  /* URL of the endpoint */
  private String url;

  /* Descriptive name of the endpoint */
  private String serviceName;

  /* Payload that the endpoint will use */
  private String payload;

  /* Required headers that the endpoint needs */
  private Map<String, String> headers;

  /* HTTP Method that it will use (GET, POST, PUT, DELETE) */
  private String method;
}

/*
 * Explanation:
 * This is a model class that encapsulates all the necessary information
 * to represent an external HTTP service. It is used to structure data
 * such as the URL, headers, example payload, etc., in a reusable and
 * organized manner within the system.
 *
 * Example of use:
 * EndpointModel endpoint = EndpointModel.builder()
 *   .url("https://api.ejemplo.com/usuarios")
 *  .nombreServicio("ServicioUsuarios")
 *   .payloadEjemplo("{\"nombre\": \"Juan\", \"edad\": 25}")
 *   .headers(Map.of("Content-Type", "application/json"))
 *   .metodoHTTP("POST")
 *   .build();
 */
