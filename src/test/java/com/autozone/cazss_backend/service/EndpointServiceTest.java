package com.autozone.cazss_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.autozone.cazss_backend.DTO.*;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.ResponseEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.RequestVariableTypeEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.exceptions.ServiceNotFoundException;
import com.autozone.cazss_backend.exceptions.ValidationException;
import com.autozone.cazss_backend.model.BodyModel;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import com.autozone.cazss_backend.repository.*;
import com.autozone.cazss_backend.util.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
public class EndpointServiceTest {
  @Mock private EndpointsRepository endpointsRepository;
  @Mock private UserRepository userRepository;
  @Mock private ResponseRepository responseRepository;
  @Mock private RequestVariableRepository requestVariableRepository;
  @Mock private RequestBodyRepository requestBodyRepository;
  @Mock private ResponsePatternRepository responsePatternRepository;
  @Mock private AZClient azClient;
  @Mock private TemplateFiller templateFiller;
  @Mock private PermissionValidator permissionValidator;
  @Mock private RequestValidatorUtil requestValidatorUtil;
  @Mock private CategoryRepository categoryRepository;
  @Mock private RequestBodyService requestBodyService;
  @Mock private RequestVariableService requestVariableService;
  @Mock private ResponseService responseService;
  @Mock private ResponsePatternService responsePatternService;
  @Mock private HistoryService historyService;

  @InjectMocks private EndpointService endpointService;

  private static final Integer TEST_USER_ID = 90;
  private static final String TEST_USER_EMAIL = "test@example.com";

  @BeforeEach
  void setUp() {
    // Limpiar el SecurityContext antes de cada test
    SecurityContextHolder.clearContext();
  }

  @Test
  void createCompleteService_withValidDTO_shouldReturnServiceDTO() {
    // --- Arrange ---
    // Configurar el SecurityContext para este test específico
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    given(securityContext.getAuthentication()).willReturn(authentication);
    given(authentication.getName()).willReturn(String.valueOf(TEST_USER_ID));
    given(authentication.isAuthenticated()).willReturn(true);
    SecurityContextHolder.setContext(securityContext);

    CreateServiceDTO dto = new CreateServiceDTO();
    dto.setCategoryId(1);
    dto.setActive(true);
    dto.setName("Test Service");
    dto.setDescription("Test Desc");
    dto.setMethod(EndpointMethodEnum.GET);
    dto.setUrl("/test");
    dto.setTemplate(null);
    dto.setRequestVariables(Collections.emptyList());
    dto.setResponses(Collections.emptyList());

    // Simula repositorios
    CategoryEntity cat = new CategoryEntity();
    given(categoryRepository.findById(1)).willReturn(Optional.of(cat));

    UserEntity usr = new UserEntity();
    usr.setUserId(TEST_USER_ID);
    usr.setEmail(TEST_USER_EMAIL);
    given(userRepository.findById(TEST_USER_ID)).willReturn(Optional.of(usr));

    EndpointsEntity saved = new EndpointsEntity();
    saved.setEndpointId(42);
    saved.setName("Test Service");
    saved.setDescription("Test Desc");
    given(endpointsRepository.save(any(EndpointsEntity.class))).willReturn(saved);

    // --- Act ---
    ServiceDTO result = endpointService.createCompleteService(dto);

    // --- Assert ---
    assertEquals(42, result.getEndpointId());
    assertEquals("Test Service", result.getName());
    assertEquals("Test Desc", result.getDescription());
  }

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
    when(permissionValidator.canUserExecuteService(anyInt(), anyInt())).thenReturn(true);

    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    given(authentication.getName()).willReturn(String.valueOf(1));
    given(securityContext.getAuthentication()).willReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    given(endpointsRepository.findByEndpointId(endpointId)).willReturn(Optional.of(endpoint));
    given(requestVariableRepository.findByEndpoint_EndpointId(endpointId)).willReturn(List.of());
    given(responseRepository.findByEndpoint_EndpointId(endpointId)).willReturn(List.of());
    given(requestBodyRepository.findByEndpoint_EndpointId(endpointId)).willReturn(Optional.empty());
    given(endpointsRepository.getReferenceById(endpointId)).willReturn(endpoint);

    ServiceInfoRequestModel request = new ServiceInfoRequestModel();
    request.setBody(List.of(new BodyModel("name", "John")));

    given(requestValidatorUtil.validateRequest(request, endpointId))
        .willReturn(new RequestValidatorUtil.ValidationResponse("true", null));

    given(templateFiller.returnFilledTemplate(request.getBody(), endpointId))
        .willReturn("{\"name\":\"John\"}");

    given(azClient.callService(org.mockito.Mockito.any(), org.mockito.Mockito.eq(request)))
        .willReturn(new ServiceResponseDTO(200, "{\"message\":\"ok\"}"));

    given(responsePatternService.getMatchesForEndpoint(endpointId, "{\"message\":\"ok\"}"))
        .willReturn(Map.of(1, List.of("ok")));

