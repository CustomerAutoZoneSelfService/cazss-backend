package com.autozone.cazss_backend.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.autozone.cazss_backend.DTO.CreateResponsePatternDTO;
import com.autozone.cazss_backend.entity.ResponseEntity;
import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import com.autozone.cazss_backend.repository.ResponsePatternRepository;
import com.autozone.cazss_backend.repository.ResponseRepository;
import com.autozone.cazss_backend.util.ResponsePatternParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
public class ResponsePatternServiceTest {

  @Mock private ResponsePatternRepository responsePatternRepository;

  @Mock private ResponsePatternParser responsePatternParser;

  @Mock private ResponseRepository responseRepository;

  @InjectMocks private ResponsePatternService responsePatternService;

  private List<ResponsePatternEntity> mockPatterns;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    // Prepare a mock ResponsePatternEntity
    ResponsePatternEntity pattern = new ResponsePatternEntity();
    pattern.setResponsePatternId(1);
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

    Map<Integer, List<String>> expectedMatches = Map.of(1, List.of("contact@example.com"));
    when(responsePatternParser.getResponsePatternMatches(mockPatterns, input))
        .thenReturn(expectedMatches);

    Map<Integer, List<String>> result =
        responsePatternService.getMatchesForEndpoint(endpointId, input);

