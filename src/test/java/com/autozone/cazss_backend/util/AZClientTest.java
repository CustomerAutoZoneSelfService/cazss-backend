package com.autozone.cazss_backend.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.autozone.cazss_backend.DTO.ServiceInfoDTO;
import com.autozone.cazss_backend.DTO.ServiceResponseDTO;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.model.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class AZClientTest {

  @Mock private RestTemplate restTemplate;

  private AZClient azClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    azClient = new AZClient(restTemplate);
  }

  @Test
  public void testCallService_success() {
    ServiceInfoDTO serviceInfo = new ServiceInfoDTO();
    serviceInfo.setUrl("http://test.com/api/{{id}}");
    serviceInfo.setMethod(EndpointMethodEnum.GET);
    serviceInfo.setTemplate(null);

    InlineParamModel inline = new InlineParamModel("id", "123");
    HeaderModel header = new HeaderModel("Authorization", "Bearer token");
    QueryStringModel query = new QueryStringModel("param", "value");

    ServiceInfoRequestModel requestModel = new ServiceInfoRequestModel();
    requestModel.setInline(List.of(inline));
    requestModel.setHeaders(List.of(header));
    requestModel.setQueryString(List.of(query));

    ResponseEntity<String> mockResponse = new ResponseEntity<>("response body", HttpStatus.OK);
    when(restTemplate.exchange(
            eq("http://test.com/api/123?param=value"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(mockResponse);

    // When
    ServiceResponseDTO response = azClient.callService(serviceInfo, requestModel);

    // Then
    assertEquals(200, response.getStatusCode());
    assertEquals("response body", response.getResponse());
  }

  @Test
  void testCallService_httpStatusCodeException() {
    // Given
    ServiceInfoDTO serviceInfo = new ServiceInfoDTO();
    serviceInfo.setUrl("http://test.com/api/{{id}}");
    serviceInfo.setMethod(EndpointMethodEnum.GET);

    InlineParamModel inline = new InlineParamModel("id", "123");
    ServiceInfoRequestModel requestModel = new ServiceInfoRequestModel();
    requestModel.setInline(List.of(inline));
    requestModel.setHeaders(List.of());
    requestModel.setQueryString(List.of());

    HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
    when(exception.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
    when(exception.getResponseBodyAsString()).thenReturn("Not Found");

    when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenThrow(exception);

    // When
    ServiceResponseDTO response = azClient.callService(serviceInfo, requestModel);

    // Then
    assertEquals(404, response.getStatusCode());
    assertEquals("Not Found", response.getResponse());
  }

  @Test
  void testCallService_generalException() {
    // Given
    ServiceInfoDTO serviceInfo = new ServiceInfoDTO();
    serviceInfo.setUrl("http://test.com/api/{{id}}");
    serviceInfo.setMethod(EndpointMethodEnum.GET);

    InlineParamModel inline = new InlineParamModel("id", "123");
    ServiceInfoRequestModel requestModel = new ServiceInfoRequestModel();
    requestModel.setInline(List.of(inline));
    requestModel.setHeaders(List.of());
    requestModel.setQueryString(List.of());

    when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
        .thenThrow(new RuntimeException("Connection timeout"));

    // When
    ServiceResponseDTO response = azClient.callService(serviceInfo, requestModel);

    // Then
    assertEquals(500, response.getStatusCode());
    assertEquals("Connection timeout", response.getResponse());
  }
}
