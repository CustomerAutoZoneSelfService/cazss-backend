package com.autozone.cazss_backend.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.autozone.cazss_backend.entity.RequestBodyEntity;
import com.autozone.cazss_backend.model.BodyModel;
import com.autozone.cazss_backend.repository.RequestBodyRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TemplateFillerTest {

  @Mock private RequestBodyRepository requestBodyRepository;

  @InjectMocks private TemplateFiller templateFiller;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testReturnFilledTemplate_success() {
    // Arrange
    Integer endpointId = 1;
    String rawTemplate = "{\"name\":\"{{username}}\",\"email\":\"{{email}}\"}";
    RequestBodyEntity requestBodyEntity = new RequestBodyEntity();
    requestBodyEntity.setTemplate(rawTemplate);

    when(requestBodyRepository.findByEndpoint_EndpointId(endpointId))
        .thenReturn(Optional.of(requestBodyEntity));

    List<BodyModel> bodyList =
        List.of(new BodyModel("username", "Alice"), new BodyModel("email", "alice@example.com"));

    // Act
    String result = templateFiller.returnFilledTemplate(bodyList, endpointId);

    // Assert
    String expected = "{\"name\":\"Alice\",\"email\":\"alice@example.com\"}";
    assertEquals(expected, result);
  }

  @Test
  public void testReturnFilledTemplate_missingTemplate() {
    // Arrange
    Integer endpointId = 1;
    when(requestBodyRepository.findByEndpoint_EndpointId(endpointId)).thenReturn(Optional.empty());

    List<BodyModel> bodyList = List.of(new BodyModel("username", "Alice"));

    // Act
    String result = templateFiller.returnFilledTemplate(bodyList, endpointId);

    // Assert
    assertEquals("", result);
  }

  @Test
  public void testReturnFilledTemplate_partialSubstitution() {
    // Arrange
    Integer endpointId = 2;
    String rawTemplate = "{\"name\":\"{{username}}\",\"email\":\"{{email}}\"}";
    RequestBodyEntity requestBodyEntity = new RequestBodyEntity();
    requestBodyEntity.setTemplate(rawTemplate);

    when(requestBodyRepository.findByEndpoint_EndpointId(endpointId))
        .thenReturn(Optional.of(requestBodyEntity));

    List<BodyModel> bodyList =
        List.of(
            new BodyModel("username", "Bob") // Missing email
            );

    // Act
    String result = templateFiller.returnFilledTemplate(bodyList, endpointId);

    // Assert
    String expected = "{\"name\":\"Bob\",\"email\":\"{{email}}\"}";
    assertEquals(expected, result);
  }

  @Test
  public void testReturnFilledTemplate_emptyBodyList() {
    // Arrange
    Integer endpointId = 3;
    String rawTemplate = "{\"name\":\"{{username}}\"}";
    RequestBodyEntity requestBodyEntity = new RequestBodyEntity();
    requestBodyEntity.setTemplate(rawTemplate);

    when(requestBodyRepository.findByEndpoint_EndpointId(endpointId))
        .thenReturn(Optional.of(requestBodyEntity));

    List<BodyModel> bodyList = List.of();

    // Act
    String result = templateFiller.returnFilledTemplate(bodyList, endpointId);

    // Assert
    assertEquals(rawTemplate, result);
  }
}