    given(responseRepository.findByEndpointIdAndStatusCode(endpointId, 200))
        .willReturn(Optional.of(new ResponseEntity(1, endpoint, 200, "Response Description")));

    EndpointServiceDTO result = endpointService.executeService(endpointId, request);

    assertEquals(200, result.getStatus().getCode());
    assertEquals(List.of("ok"), result.getResponse().get(1));
  }

  @Test
  void executeService_withInvalidValidation_shouldThrow() {
    int endpointId = 1;
    ServiceInfoRequestModel request = new ServiceInfoRequestModel();

    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    given(authentication.getName()).willReturn(String.valueOf(1));
    given(securityContext.getAuthentication()).willReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    Map<String, Map<String, String>> errors = new HashMap<>();
    Map<String, String> generalError = new HashMap<>();
    generalError.put("general", "Validation error");
    errors.put("general", generalError);
    when(permissionValidator.canUserExecuteService(anyInt(), anyInt())).thenReturn(true);

    given(requestValidatorUtil.validateRequest(request, endpointId))
        .willReturn(new RequestValidatorUtil.ValidationResponse("false", errors));

    ValidationException exception =
        assertThrows(
            ValidationException.class, () -> endpointService.executeService(endpointId, request));

    assertThat(exception.getMessage()).contains("Error de validación");
  }

  @Test
  void testGetFullServiceInfoById_withValidRequest_shouldReturnResponse() {
    // Arrange
    final int ENDPOINT_ID = 1;
    final String ENDPOINT_NAME = "getFullServiceInfoTest";
    final int REQUEST_VARIABLE_ID = 93;
    final String REQUEST_VARIABLE_KEY_NAME = "VariableOneForFetchAllInfo";
    final int CATEGORY_ID = 12;
    final int USER_ID = 10;

    CategoryEntity mockCategory = new CategoryEntity();
    mockCategory.setCategoryId(CATEGORY_ID);
    mockCategory.setName("NameForFetchFullInfoTestCategory");
    mockCategory.setColor("#FFFFFF");

    UserEntity mockUser = new UserEntity();
    mockUser.setUserId(USER_ID);
    mockUser.setActive(true);
    mockUser.setRole(UserRoleEnum.ADMIN);
    mockUser.setEmail("exampleFetchAllInfoTestEmail@autozone.com");

    EndpointsEntity mockEndpoint = new EndpointsEntity();
    mockEndpoint.setEndpointId(ENDPOINT_ID);
    mockEndpoint.setCategory(mockCategory);
    mockEndpoint.setName(ENDPOINT_NAME);
    mockEndpoint.setUser(mockUser);
    mockEndpoint.setDescription("Test Description");
    mockEndpoint.setMethod(EndpointMethodEnum.GET);
    mockEndpoint.setUrl("http://testservice.com");

    given(endpointsRepository.findByEndpointId(1)).willReturn(Optional.of(mockEndpoint));

    RequestVariableEntity mockVar = new RequestVariableEntity();
    mockVar.setRequestVariableId(REQUEST_VARIABLE_ID);
    mockVar.setEndpoint(mockEndpoint);
    mockVar.setKeyName(REQUEST_VARIABLE_KEY_NAME);
    mockVar.setDefaultValue("ijjoijopjl");
    mockVar.setType(RequestVariableTypeEnum.HEADER);
    mockVar.setDescription("A test variable");

    List<RequestVariableEntity> mockRequestVariables = new ArrayList<>();
    mockRequestVariables.add(mockVar);

    given(requestVariableRepository.findByEndpoint_EndpointId(ENDPOINT_ID))
        .willReturn(mockRequestVariables);

    ResponseEntity mockResponse = new ResponseEntity();
    mockResponse.setEndpoint(mockEndpoint);
    mockResponse.setStatusCode(200);
    mockResponse.setDescription("Success");

    List<ResponseEntity> mockResponses = new ArrayList<>();
    mockResponses.add(mockResponse);

    given(responseRepository.findByEndpoint_EndpointId(ENDPOINT_ID)).willReturn(mockResponses);

    // Act
    CreateServiceDTO result = endpointService.getFullServiceInfoById(ENDPOINT_ID);

    // Assert
    assertNotNull(result);
    assertEquals(ENDPOINT_NAME, result.getName());
    assertEquals(EndpointMethodEnum.GET, result.getMethod());
    assertEquals(
        REQUEST_VARIABLE_ID,
        requestVariableRepository
            .findByEndpoint_EndpointId(ENDPOINT_ID)
            .getFirst()
            .getRequestVariableId());
    assertEquals(REQUEST_VARIABLE_KEY_NAME, result.getRequestVariables().getFirst().getKey());
  }

  @Test
  void testGetFullServiceInfoById_withInvalidRequest_shouldThrow() {
    // Act & Assert
    ServiceNotFoundException exception =
        assertThrows(
            ServiceNotFoundException.class, () -> endpointService.getFullServiceInfoById(1));

    assertThat(exception.getMessage()).contains("Endpoint not found with id");
  }
}
