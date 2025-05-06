package com.autozone.cazss_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.autozone.cazss_backend.DTO.*;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.exceptions.ValidationException;
import com.autozone.cazss_backend.model.BodyModel;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import com.autozone.cazss_backend.repository.*;
import com.autozone.cazss_backend.util.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EndpointServiceTest {

  @Mock private EndpointsRepository endpointsRepository;
  @Mock private ResponseRepository responseRepository;
  @Mock private RequestVariableRepository requestVariableRepository;
  @Mock private RequestBodyRepository requestBodyRepository;
  @Mock private ResponsePatternRepository responsePatternRepository;
  @Mock private AZClient azClient;
  @Mock private TemplateFiller templateFiller;
  @Mock private RequestValidatorUtil requestValidatorUtil;
  @Mock private ResponsePatternService responsePatternService;

  @InjectMocks private EndpointService endpointService;

  @Test
  void getServiceById_shouldReturnPopulatedServiceInfoDTO() {
    EndpointsEntity endpoint = new EndpointsEntity();
    endpoint.setEndpointId(1);
    endpoint.setName("Test API");
    endpoint.setDescription("A test");
    endpoint.setActive(true);
    endpoint.setMethod(EndpointMethodEnum.GET);
    endpoint.setUrl("http://localhost/test");

    given(endpointsRepository.findByEndpointId(1)).willReturn(Optional.of(endpoint));
    given(requestVariableRepository.findByEndpoint_EndpointId(1)).willReturn(List.of());
    given(responseRepository.findByEndpoint_EndpointId(1)).willReturn(List.of());
    given(responsePatternRepository.findByResponse_ResponseIdIn(Set.of())).willReturn(List.of());
    given(requestBodyRepository.findByEndpoint_EndpointId(1)).willReturn(Optional.empty());

    ServiceInfoDTO result = endpointService.getServiceById(1);

    assertEquals("Test API", result.getName());
    assertEquals("", result.getTemplate());
  }

  @Test
  void getServiceById_withInvalidId_shouldThrow() {
    given(endpointsRepository.findByEndpointId(99)).willReturn(Optional.empty());

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> endpointService.getServiceById(99));
    assertThat(ex.getMessage()).contains("Endpoint not found");
  }

  @Test
  void executeService_withValidRequest_shouldReturnResponse() {
    int endpointId = 1;

    EndpointsEntity endpoint = new EndpointsEntity();
    endpoint.setEndpointId(endpointId);
    endpoint.setName("Example");
    endpoint.setDescription("Desc");
    endpoint.setMethod(EndpointMethodEnum.POST);
    endpoint.setActive(true);
    endpoint.setUrl("http://service");

    given(endpointsRepository.findByEndpointId(endpointId)).willReturn(Optional.of(endpoint));
    given(requestVariableRepository.findByEndpoint_EndpointId(endpointId)).willReturn(List.of());
    given(responseRepository.findByEndpoint_EndpointId(endpointId)).willReturn(List.of());
    given(responsePatternRepository.findByResponse_ResponseIdIn(Set.of())).willReturn(List.of());
    given(requestBodyRepository.findByEndpoint_EndpointId(endpointId)).willReturn(Optional.empty());

    ServiceInfoRequestModel request = new ServiceInfoRequestModel();
    request.setBody(List.of(new BodyModel("name", "John")));

    given(requestValidatorUtil.validateRequest(request, endpointId))
        .willReturn(new RequestValidatorUtil.ValidationResponse("true", null));

    given(templateFiller.returnFilledTemplate(request.getBody(), endpointId))
        .willReturn("{\"name\":\"John\"}");

    given(azClient.callService(org.mockito.Mockito.any(), org.mockito.Mockito.eq(request)))
        .willReturn(new ServiceResponseDTO(200, "{\"message\":\"ok\"}"));

    given(responsePatternService.getMatchesForEndpoint(endpointId, "{\"message\":\"ok\"}"))
        .willReturn(Map.of("message", List.of("ok")));

    EndpointServiceDTO result = endpointService.executeService(endpointId, request);

    assertEquals(200, result.getStatus().getCode());
    assertThat(result.getResponse()).containsKey("message");
  }

  @Test
  void executeService_withInvalidValidation_shouldThrow() {
    int endpointId = 1;
    ServiceInfoRequestModel request = new ServiceInfoRequestModel();

    Map<String, Map<String, String>> errors = new HashMap<>();
    Map<String, String> generalError = new HashMap<>();
    generalError.put("general", "Validation error");
    errors.put("general", generalError);

    given(requestValidatorUtil.validateRequest(request, endpointId))
        .willReturn(new RequestValidatorUtil.ValidationResponse("false", errors));

    ValidationException exception =
        assertThrows(
            ValidationException.class, () -> endpointService.executeService(endpointId, request));

    assertThat(exception.getMessage()).contains("Error de validación");
  }
}
