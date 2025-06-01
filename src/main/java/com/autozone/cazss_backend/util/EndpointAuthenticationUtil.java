package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.entity.AuthenticationStrategyAttributeEntity;
import com.autozone.cazss_backend.entity.AuthenticationStrategyEntity;
import com.autozone.cazss_backend.enumerator.AuthStrategyEnum;
import com.autozone.cazss_backend.model.HeaderModel;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class EndpointAuthenticationUtil {

  public void attachAuthenticationHeaders(
      ServiceInfoRequestModel request, AuthenticationStrategyEntity strategy) {
    List<HeaderModel> headers =
        request.getHeaders() != null ? new ArrayList<>(request.getHeaders()) : new ArrayList<>();

    AuthStrategyEnum strategyType = strategy.getStrategy();
    List<AuthenticationStrategyAttributeEntity> attributes = strategy.getAttributes();

    switch (strategyType) {
      case Bearer:
        String token = getAttributeValue(attributes, "token");
        headers.add(new HeaderModel("Authorization", "Bearer " + token));
        break;
      case Basic:
        String basic = getBasicCredentials(attributes);
        headers.add(new HeaderModel("Authorization", "Basic " + basic));
        break;
      case Cookie:
        String cookie = getAttributeValue(attributes, "cookie");
        headers.add(new HeaderModel("Cookie", cookie));
        break;
      case Other:
        for (AuthenticationStrategyAttributeEntity attr : attributes) {
          headers.add(new HeaderModel(attr.getKeyName(), attr.getValue()));
        }
        break;
    }

    request.setHeaders(headers);
  }

  private String getAttributeValue(
      List<AuthenticationStrategyAttributeEntity> attributes, String key) {
    return attributes.stream()
        .filter(attr -> key.equalsIgnoreCase(attr.getKeyName()))
        .map(AuthenticationStrategyAttributeEntity::getValue)
        .findFirst()
        .orElse("");
  }

  private String getBasicCredentials(List<AuthenticationStrategyAttributeEntity> attributes) {
    String username = getAttributeValue(attributes, "username");
    String password = getAttributeValue(attributes, "password");
    return Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
  }
}
