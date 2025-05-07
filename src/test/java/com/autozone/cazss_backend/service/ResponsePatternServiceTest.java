package com.autozone.cazss_backend.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.autozone.cazss_backend.entity.ResponseEntity;
import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import com.autozone.cazss_backend.repository.ResponsePatternRepository;
import com.autozone.cazss_backend.util.RegexParser;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
public class ResponsePatternServiceTest {

  @Mock private ResponsePatternRepository responsePatternRepository;

  @Mock private RegexParser regexParser;

  @InjectMocks private ResponsePatternService responsePatternService;

  private List<ResponsePatternEntity> mockPatterns;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    // Prepare a mock ResponsePatternEntity
    ResponsePatternEntity pattern = new ResponsePatternEntity();
    pattern.setName("email");
    pattern.setPattern(".*@.*\\..*");

    ResponseEntity response = new ResponseEntity();
    response.setResponseId(1); // assuming this field exists
    pattern.setResponse(response);

    pattern.setDescription("Email matcher");
    pattern.setParentId(0);
    pattern.setIsLeaf(true);

    mockPatterns = List.of(pattern);
  }

  @Test
  public void testGetMatchesForEndpoint_WithPatterns() {
    Integer endpointId = 1;
    String input = "contact@example.com";

    when(responsePatternRepository.findByResponse_ResponseId(endpointId)).thenReturn(mockPatterns);

    Map<String, List<String>> expectedMatches = Map.of("email", List.of("contact@example.com"));
    when(regexParser.getResponsePatternMatches(mockPatterns, input)).thenReturn(expectedMatches);

    Map<String, List<String>> result =
        responsePatternService.getMatchesForEndpoint(endpointId, input);

    assertEquals(expectedMatches, result);
    verify(regexParser).getResponsePatternMatches(mockPatterns, input);
  }

  @Test
  public void testGetMatchesForEndpoint_NoPatternsFound() {
    Integer endpointId = 2;
    String input = "no pattern";

    when(responsePatternRepository.findByResponse_ResponseId(endpointId))
        .thenReturn(Collections.emptyList());

    Map<String, List<String>> result =
        responsePatternService.getMatchesForEndpoint(endpointId, input);

    assertEquals(1, result.size());
    assertTrue(result.containsKey("content"));
    assertEquals(List.of(input), result.get("content"));

    verify(regexParser, never()).getResponsePatternMatches(any(), any());
  }

  @Test
  public void testGetMatchesForEndpoint_NullPatterns() {
    Integer endpointId = 3;
    String input = "some input";

    when(responsePatternRepository.findByResponse_ResponseId(endpointId)).thenReturn(null);

    Map<String, List<String>> result =
        responsePatternService.getMatchesForEndpoint(endpointId, input);

    assertEquals(1, result.size());
    assertTrue(result.containsKey("content"));
    assertEquals(List.of(input), result.get("content"));

    verify(regexParser, never()).getResponsePatternMatches(any(), any());
  }
}
