package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.DTO.authentication.TokenResponseDTO;
import com.autozone.cazss_backend.entity.AuthenticationStrategyAttributeEntity;
import com.autozone.cazss_backend.entity.AuthenticationStrategyEntity;
import com.autozone.cazss_backend.enumerator.AuthStrategyEnum;
import com.autozone.cazss_backend.model.HeaderModel;
import com.autozone.cazss_backend.model.QueryStringModel;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class EndpointAuthenticationUtil {
  private final Logger logger = LoggerFactory.getLogger(EndpointAuthenticationUtil.class);
  private final SimpleCache cache = new SimpleCache();

  // Primitives
  private String getAttributeValue(
      List<AuthenticationStrategyAttributeEntity> attributes, String key) {
    return attributes.stream()
        .filter(attr -> key.equalsIgnoreCase(attr.getKeyName()))
        .map(AuthenticationStrategyAttributeEntity::getValue)
        .findFirst()
        .orElse("");
  }

  // Implementation
  private void basicAuthentication(
      ServiceInfoRequestModel request, List<AuthenticationStrategyAttributeEntity> attributes) {
    String username = getAttributeValue(attributes, "username");
    String password = getAttributeValue(attributes, "password");

    String authorization =
        Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    request.getHeaders().add(new HeaderModel("Authorization", "Basic" + authorization));
  }

  private void bearerAuthentication(
      ServiceInfoRequestModel request, List<AuthenticationStrategyAttributeEntity> attributes) {
    String token = getAttributeValue(attributes, "token");

    request.getHeaders().add(new HeaderModel("Authorization", "Bearer " + token));
  }

  private void oauthAuthentication(
      ServiceInfoRequestModel request, List<AuthenticationStrategyAttributeEntity> attributes)
      throws Exception {
    String url = getAttributeValue(attributes, "url");
    String clientId = getAttributeValue(attributes, "client_id");
    String clientSecret = getAttributeValue(attributes, "client_secret");
    String grantType = getAttributeValue(attributes, "grant_type");
    String scope = getAttributeValue(attributes, "scope");

    String cacheKey = url + ":" + grantType + ":" + scope;
    String token = cache.get(cacheKey);

    // Request a token if:
    // - it was not found in the cache
    // - the token has expired
    if (token == null) {
      // Form data
      MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
      formData.add("client_id", clientId);
      formData.add("client_secret", clientSecret);
      formData.add("grant_type", grantType);
      formData.add("scope", scope);

      // Headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

      // Request
      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
      RestTemplate restTemplate = CustomRestTemplate.restTemplate();
      ResponseEntity<TokenResponseDTO> response =
          restTemplate.postForEntity(url, requestEntity, TokenResponseDTO.class);
      TokenResponseDTO body = response.getBody();

      // Save in cache
      LocalDateTime now = LocalDateTime.now();
      Duration duration = Duration.ofSeconds(body.getExpires_in());
      token = body.getAccess_token();
      cache.put(cacheKey, token, now.plus(duration));
    }

    request.getHeaders().add(new HeaderModel("Authorization", "Bearer " + token));
  }

  private void keyValueAuthentication(
      AuthStrategyEnum strategy,
      ServiceInfoRequestModel request,
      List<AuthenticationStrategyAttributeEntity> attributes) {
    String key = getAttributeValue(attributes, "key");
    String value = getAttributeValue(attributes, "value");

    if (strategy == AuthStrategyEnum.Header) request.getHeaders().add(new HeaderModel(key, value));
    else if (strategy == AuthStrategyEnum.QueryString)
      request.getQueryString().add(new QueryStringModel(key, value));
  }

  // Hook
  public void hookRequest(
      ServiceInfoRequestModel request, AuthenticationStrategyEntity authStrategy) {
    try {
      AuthStrategyEnum strategy = authStrategy.getStrategy();
      List<AuthenticationStrategyAttributeEntity> attributes = authStrategy.getAttributes();

      switch (strategy) {
        case Basic -> basicAuthentication(request, attributes);
        case Bearer -> bearerAuthentication(request, attributes);
        case OAuth -> oauthAuthentication(request, attributes);
        case Header -> keyValueAuthentication(AuthStrategyEnum.Header, request, attributes);
        case QueryString ->
            keyValueAuthentication(AuthStrategyEnum.QueryString, request, attributes);
        default -> logger.warn("Strategy {} is not implemented", strategy);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