    assertEquals(expectedMatches, result);
    verify(responsePatternParser).getResponsePatternMatches(mockPatterns, input);
  }

  @Test
  public void testGetMatchesForEndpoint_NoPatternsFound() {
    Integer endpointId = 2;
    String input = "no pattern";

    when(responsePatternRepository.findByResponse_ResponseId(endpointId))
        .thenReturn(Collections.emptyList());

    Map<Integer, List<String>> result =
        responsePatternService.getMatchesForEndpoint(endpointId, input);

    assertEquals(1, result.size());
    assertTrue(result.containsKey(-1));
    assertEquals(List.of(input), result.get(-1));

    verify(responsePatternParser, never()).getResponsePatternMatches(any(), any());
  }

  @Test
  public void testGetMatchesForEndpoint_NullPatterns() {
    Integer endpointId = 3;
    String input = "some input";

    when(responsePatternRepository.findByResponse_ResponseId(endpointId)).thenReturn(null);

    Map<Integer, List<String>> result =
        responsePatternService.getMatchesForEndpoint(endpointId, input);

    assertEquals(1, result.size());
    assertTrue(result.containsKey(-1));
    assertEquals(List.of(input), result.get(-1));

    verify(responsePatternParser, never()).getResponsePatternMatches(any(), any());
  }

  @Test
  public void testAddPatterns() {
    Integer responseId = 1;

    ResponseEntity responseEntity = new ResponseEntity();
    responseEntity.setResponseId(responseId);

    CreateResponsePatternDTO dto = new CreateResponsePatternDTO();
    dto.setResponsePatternId(-1);
    dto.setName("Email");
    dto.setDescription("Email matcher");
    dto.setPattern(".*@.*\\..*");
    dto.setIsLeaf(true);
    dto.setParentId(null);

    List<CreateResponsePatternDTO> dtoList = new ArrayList<>();
    dtoList.add(dto);

    when(responseRepository.findById(responseId)).thenReturn(Optional.of(responseEntity));

    ResponsePatternEntity saved = new ResponsePatternEntity();
    saved.setResponsePatternId(10);
    saved.setName(dto.getName());
    saved.setDescription(dto.getDescription());
    saved.setPattern(dto.getPattern());
    saved.setIsLeaf(dto.getIsLeaf());
    saved.setParentId(dto.getParentId());
    saved.setResponse(responseEntity);

    when(responsePatternRepository.save(any(ResponsePatternEntity.class))).thenReturn(saved);

    List<CreateResponsePatternDTO> result = responsePatternService.addPatterns(responseId, dtoList);

    assertEquals(1, result.size());
    assertEquals("Email", result.get(0).getName());
    assertEquals(".*@.*\\..*", result.get(0).getPattern());
  }

  @Test
  public void testInsertTree() {
    Integer responseId = 1;

    ResponseEntity responseEntity = new ResponseEntity();
    responseEntity.setResponseId(responseId);

    when(responseRepository.findById(responseId)).thenReturn(Optional.of(responseEntity));

    CreateResponsePatternDTO root = new CreateResponsePatternDTO();
    root.setResponsePatternId(-1);
    root.setParentId(null);
    root.setName("Root");
    root.setDescription("Root Node");
    root.setPattern(".*");
    root.setIsLeaf(true);

    ResponsePatternEntity savedEntity = new ResponsePatternEntity();
    savedEntity.setResponsePatternId(1);
    savedEntity.setName("Root");
    savedEntity.setDescription("Root Node");
    savedEntity.setPattern(".*");
    savedEntity.setIsLeaf(true);
    savedEntity.setParentId(null);
    savedEntity.setResponse(responseEntity);

    when(responsePatternRepository.save(any(ResponsePatternEntity.class))).thenReturn(savedEntity);

    Map<Integer, List<CreateResponsePatternDTO>> tree = new HashMap<>();
    tree.put(null, List.of(root)); // Root node

    List<CreateResponsePatternDTO> result = responsePatternService.insertTree(tree, responseId);

    assertEquals(1, result.size());
    assertEquals("Root", result.get(0).getName());
    assertEquals(".*", result.get(0).getPattern());
  }

  @Test
  public void testParseToDTO() {
    ResponsePatternEntity entity = new ResponsePatternEntity();
    entity.setResponsePatternId(100);
    entity.setParentId(0);
    entity.setName("TestPattern");
    entity.setDescription("Test Description");
    entity.setIsLeaf(true);

    List<CreateResponsePatternDTO> dtoList = responsePatternService.parseToDTO(List.of(entity));

    assertEquals(1, dtoList.size());
    CreateResponsePatternDTO dto = dtoList.get(0);
    assertEquals(Integer.valueOf(100), dto.getResponsePatternId());
    assertEquals(Integer.valueOf(0), dto.getParentId());
    assertEquals("TestPattern", dto.getName());
    assertEquals("Test Description", dto.getDescription());
    assertTrue(dto.getIsLeaf());
  }

  @Test
  public void testReplacePatterns() {
    Integer responseId = 1;

    ResponseEntity response = new ResponseEntity();
    response.setResponseId(responseId);

    CreateResponsePatternDTO dto = new CreateResponsePatternDTO();
    dto.setResponsePatternId(-1);
    dto.setName("Phone");
    dto.setPattern("\\d{10}");
    dto.setDescription("Phone matcher");
    dto.setParentId(null);
    dto.setIsLeaf(true);

    List<CreateResponsePatternDTO> dtoList = new ArrayList<>();
    dtoList.add(dto);

    when(responseRepository.findById(responseId)).thenReturn(Optional.of(response));

    // Simulate delete and add
    doNothing().when(responsePatternRepository).deleteAll();

    ResponsePatternEntity saved = new ResponsePatternEntity();
    saved.setResponsePatternId(20);
    saved.setName(dto.getName());
    saved.setPattern(dto.getPattern());
    saved.setDescription(dto.getDescription());
    saved.setParentId(dto.getParentId());
    saved.setIsLeaf(dto.getIsLeaf());
    saved.setResponse(response);

    when(responsePatternRepository.save(any(ResponsePatternEntity.class))).thenReturn(saved);

    List<CreateResponsePatternDTO> result =
        responsePatternService.replacePatterns(responseId, dtoList);

    assertEquals(1, result.size());
    assertEquals("Phone", result.get(0).getName());
    assertEquals("\\d{10}", result.get(0).getPattern());
  }

  @Test
  public void testReplaceTree() {
    Integer responseId = 1;

    ResponseEntity responseEntity = new ResponseEntity();
    responseEntity.setResponseId(responseId);

    CreateResponsePatternDTO root = new CreateResponsePatternDTO();
    root.setResponsePatternId(-1);
    root.setParentId(null);
    root.setName("RootNode");
    root.setPattern("root.*");
    root.setDescription("Root");
    root.setIsLeaf(true);

    when(responseRepository.findById(responseId)).thenReturn(Optional.of(responseEntity));

    // simulate deletion
    doNothing().when(responsePatternRepository).deleteByResponse_ResponseId(responseId);

    ResponsePatternEntity saved = new ResponsePatternEntity();
    saved.setResponsePatternId(30);
    saved.setName("RootNode");
    saved.setPattern("root.*");
    saved.setDescription("Root");
    saved.setIsLeaf(true);
    saved.setParentId(null);
    saved.setResponse(responseEntity);

    when(responsePatternRepository.save(any(ResponsePatternEntity.class))).thenReturn(saved);

    Map<Integer, List<CreateResponsePatternDTO>> tree = new HashMap<>();
    tree.put(null, List.of(root));

    List<CreateResponsePatternDTO> result = responsePatternService.replaceTree(tree, responseId);

    assertEquals(1, result.size());
    assertEquals("RootNode", result.get(0).getName());
  }

  @Test
  public void testUpdatePatterns() {
    Integer responseId = 1;

    ResponseEntity response = new ResponseEntity();
    response.setResponseId(responseId);

    CreateResponsePatternDTO dto = new CreateResponsePatternDTO();
    dto.setResponsePatternId(1); // existing ID
    dto.setName("UpdatedName");
    dto.setPattern("new.*pattern");
    dto.setDescription("Updated Description");
    dto.setIsLeaf(true);
    dto.setParentId(null);

    List<CreateResponsePatternDTO> dtoList = new ArrayList<>();
    dtoList.add(dto);

    ResponsePatternEntity existing = new ResponsePatternEntity();
    existing.setResponsePatternId(1);
    existing.setName("OldName");
    existing.setPattern("old.*pattern");
    existing.setDescription("Old Description");
    existing.setIsLeaf(true);
    existing.setParentId(null);
    existing.setResponse(response);

    when(responseRepository.findById(responseId)).thenReturn(Optional.of(response));
    when(responsePatternRepository.findByResponse_ResponseId(responseId))
        .thenReturn(List.of(existing));
    when(responsePatternRepository.findById(1)).thenReturn(Optional.of(existing));

    ResponsePatternEntity saved = new ResponsePatternEntity();
    saved.setResponsePatternId(1);
    saved.setName("UpdatedName");
    saved.setPattern("new.*pattern");
    saved.setDescription("Updated Description");
    saved.setIsLeaf(true);
    saved.setParentId(null);
    saved.setResponse(response);

    when(responsePatternRepository.save(any(ResponsePatternEntity.class))).thenReturn(saved);

    List<CreateResponsePatternDTO> result =
        responsePatternService.updatePatterns(responseId, dtoList);

    assertEquals(1, result.size());
    assertEquals("UpdatedName", result.get(0).getName());
    assertEquals("new.*pattern", result.get(0).getPattern());
    assertTrue(result.get(0).getIsLeaf());
  }
}
