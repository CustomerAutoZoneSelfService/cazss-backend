package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CreateResponsePatternDTO;
import com.autozone.cazss_backend.entity.ResponseEntity;
import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import com.autozone.cazss_backend.repository.ResponsePatternRepository;
import com.autozone.cazss_backend.repository.ResponseRepository;
import com.autozone.cazss_backend.util.RegexParser;
import com.autozone.cazss_backend.util.ResponsePatternTreeValidator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ResponsePatternService {

  private final ResponsePatternRepository responsePatternRepository;

  private final ResponseRepository responseRepository;

  private final RegexParser regexParser;

  private static final Logger logger = LoggerFactory.getLogger(ResponsePatternService.class);

  public ResponsePatternService(
      ResponsePatternRepository responsePatternRepository,
      RegexParser regexParser,
      ResponseRepository responseRepository) {
    this.responsePatternRepository = responsePatternRepository;
    this.regexParser = regexParser;
    this.responseRepository = responseRepository;
  }

  /**
   * Retrieves the response patterns for a given endpoint ID and parses the input string to find
   * matches.
   *
   * @param endpointId The ID of the endpoint for which to retrieve response patterns.
   * @param inputString The input string to parse for matches against the response patterns.
   * @return A map where the keys are pattern names and the values are lists of matched strings.
   */
  public Map<String, List<String>> getMatchesForEndpoint(Integer endpointId, String inputString) {
    List<ResponsePatternEntity> patterns =
        responsePatternRepository.findByResponse_ResponseId(endpointId);

    if (patterns == null || patterns.isEmpty()) {
      Map<String, List<String>> fallback = new HashMap<>();
      fallback.put("content", List.of(inputString));
      return fallback;
    }

    return regexParser.getResponsePatternMatches(patterns, inputString);
  }

  /**
   * Transforms a list of entities to a list of models used in response pattern services
   *
   * @param entitiesList
   * @return
   */
  public List<CreateResponsePatternDTO> parseToDTO(List<ResponsePatternEntity> entitiesList) {
    List<CreateResponsePatternDTO> dtoList = new ArrayList<>();

    for (ResponsePatternEntity entity : entitiesList) {
      CreateResponsePatternDTO dto = new CreateResponsePatternDTO();
      dto.setResponsePatternId(entity.getResponsePatternId());
      dto.setParentId(entity.getParentId());
      dto.setName(entity.getName());
      dto.setDescription(entity.getDescription());
      dto.setLeaf(entity.getIsLeaf());
      dtoList.add(dto);
    }

    return dtoList;
  }

  /**
   * Inserts new response patterns in the data base connecting to the old ones, whilst maintaining
   * the tree structure
   *
   * @param tree
   * @param responseId
   * @return
   */
  public List<CreateResponsePatternDTO> insertTree(
      Map<Integer, List<CreateResponsePatternDTO>> tree, Integer responseId) {

    CreateResponsePatternDTO root = tree.get(null).get(0);

    ResponseEntity responseEntity =
        responseRepository
            .findById(responseId)
            .orElseThrow(
                () -> new IllegalArgumentException("Response ID " + responseId + " not found"));

    Map<Integer, Integer> idMap = new HashMap<>(); // oldId to newId
    List<CreateResponsePatternDTO> result = new ArrayList<>();

    dfsInsert(root, tree, responseId, idMap, responseEntity, result);

    return result;
  }

  /**
   * Replaces certain nodes in the data base with the new ones, whilst maintaining the tree
   * structure
   *
   * @param tree
   * @param responseId
   * @return
   */
  public List<CreateResponsePatternDTO> replaceTree(
      Map<Integer, List<CreateResponsePatternDTO>> tree, Integer responseId) {

    CreateResponsePatternDTO root = tree.get(null).get(0);

    ResponseEntity responseEntity =
        responseRepository
            .findById(responseId)
            .orElseThrow(
                () -> new IllegalArgumentException("Response ID " + responseId + " not found"));

    Map<Integer, Integer> idMap = new HashMap<>(); // oldId to newId
    List<CreateResponsePatternDTO> result = new ArrayList<>();

    dfsReplace(root, tree, responseId, idMap, responseEntity, result);

    return result;
  }

  private void dfsInsert(
      CreateResponsePatternDTO node,
      Map<Integer, List<CreateResponsePatternDTO>> tree,
      Integer responseId,
      Map<Integer, Integer> idMap,
      ResponseEntity responseEntity,
      List<CreateResponsePatternDTO> result) {

    Integer originalId = node.getResponsePatternId();
    Integer parentId = node.getParentId();

    // Resolve real parent ID if it was a temporary negative one
    if (parentId != null && parentId < 0 && idMap.containsKey(parentId)) {
      parentId = idMap.get(parentId);
    }

    // Insert only if ID is negative (not yet in DB)
    if (originalId < 0) {
      ResponsePatternEntity entity = new ResponsePatternEntity();
      entity.setName(node.getName());
      entity.setDescription(node.getDescription());
      entity.setPattern(node.getPattern());
      entity.setIsLeaf(node.getLeaf());
      entity.setParentId(parentId);
      entity.setResponse(responseEntity);

      ResponsePatternEntity saved = responsePatternRepository.save(entity);
      idMap.put(originalId, saved.getResponsePatternId());

      CreateResponsePatternDTO savedDTO = new CreateResponsePatternDTO();
      savedDTO.setResponsePatternId(saved.getResponsePatternId());
      savedDTO.setParentId(saved.getParentId());
      savedDTO.setName(saved.getName());
      savedDTO.setDescription(saved.getDescription());
      savedDTO.setPattern(node.getPattern());
      savedDTO.setLeaf(saved.getIsLeaf());

      result.add(savedDTO);

      originalId = saved.getResponsePatternId();
    }
    // Traverse children
    for (CreateResponsePatternDTO child :
        tree.getOrDefault(node.getResponsePatternId(), Collections.emptyList())) {
      child.setParentId(originalId);
      dfsInsert(child, tree, responseId, idMap, responseEntity, result);
    }
  }

  private void dfsSelectiveUpdate(
      CreateResponsePatternDTO node,
      Map<Integer, List<CreateResponsePatternDTO>> tree,
      Integer responseId,
      Map<Integer, Integer> idMap,
      ResponseEntity responseEntity,
      List<CreateResponsePatternDTO> result,
      Map<Integer, CreateResponsePatternDTO> updatesMap) {
    Integer originalId = node.getResponsePatternId();
    Integer parentId = node.getParentId();

    // Resolve remapped parent if needed
    if (parentId != null && parentId < 0 && idMap.containsKey(parentId)) {
      parentId = idMap.get(parentId);
    }

    ResponsePatternEntity entity;

    if (originalId < 0) {
      // Insert new node
      entity = new ResponsePatternEntity();
      entity.setName(node.getName());
      entity.setDescription(node.getDescription());
      entity.setPattern(node.getPattern());
      entity.setIsLeaf(node.getLeaf());
      entity.setParentId(parentId);
      entity.setResponse(responseEntity);

      entity = responsePatternRepository.save(entity);
      idMap.put(originalId, entity.getResponsePatternId());
    } else if (updatesMap.containsKey(originalId)) {
      // Update only if in the updates list
      entity =
          responsePatternRepository
              .findById(originalId)
              .orElseThrow(
                  () -> new IllegalArgumentException("Pattern ID " + originalId + " not found"));

      entity.setName(node.getName());
      entity.setDescription(node.getDescription());
      entity.setPattern(node.getPattern());
      entity.setIsLeaf(node.getLeaf());
      entity.setParentId(parentId);

      responsePatternRepository.save(entity);
    } else {
      // Node not touched, skip
      return;
    }

    // Collect result
    CreateResponsePatternDTO savedDTO = new CreateResponsePatternDTO();
    savedDTO.setResponsePatternId(entity.getResponsePatternId());
    savedDTO.setParentId(entity.getParentId());
    savedDTO.setName(entity.getName());
    savedDTO.setDescription(entity.getDescription());
    savedDTO.setPattern(entity.getPattern());
    savedDTO.setLeaf(entity.getIsLeaf());
    result.add(savedDTO);

    // Traverse children
    for (CreateResponsePatternDTO child : tree.getOrDefault(originalId, Collections.emptyList())) {
      child.setParentId(entity.getResponsePatternId());
      dfsSelectiveUpdate(child, tree, responseId, idMap, responseEntity, result, updatesMap);
    }
  }

  private void dfsReplace(
      CreateResponsePatternDTO node,
      Map<Integer, List<CreateResponsePatternDTO>> tree,
      Integer responseId,
      Map<Integer, Integer> idMap,
      ResponseEntity responseEntity,
      List<CreateResponsePatternDTO> result) {

    Integer originalId = node.getResponsePatternId();
    Integer parentId = node.getParentId();

    // Resolve real parent ID
    if (parentId != null && idMap.containsKey(parentId)) {
      parentId = idMap.get(parentId);
    }

    // Always insert a new entity
    ResponsePatternEntity entity = new ResponsePatternEntity();
    entity.setName(node.getName());
    entity.setDescription(node.getDescription());
    entity.setPattern(node.getPattern());
    entity.setIsLeaf(node.getLeaf());
    entity.setParentId(parentId);
    entity.setResponse(responseEntity);

    ResponsePatternEntity saved = responsePatternRepository.save(entity);
    idMap.put(originalId, saved.getResponsePatternId());

    CreateResponsePatternDTO savedDTO = new CreateResponsePatternDTO();
    savedDTO.setResponsePatternId(saved.getResponsePatternId());
    savedDTO.setParentId(saved.getParentId());
    savedDTO.setName(saved.getName());
    savedDTO.setDescription(saved.getDescription());
    savedDTO.setPattern(node.getPattern());
    savedDTO.setLeaf(saved.getIsLeaf());

    result.add(savedDTO);

    for (CreateResponsePatternDTO child : tree.getOrDefault(originalId, Collections.emptyList())) {
      child.setParentId(saved.getResponsePatternId());
      dfsReplace(child, tree, responseId, idMap, responseEntity, result);
    }
  }

  public List<CreateResponsePatternDTO> addPatterns(
      Integer responseId, List<CreateResponsePatternDTO> responsePatterns) {
    logger.debug("Entering addPatterns by response id");

    List<ResponsePatternEntity> existing =
        Optional.ofNullable(responsePatternRepository.findByResponse_ResponseId(responseId))
            .orElse(Collections.emptyList());

    responsePatterns.addAll(parseToDTO(existing));
    if (!ResponsePatternTreeValidator.isValid(responsePatterns)) {
      throw new IllegalArgumentException("Invalid response pattern tree");
    }
    Map<Integer, List<CreateResponsePatternDTO>> tree =
        ResponsePatternTreeValidator.healAndGroupByParent(responsePatterns);

    return insertTree(tree, responseId);
  }

  public List<CreateResponsePatternDTO> updatePatterns(
      Integer responseId, List<CreateResponsePatternDTO> updates) {
    logger.debug("Starting partial tree update for response {}", responseId);

    // Fetch full tree from DB and convert to DTOs
    List<ResponsePatternEntity> existing =
        responsePatternRepository.findByResponse_ResponseId(responseId);
    List<CreateResponsePatternDTO> fullTree = parseToDTO(existing);

    // Merge updates into fullTree (replacing same-ID nodes)
    Map<Integer, CreateResponsePatternDTO> updateMap =
        updates.stream()
            .collect(
                Collectors.toMap(
                    CreateResponsePatternDTO::getResponsePatternId, Function.identity()));

    List<CreateResponsePatternDTO> merged = new ArrayList<>();
    for (CreateResponsePatternDTO node : fullTree) {
      if (updateMap.containsKey(node.getResponsePatternId())) {
        merged.add(updateMap.get(node.getResponsePatternId())); // override with update
      } else {
        merged.add(node); // keep existing
      }
    }

    // Add completely new nodes
    updates.stream().filter(dto -> dto.getResponsePatternId() < 0).forEach(merged::add);

    // Validate full merged tree
    if (!ResponsePatternTreeValidator.isValid(merged)) {
      throw new IllegalArgumentException("Invalid tree after merging updates");
    }

    // Group into tree and apply changes
    Map<Integer, List<CreateResponsePatternDTO>> tree =
        ResponsePatternTreeValidator.healAndGroupByParent(merged);
    ResponseEntity responseEntity =
        responseRepository
            .findById(responseId)
            .orElseThrow(
                () -> new IllegalArgumentException("Response ID " + responseId + " not found"));

    Map<Integer, Integer> idMap = new HashMap<>();
    List<CreateResponsePatternDTO> result = new ArrayList<>();

    // Start traversal from root
    CreateResponsePatternDTO root = tree.get(null).get(0);
    dfsSelectiveUpdate(root, tree, responseId, idMap, responseEntity, result, updateMap);

    return result;
  }

  public List<CreateResponsePatternDTO> replacePatterns(
      Integer responseId, List<CreateResponsePatternDTO> responsePatterns) {
    logger.debug("Entering replacePatterns by response id");

    List<ResponsePatternEntity> existing =
        Optional.ofNullable(responsePatternRepository.findByResponse_ResponseId(responseId))
            .orElse(Collections.emptyList());
    responsePatternRepository.deleteAll(existing);

    if (!ResponsePatternTreeValidator.isValid(responsePatterns)) {
      throw new IllegalArgumentException("Invalid response pattern tree");
    }
    Map<Integer, List<CreateResponsePatternDTO>> tree =
        ResponsePatternTreeValidator.healAndGroupByParent(responsePatterns);

    return replaceTree(tree, responseId);
  }
}
