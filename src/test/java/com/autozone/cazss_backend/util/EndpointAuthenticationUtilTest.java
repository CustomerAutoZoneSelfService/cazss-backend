package com.autozone.cazss_backend.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.autozone.cazss_backend.entity.AuthenticationStrategyAttributeEntity;
import com.autozone.cazss_backend.entity.AuthenticationStrategyEntity;
import com.autozone.cazss_backend.enumerator.AuthStrategyEnum;
import com.autozone.cazss_backend.model.HeaderModel;
import com.autozone.cazss_backend.model.QueryStringModel;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EndpointAuthenticationUtilTest {
  @InjectMocks private EndpointAuthenticationUtil endpointAuthenticationUtil;

  private ServiceInfoRequestModel request;
  private AuthenticationStrategyEntity authStrategy;

  @BeforeEach
  void setUp() {
    request =
        new ServiceInfoRequestModel(
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    authStrategy = new AuthenticationStrategyEntity();
  }

  @Test
  void testBasicAuthentication() {
    authStrategy.setStrategy(AuthStrategyEnum.Basic);
    List<AuthenticationStrategyAttributeEntity> attributes = new ArrayList<>();
    attributes.add(createAttribute("username", "testuser"));
    attributes.add(createAttribute("password", "testpass"));
    authStrategy.setAttributes(attributes);

    endpointAuthenticationUtil.hookRequest(request, authStrategy);

    assertEquals(1, request.getHeaders().size());
    HeaderModel header = request.getHeaders().get(0);
    assertEquals("Authorization", header.getKey());
    assertEquals("Basic dGVzdHVzZXI6dGVzdHBhc3M=", header.getValue());
  }

  @Test
  void testBearerAuthentication() {
    authStrategy.setStrategy(AuthStrategyEnum.Bearer);
    List<AuthenticationStrategyAttributeEntity> attributes = new ArrayList<>();
    attributes.add(createAttribute("token", "my-bearer-token"));
    authStrategy.setAttributes(attributes);

    endpointAuthenticationUtil.hookRequest(request, authStrategy);

    assertEquals(1, request.getHeaders().size());
    HeaderModel header = request.getHeaders().get(0);
    assertEquals("Authorization", header.getKey());
    assertEquals("Bearer my-bearer-token", header.getValue());
  }

  @Test
  void testHeaderAuthentication() {
    authStrategy.setStrategy(AuthStrategyEnum.Header);
    List<AuthenticationStrategyAttributeEntity> attributes = new ArrayList<>();
    attributes.add(createAttribute("key", "X-API-Key"));
    attributes.add(createAttribute("value", "my-api-key"));
    authStrategy.setAttributes(attributes);

    endpointAuthenticationUtil.hookRequest(request, authStrategy);

    assertEquals(1, request.getHeaders().size());
    HeaderModel header = request.getHeaders().get(0);
    assertEquals("X-API-Key", header.getKey());
    assertEquals("my-api-key", header.getValue());
  }

  @Test
  void testQueryStringAuthentication() {
    authStrategy.setStrategy(AuthStrategyEnum.QueryString);
    List<AuthenticationStrategyAttributeEntity> attributes = new ArrayList<>();
    attributes.add(createAttribute("key", "api_key"));
    attributes.add(createAttribute("value", "my-query-api-key"));
    authStrategy.setAttributes(attributes);

    endpointAuthenticationUtil.hookRequest(request, authStrategy);

    assertEquals(1, request.getQueryString().size());
    QueryStringModel queryParam = request.getQueryString().get(0);
    assertEquals("api_key", queryParam.getKey());
    assertEquals("my-query-api-key", queryParam.getValue());
  }

  private AuthenticationStrategyAttributeEntity createAttribute(String key, String value) {
    AuthenticationStrategyAttributeEntity attribute = new AuthenticationStrategyAttributeEntity();
    attribute.setKeyName(key);
    attribute.setValue(value);
    return attribute;
  }
}
