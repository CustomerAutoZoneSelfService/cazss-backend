package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.*;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.exceptions.*;
import com.autozone.cazss_backend.model.ServiceInfoRequestModel;
import com.autozone.cazss_backend.model.StatusModel;
import com.autozone.cazss_backend.repository.*;
import com.autozone.cazss_backend.util.*;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class EndpointService {
  private static final Logger logger = LoggerFactory.getLogger(EndpointService.class);

  @Autowired private EndpointsRepository endpointsRepository;

  @Autowired private RequestVariableRepository requestVariableRepository;

  @Autowired private ResponseRepository responseRepository;

  @Autowired private ResponsePatternRepository responsePatternRepository;

  @Autowired private RequestBodyRepository requestBodyRepository;

  @Autowired private AZClient azClient;

  @Autowired private TemplateFiller templateFiller;

  @Autowired private RequestValidatorUtil requestValidatorUtil;

  @Autowired private ResponsePatternService responsePatternService;

  @Autowired private HistoryService historyService;

  @Autowired private RequestBodyService requestBodyService;

  @Autowired private RequestVariableService requestVariableService;

  @Autowired private ResponseService responseService;

  @Autowired private UserRepository userRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private EndpointAuthenticationUtil endpointAuthenticationUtil;

  @Autowired private AuthenticationStrategyRepository authenticationStrategyRepository;
  @Autowired private PermissionValidator permissionValidator;

  public List<ServiceDTO> getAvailableServices() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer userId = Integer.parseInt(authentication.getName());

    if (permissionValidator.isAdmin(userId)) {
      return endpointsRepository.findAllServiceDTOs();
    } else if (permissionValidator.isConfigurator(userId)) {
      return endpointsRepository.findUserSpecificAndNullCategoryServices(userId);
    } else {
      return endpointsRepository.findUserSpecificServices(userId);
    }
  }

  @Autowired private UserDataUtil userDataUtil;

  public List<ServiceDTO> getAllServices() {
    return endpointsRepository.findAllServiceDTOs();
  }

  // TODO validate if endpoint is active
  public ServiceInfoDTO getServiceById(Integer id) {
    System.out.println("ENTERING GET SERVICE BY ID");
    EndpointsEntity endpoint =
        endpointsRepository
            .findByEndpointId(id)
            .orElseThrow(() -> new ServiceNotFoundException("Endpoint not found with id: " + id));

    if (!endpoint.getActive()) {
      throw new ServiceNotActiveException(id);
    }

    ServiceInfoDTO serviceInformation =
        new ServiceInfoDTO(
            endpoint.getName(),
            endpoint.getDescription(),
            endpoint.getActive(),
            endpoint.getMethod(),
            endpoint.getUrl());

    serviceInformation.setId(endpoint.getEndpointId());

    List<RequestVariableEntity> requestVariables =
        requestVariableRepository.findByEndpoint_EndpointId(endpoint.getEndpointId());
    serviceInformation.setVariables(
        requestVariables.stream()
            .map(
                requestVaraibleEntity ->
                    new RequestVariableDTO(
                        requestVaraibleEntity.getRequestVariableId(),
                        requestVaraibleEntity.getType(),
                        requestVaraibleEntity.getKeyName(),
                        requestVaraibleEntity.getDefaultValue(),
                        requestVaraibleEntity.getCustomizable(),
                        requestVaraibleEntity.getDescription()))
            .collect(Collectors.toList()));

    List<ResponseEntity> responses =
        responseRepository.findByEndpoint_EndpointId(endpoint.getEndpointId());
    serviceInformation.setResponses(
        responses.stream()
            .map(
                responseEntity ->
                    new ServiceResponseDTO(
                        responseEntity.getStatusCode(), responseEntity.getDescription()))
            .collect(Collectors.toList()));

    Set<Integer> responseIds =
        responses.stream().map(ResponseEntity::getResponseId).collect(Collectors.toSet());

    List<ResponsePatternEntity> responsePatterns =
        responsePatternRepository.findByResponse_ResponseIdInAndIsLeafTrue(responseIds);
    serviceInformation.setFilters(
        responsePatterns.stream()
            .map(
                responsePatternEntity ->
                    new FilterDTO(
                        responsePatternEntity.getResponsePatternId(),
                        responsePatternEntity.getPattern(),
                        responsePatternEntity.getName(),
                        responsePatternEntity.getDescription()))
            .collect(Collectors.toList()));

    RequestBodyEntity requestBody =
        requestBodyRepository.findByEndpoint_EndpointId(id).orElse(null);
    serviceInformation.setTemplate(requestBody != null ? requestBody.getTemplate() : "");

    return serviceInformation;
  }

  /**
   * Retrieves ALL the info on a given endpoint ID by ID.
   *
   * @param id The ID of the endpoint from which to retrieve all info.
   * @return A CreateServiceDTO containing ALL info on the endpoint.
   */
  public CreateServiceDTO getFullServiceInfoById(Integer id) {
    System.out.println("ENTERING GET SERVICE BY ID");
    EndpointsEntity endpoint =
        endpointsRepository
            .findByEndpointId(id)
            .orElseThrow(() -> new ServiceNotFoundException("Endpoint not found with id: " + id));

    CreateServiceDTO serviceInformation = new CreateServiceDTO();

    serviceInformation.setCategoryId(
        endpoint.getCategory() != null ? endpoint.getCategory().getCategoryId() : null);
    serviceInformation.setActive(endpoint.getActive());
    serviceInformation.setName(endpoint.getName());
    serviceInformation.setDescription(endpoint.getDescription());
    serviceInformation.setMethod(endpoint.getMethod());
    serviceInformation.setUrl(endpoint.getUrl());
    serviceInformation.setAuthenticationStrategy(
        endpoint.getAuthStrategy() != null ? endpoint.getAuthStrategy().getAuthStrategyId() : null);

    RequestBodyEntity requestBody =
        requestBodyRepository.findByEndpoint_EndpointId(id).orElse(null);
    serviceInformation.setTemplate(requestBody != null ? requestBody.getTemplate() : "");

    List<RequestVariableEntity> requestVariables =
        requestVariableRepository.findByEndpoint_EndpointId(endpoint.getEndpointId());
    serviceInformation.setRequestVariables(
        requestVariables.stream()
            .map(
                requestVariableEntity ->
                    new CreateRequestVariableDTO(
                        requestVariableEntity.getType(),
                        requestVariableEntity.getKeyName(),
                        requestVariableEntity.getDefaultValue(),
                        requestVariableEntity.getCustomizable(),
                        requestVariableEntity.getDescription()))
            .collect(Collectors.toList()));

    List<ResponseEntity> responses =
        responseRepository.findByEndpoint_EndpointId(endpoint.getEndpointId());
    serviceInformation.setResponses(
        responses.stream()
            .map(
                responseEntity ->
                    new CreateResponseDTO(
                        responseEntity.getStatusCode(),
                        responseEntity.getDescription(),
                        returnCreateResponsePatternDTOListFromResponseId(
                            responseEntity.getResponseId())))
            .collect(Collectors.toList()));

    return serviceInformation;
  }

  @Transactional
  public ServiceDTO createCompleteService(CreateServiceDTO serviceDTO) {
    logger.debug("Creating {} service: {}", serviceDTO.getName(), serviceDTO);

    // Category and User
    List<Object> independentEntities =
        getExistingEntitiesIndependentOfService(serviceDTO.getCategoryId());

    // Get authenticated user id from security context
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ServiceNotFoundException("No authenticated user found");
    }

    String userIdStr = authentication.getName();
    Integer userId;
    try {
      userId = Integer.parseInt(userIdStr);
    } catch (NumberFormatException e) {
      logger.error("Invalid user ID format in JWT token: {}", userIdStr);
      throw new ServiceNotFoundException("Invalid user ID format in authentication token");
    }

    // Endpoint
    EndpointsEntity newEndpoint = new EndpointsEntity();
    mapDtoToEndpoint(
        newEndpoint,
        (CategoryEntity) independentEntities.get(0),
        (UserEntity) independentEntities.get(1),
        serviceDTO);

    EndpointsEntity newEndpointEntity = endpointsRepository.save(newEndpoint);
    logger.debug("Created endpoint with id {}", newEndpointEntity.getEndpointId());

    setEntitiesDependentOfService(newEndpointEntity, serviceDTO);

    // Result
    return new ServiceDTO(
        newEndpointEntity.getEndpointId(),
        newEndpointEntity.getName(),
        newEndpointEntity.getDescription());
  }

  /**
   * A shared method to map properties from a DTO to an EndpointsEntity. This handles both creation
   * and updates, eliminating duplicate code.
   *
   * @param endpoint The entity to populate (can be new or existing).
   * @param serviceDTO The DTO containing the new data.
   * @param category The associated CategoryEntity.
   * @param user The associated UserEntity.
   */
  private void mapDtoToEndpoint(
      EndpointsEntity endpoint,
      CategoryEntity category,
      UserEntity user,
      CreateServiceDTO serviceDTO) {
    if (serviceDTO.getAuthenticationStrategy() != null) {
      AuthenticationStrategyEntity authenticationStrategyEntity =
          authenticationStrategyRepository
              .findById(serviceDTO.getAuthenticationStrategy())
              .orElseThrow(
                  () ->
                      new AuthenticationStrategyNotFoundException(
                          "Authentication strategy not found with id: "
                              + serviceDTO.getAuthenticationStrategy()));
      endpoint.setAuthStrategy(authenticationStrategyEntity);
    } else {
      endpoint.setAuthStrategy(null);
    }
    endpoint.setCategory(category);
    endpoint.setUser(user);
    endpoint.setActive(serviceDTO.getActive());
    endpoint.setName(serviceDTO.getName());
    endpoint.setDescription(serviceDTO.getDescription());
    endpoint.setMethod(serviceDTO.getMethod());
    endpoint.setUrl(serviceDTO.getUrl());
  }

  /**
   * Updates an existing service endpoint and its dependent entities in a single transaction.
   *
   * @param id The ID of the service to update.
   * @param updatedService The DTO containing the updated service information.
   * @return A simplified {@link ServiceDTO} of the updated service.
   * @throws ServiceNotFoundException if no endpoint with the given ID is found.
   */
  @Transactional
  public ServiceDTO updateCompleteService(final Integer id, CreateServiceDTO updatedService) {
    EndpointsEntity existingEndpoint =
        endpointsRepository
            .findByEndpointId(id)
            .orElseThrow(() -> new ServiceNotFoundException("Service not found with id " + id));
    logger.debug("Found existing endpoint with id: {}", existingEndpoint.getEndpointId());
    // Category and User
    List<Object> independentEntities =
        getExistingEntitiesIndependentOfService(updatedService.getCategoryId());
    mapDtoToEndpoint(
        existingEndpoint,
        (CategoryEntity) independentEntities.getFirst(),
        existingEndpoint.getUser(), // The user should be the one that created it originally
        updatedService);
    setEntitiesDependentOfService(existingEndpoint, updatedService);

    return new ServiceDTO(
        existingEndpoint.getEndpointId(),
        existingEndpoint.getName(),
        existingEndpoint.getDescription());
  }

  private List<Object> getExistingEntitiesIndependentOfService(Integer categoryId) {
    List<Object> entities = new ArrayList<>();
    // Category
    CategoryEntity category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(
                () -> new ServiceNotFoundException("Category not found with id " + categoryId));

    entities.add(category);

    // User
    UserEntity user = userDataUtil.getUserEntity();

    logger.debug("Found user: {}", user.getUserId());

    entities.add(user);

    return entities;
  }

  private void setEntitiesDependentOfService(
      EndpointsEntity endpoint, CreateServiceDTO serviceDTO) {
    // Request body
    if (serviceDTO.getTemplate() != null) {
      requestBodyService.createOrUpdateRequestBody(endpoint, serviceDTO.getTemplate());
      logger.debug("Created request body for endpoint id {}", endpoint.getEndpointId());
    }

    // Request variables
    if (serviceDTO.getRequestVariables() != null && !serviceDTO.getRequestVariables().isEmpty()) {
      for (CreateRequestVariableDTO requestVariableDTO : serviceDTO.getRequestVariables())
        requestVariableService.updateRequestVariables(endpoint, serviceDTO.getRequestVariables());
      logger.debug("Updated request variables for endpoint id {}", endpoint.getEndpointId());
    }

    // Responses
    if (serviceDTO.getResponses() != null && !serviceDTO.getResponses().isEmpty()) {
      responseService.updateResponses(endpoint, serviceDTO.getResponses());
      logger.debug(
          "Created {} responses for endpoint id {}",
          serviceDTO.getResponses().size(),
          endpoint.getEndpointId());
    }
  }

  // Method to execute the service based on the request model
  public EndpointServiceDTO executeService(
      Integer serviceId, ServiceInfoRequestModel serviceInfoRequestModel) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer userId = Integer.parseInt(authentication.getName());
    if (permissionValidator.canUserExecuteService(userId, serviceId)) {

      logger.debug("=== INICIO EJECUCIÓN SERVICIO ===");
      logger.debug("Request recibido: {}", serviceInfoRequestModel);

      // Validate request using RequestValidatorUtil
      logger.debug("Iniciando validación del request...");
      RequestValidatorUtil.ValidationResponse validationResponse =
          requestValidatorUtil.validateRequest(serviceInfoRequestModel, serviceId);
      logger.debug("Resultado de la validación: {}", validationResponse);
      if (!"true".equals(validationResponse.getStatus())) {
        logger.error("Validación fallida: {}", validationResponse.getErrores());
        throw new ValidationException("Error de validación: " + validationResponse.getErrores());
      }

      ServiceInfoDTO serviceInfo = getServiceById(serviceId);
      logger.debug("Información del servicio obtenida: {}", serviceInfo);

      EndpointsEntity endpoint = endpointsRepository.getReferenceById(serviceId);
      AuthenticationStrategyEntity authStrategy = endpoint.getAuthStrategy();

      if (authStrategy != null) {
        logger.debug(
            "Hooking request with the following authentication strategy: {}", authStrategy);
        endpointAuthenticationUtil.hookRequest(serviceInfoRequestModel, authStrategy);
      }

      String template =
          templateFiller.returnFilledTemplate(
              serviceInfoRequestModel.getBody(), serviceInfo.getId());

      serviceInfo.setTemplate(template);

      ServiceResponseDTO serviceResponse =
          azClient.callService(serviceInfo, serviceInfoRequestModel);

      int code = serviceResponse.getStatusCode();

      Optional<ResponseEntity> resRepo =
          responseRepository.findByEndpointIdAndStatusCode(serviceId, code);

      String description =
          resRepo.isPresent()
              ? resRepo.get().getDescription()
              : HttpStatus.valueOf(code).getReasonPhrase(); // OK, Bad Request, etc
      StatusModel status = new StatusModel(code, description);

      // regexparser Lou/edgar
      Map<Integer, List<String>> parsedResponse = new HashMap<>();
      if (serviceResponse.getResponse() != null
          && !serviceResponse.getResponse().trim().isEmpty()
          && resRepo.isPresent()) {
        parsedResponse =
            responsePatternService.getMatchesForEndpoint(
                resRepo.get().getResponseId(), serviceResponse.getResponse());
      } else {
        logger.warn("Empty or null response description for endpoint {}", serviceInfo.getId());
      }

      // SAVE IN HISTORY
      UserEntity user = userDataUtil.getUserEntity();
      historyService.addHistory(
          user,
          endpoint,
          status.getCode(),
          serviceInfoRequestModel.getBody().toString(),
          parsedResponse.toString());
      // SAVE IN HISTORY - END

      return new EndpointServiceDTO(status, parsedResponse);
    } else {
      throw new UnauthorizedUserException("User does not have access to this endpoint");
    }
  }

  private List<CreateResponsePatternDTO> returnCreateResponsePatternDTOListFromResponseId(
      Integer responseId) {
    List<ResponsePatternEntity> responsePatterns =
        responsePatternRepository.findByResponse_ResponseId(responseId);
    return (responsePatterns != null
        ? responsePatterns.stream()
            .map(
                responsePatternEntity ->
                    new CreateResponsePatternDTO(
                        responsePatternEntity.getResponsePatternId(),
                        responsePatternEntity.getParentId(),
                        responsePatternEntity.getName(),
                        responsePatternEntity.getDescription(),
                        responsePatternEntity.getPattern(),
                        responsePatternEntity.getIsLeaf()))
            .collect(Collectors.toList())
        : null);
  }

  /**
   * Finds an endpoint by its ID or throws a standardized exception.
   *
   * @param id The ID of the endpoint to find.
   * @return The found EndpointsEntity.
   * @throws ServiceNotFoundException if the endpoint does not exist.
   */
  public EndpointsEntity findEndpointById(Integer id) {
    return endpointsRepository
        .findById(id)
        .orElseThrow(() -> new ServiceNotFoundException("Endpoint not found with id: " + id));
  }
}
