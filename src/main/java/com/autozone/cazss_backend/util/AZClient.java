package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.DTO.ServiceInfoDTO;
import com.autozone.cazss_backend.DTO.ServiceResponseDTO;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.model.HeaderModel;
import com.autozone.cazss_backend.model.InlineParamModel;
import com.autozone.cazss_backend.model.QueryStringModel;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import java.util.List;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AZClient {

  private final RestTemplate restTemplate; // Uso RestTemplate mas facil hacer peticiones

  public AZClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // serviceInfoRequestModel es la informacion que viene de la request del
  // ususario
  // serviceInfo es la informacion que viene de la bd
  public ServiceResponseDTO callService(
      ServiceInfoDTO serviceInfo, ServiceInfoRequestModel serviceInfoRequest) {

    try {
      String url = serviceInfo.getUrl();
      for (InlineParamModel inline : serviceInfoRequest.getInline()) {
        url = url.replace("{{" + inline.getKey() + "}}", inline.getValue());
      }

      EndpointMethodEnum method = serviceInfo.getMethod();

      HttpMethod httpMethod = HttpMethod.valueOf(method.name());

      List<HeaderModel> httpHeaders = serviceInfoRequest.getHeaders();
      HttpHeaders headers = new HttpHeaders();

      UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
      for (QueryStringModel param : serviceInfoRequest.getQueryString()) {
        builder.queryParam(param.getKey(), param.getValue());
      }

      for (HeaderModel headerModel : httpHeaders) {
        if (headerModel.getKey() != null && headerModel.getValue() != null) {
          headers.add(headerModel.getKey(), headerModel.getValue());
        }
      }
      String body = serviceInfo.getTemplate();

      HttpEntity<String> requestEntity =
          (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE)
              ? new HttpEntity<>(headers)
              : new HttpEntity<>(body, headers);

      ResponseEntity<String> response =
          restTemplate.exchange(
              builder.toUriString(),
              httpMethod, // Method
              requestEntity,
              String.class);

      // Crear el objeto y retornarlo
      return new ServiceResponseDTO(response.getStatusCode().value(), response.getBody());

    } catch (HttpStatusCodeException ex) {
      return new ServiceResponseDTO(ex.getStatusCode().value(), ex.getResponseBodyAsString());
    } catch (Exception ex) {
      return new ServiceResponseDTO(500, ex.getMessage());
    }
  }
}
