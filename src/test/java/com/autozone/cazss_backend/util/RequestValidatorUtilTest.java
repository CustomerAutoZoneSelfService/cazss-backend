package com.autozone.cazss_backend.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.autozone.cazss_backend.model.HeaderModel;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import com.autozone.cazss_backend.model.ValidationResultModel;
import com.autozone.cazss_backend.service.ValidationService;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RequestValidatorUtilTest {

  @Mock private ValidationService validationService;

  @InjectMocks private RequestValidatorUtil requestValidatorUtil;

  private ServiceInfoRequestModel validRequest;

  @BeforeEach
  void setUp() {
    // Crear un request válido para usar en las pruebas
    validRequest = new ServiceInfoRequestModel();
    validRequest.setHeaders(
        Arrays.asList(
            new HeaderModel("Content-Type", "application/json"),
            new HeaderModel("Authorization", "Bearer token")));
  }

  @Test
  void validateRequest_WithNullRequest_ShouldReturnError() {
    // Act
    RequestValidatorUtil.ValidationResponse response =
        requestValidatorUtil.validateRequest(null, null);

    // Assert
    assertEquals("error", response.getStatus());
    assertNotNull(response.getErrores());
    assertTrue(
        response
            .getErrores()
            .get("general")
            .get("general")
            .contains("El request no puede ser nulo"));
  }

  @Test
  void validateRequest_WithNullId_ShouldReturnError() {
    // Arrange
    Integer id = null;

    // Act
    RequestValidatorUtil.ValidationResponse response =
        requestValidatorUtil.validateRequest(validRequest, id);

    // Assert
    assertEquals("error", response.getStatus());
    assertNotNull(response.getErrores());
    assertTrue(
        response
            .getErrores()
            .get("general")
            .get("general")
            .contains("El ID del endpoint no puede ser nulo"));
  }

  @Test
  void validateRequest_WithEmptyHeaders_ShouldReturnError() {
    // Arrange
    validRequest.setHeaders(Collections.singletonList(new HeaderModel()));

    // Act
    RequestValidatorUtil.ValidationResponse response =
        requestValidatorUtil.validateRequest(validRequest, 1);

    // Assert
    assertEquals("error", response.getStatus());
    assertNotNull(response.getErrores());
    assertTrue(
        response
            .getErrores()
            .get("general")
            .get("general")
            .contains("Los headers deben tener una clave válida"));
  }

  @Test
  void validateRequest_WithNullHeaders_ShouldReturnSuccess() {
    // Arrange
    validRequest.setHeaders(null);
    when(validationService.validateRequest(anyInt(), any()))
        .thenReturn(createSuccessValidationResult());

    // Act
    RequestValidatorUtil.ValidationResponse response =
        requestValidatorUtil.validateRequest(validRequest, 1);

    // Assert
    assertEquals("true", response.getStatus());
    assertNull(response.getErrores());
  }

  @Test
  void validateRequest_WithValidRequest_ShouldReturnSuccess() {
    // Arrange
    when(validationService.validateRequest(anyInt(), any()))
        .thenReturn(createSuccessValidationResult());

    // Act
    RequestValidatorUtil.ValidationResponse response =
        requestValidatorUtil.validateRequest(validRequest, 1);

    // Assert
    assertEquals("true", response.getStatus());
    assertNull(response.getErrores());
  }

  @Test
  void validateRequest_WithValidationServiceError_ShouldReturnError() {
    // Arrange
    when(validationService.validateRequest(anyInt(), any()))
        .thenReturn(createErrorValidationResult());

    // Act
    RequestValidatorUtil.ValidationResponse response =
        requestValidatorUtil.validateRequest(validRequest, 1);

    // Assert
    assertEquals("error", response.getStatus());
    assertNotNull(response.getErrores());
    assertTrue(response.getErrores().containsKey("header"));
  }

  private ValidationResultModel createSuccessValidationResult() {
    ValidationResultModel result = new ValidationResultModel();
    result.setStatus("true");
    return result;
  }

  private ValidationResultModel createErrorValidationResult() {
    ValidationResultModel result = new ValidationResultModel();
    result.setStatus("error");
    Map<String, Map<String, String>> errors = new HashMap<>();
    Map<String, String> headerErrors = new HashMap<>();
    headerErrors.put("Authorization", "Header requerido");
    errors.put("header", headerErrors);
    result.setErrors(errors);
    return result;
  }
}
