package com.autozone.cazss_backend.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.RequestVariableEntity;
import com.autozone.cazss_backend.enumerator.RequestVariableTypeEnum;
import com.autozone.cazss_backend.model.ValidationDataModel;
import com.autozone.cazss_backend.model.ValidationResultModel;
import com.autozone.cazss_backend.repository.EndpointsRepository;
import com.autozone.cazss_backend.repository.RequestVariableRepository;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
public class ValidationServiceTest {

  @Mock private EndpointsRepository endpointsRepository;
  @Mock private RequestVariableRepository requestVariableRepository;
  @InjectMocks private ValidationService validationService;

  private EndpointsEntity endpoint;
  private RequestVariableEntity headerVariable;

  @Before
  public void setUp() {
    // Initialize the mocks and the service
    MockitoAnnotations.openMocks(this);

    // Setup mock data
    endpoint = new EndpointsEntity();
    endpoint.setEndpointId(1);
    endpoint.setActive(true);

    headerVariable = new RequestVariableEntity();
    headerVariable.setKeyName("Authorization");
    headerVariable.setType(RequestVariableTypeEnum.HEADER);

    // Mock repository calls
    when(endpointsRepository.findById(1)).thenReturn(Optional.of(endpoint));
    when(requestVariableRepository.findByEndpoint_EndpointId(1))
        .thenReturn(Collections.singletonList(headerVariable));
  }

  @Test
  public void testValidateRequest_Success() {
    // Test data
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer testToken");

    // Call method
    ValidationResultModel result = validationService.validateRequest(1, headers);

    // Assertions
    assertEquals("true", result.getStatus());
    assertTrue(result.getErrors().isEmpty());
  }

  @Test
  public void testValidateRequest_EndpointInactive() {
    // Set the endpoint to inactive
    endpoint.setActive(false);

    // Test data
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer testToken");

    // Call method
    ValidationResultModel result = validationService.validateRequest(1, headers);

    // Assertions
    assertEquals("error", result.getStatus());
    assertTrue(result.getErrors().containsKey("general"));
    assertTrue(result.getErrors().get("general").containsValue("El endpoint no está activo"));
  }

  @Test
  public void testValidateRequest_HeaderMissing() {
    // Test data with missing header
    Map<String, String> headers = new HashMap<>();
    // Missing "Authorization" header

    // Call method
    ValidationResultModel result = validationService.validateRequest(1, headers);

    // Assertions
    assertEquals("error", result.getStatus());
    assertTrue(result.getErrors().containsKey("header"));
    assertTrue(result.getErrors().get("header").containsKey("Authorization"));
    assertEquals("falta", result.getErrors().get("header").get("Authorization"));
  }

  @Test(expected = RuntimeException.class)
  public void testGetValidationData_EndpointNotFound() {
    // Simulate endpoint not found in repository
    when(endpointsRepository.findById(1)).thenReturn(Optional.empty());

    // Call method
    validationService.getValidationData(1); // This should throw an exception
  }

  @Test
  public void testGetValidationData_Success() {
    // Call method
    ValidationDataModel validationData = validationService.getValidationData(1);

    // Assertions
    assertNotNull(validationData);
    assertEquals(endpoint, validationData.getEndpoint());
    assertFalse(validationData.getHeaderVariables().isEmpty());
  }
}
