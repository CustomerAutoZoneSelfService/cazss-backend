package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.CreateResponsePatternDTO;
import com.autozone.cazss_backend.entity.ResponseEntity;
import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import com.autozone.cazss_backend.repository.ResponsePatternRepository;
import com.autozone.cazss_backend.repository.ResponseRepository;
import com.autozone.cazss_backend.repository.UserFilterRepository;
import com.autozone.cazss_backend.util.ResponsePatternParser;
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
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResponsePatternService {

  private final ResponsePatternRepository responsePatternRepository;

  private final ResponseRepository responseRepository;

  private final ResponsePatternParser regexParser;

  private final UserFilterRepository userFilterRepository;

  private static final Logger logger = LoggerFactory.getLogger(ResponsePatternService.class);

  public ResponsePatternService(
      ResponsePatternRepository responsePatternRepository,
      ResponsePatternParser regexParser,
      ResponseRepository responseRepository,
      UserFilterRepository userFilterRepository) {
    this.responsePatternRepository = responsePatternRepository;
    this.regexParser = regexParser;
    this.responseRepository = responseRepository;
    this.userFilterRepository = userFilterRepository;
  }

  /**
   * Retrieves the response patterns for a given response ID and parses the input string to find
   * matches.
   *
   * @param responseId The ID of the response for which to retrieve response patterns.
   * @param inputString The input string to parse for matches against the response patterns.
   * @return A map where the keys are pattern names and the values are lists of matched strings.
   */
  public Map<Integer, List<String>> getMatchesForEndpoint(Integer responseId, String inputString) {
    List<ResponsePatternEntity> patterns =
        responsePatternRepository.findByResponse_ResponseId(responseId);

    if (patterns == null || patterns.isEmpty()) {
      Map<Integer, List<String>> fallback = new HashMap<>();
      fallback.put(-1, List.of(inputString));
      return fallback;
    }

    return regexParser.getResponsePatternMatches(patterns, inputString);
  }

  /**
   * Converts a list of ResponsePatternEntity instances into a list of CreateResponsePatternDTO
   * objects, preserving relevant pattern metadata.
   *
   * @param entitiesList The list of response pattern entities retrieved from the database.
   * @return A list of DTOs representing the same information as the original entities.
   */
  public List<CreateResponsePatternDTO> parseToDTO(List<ResponsePatternEntity> entitiesList) {
    List<CreateResponsePatternDTO> dtoList = new ArrayList<>();

    for (ResponsePatternEntity entity : entitiesList) {
      CreateResponsePatternDTO dto = new CreateResponsePatternDTO();
      dto.setResponsePatternId(entity.getResponsePatternId());
      dto.setParentId(entity.getParentId());
      dto.setName(entity.getName());
      dto.setDescription(entity.getDescription());
      dto.setIsLeaf(entity.getIsLeaf());
      dtoList.add(dto);
    }

    return dtoList;
  }

  /**
   * Inserts a new tree of response patterns into the database, linking them to the specified
   * response. Existing patterns are not modified. This assumes the new patterns are correctly
   * structured as a tree and not yet persisted (indicated by negative IDs).
   *
   * @param tree A tree structure where each node is grouped by its parent ID.
   * @param responseId The ID of the response to which the new patterns will be linked.
   * @return A list of DTOs representing the inserted nodes with updated database-generated IDs.
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
   * Replaces an existing pattern tree in the database with a new tree. All provided nodes are
   * inserted as new entries and existing nodes are removed. It assumes the tree structure is valid
   * and the database can be cleaned prior to insertion.
   *
   * @param tree A tree structure of new patterns grouped by parent ID.
   * @param responseId The ID of the response to which the tree belongs.
   * @return A list of DTOs reflecting the newly inserted structure with updated IDs.
   */
  public List<CreateResponsePatternDTO> replaceTree(
      Map<Integer, List<CreateResponsePatternDTO>> tree, Integer responseId) {

    if (tree.isEmpty()) {
      return new ArrayList<>();
    }

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

  /**
   * Traverses and inserts a pattern tree recursively. New nodes are inserted into the database only
   * if their ID is negative (indicating they are new). Parent-child relationships are maintained
   * using an ID remapping structure.
   *
   * @param node The current node to insert.
   * @param tree The grouped tree structure.
   * @param responseId The response to which this tree belongs.
   * @param idMap A map to track remapped (new) IDs of nodes.
   * @param responseEntity The actual response entity from the database.
   * @param result A list accumulating all inserted DTOs.
   */
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
      entity.setIsLeaf(node.getIsLeaf());
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
      savedDTO.setIsLeaf(saved.getIsLeaf());

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

  /**
   * Traverses and selectively updates or inserts nodes in the pattern tree. Existing nodes are
   * updated only if present in the update map, and new nodes (with negative IDs) are inserted.
   *
   * @param node The root node to start traversal.
   * @param tree The grouped structure of pattern nodes.
   * @param responseId The response to which the nodes are linked.
   * @param idMap A mapping between old (possibly temporary) IDs and new database-generated IDs.
   * @param responseEntity The associated response entity.
   * @param result A list to collect all updated/inserted DTOs.
   * @param updatesMap A map containing only the nodes that need to be updated.
   */
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
      entity.setIsLeaf(node.getIsLeaf());
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
      entity.setIsLeaf(node.getIsLeaf());
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
    savedDTO.setIsLeaf(entity.getIsLeaf());
    result.add(savedDTO);

    // Traverse children
    for (CreateResponsePatternDTO child : tree.getOrDefault(originalId, Collections.emptyList())) {
      child.setParentId(entity.getResponsePatternId());
      dfsSelectiveUpdate(child, tree, responseId, idMap, responseEntity, result, updatesMap);
    }
  }

  /**
   * Traverses and replaces the entire response pattern tree structure by inserting new entries for
   * each node, regardless of whether it existed previously. This method assumes that the database
   * has been cleared of previous nodes.
   *
   * @param node The node to process and insert.
   * @param tree The full tree grouped by parent ID.
   * @param responseId The response entity identifier.
   * @param idMap A remapping structure from temporary to actual database IDs.
   * @param responseEntity The response entity to which the tree is linked.
   * @param result A list of DTOs containing the newly inserted nodes.
   */
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
    entity.setIsLeaf(node.getIsLeaf());
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
    savedDTO.setIsLeaf(saved.getIsLeaf());

    result.add(savedDTO);

    for (CreateResponsePatternDTO child : tree.getOrDefault(originalId, Collections.emptyList())) {
      child.setParentId(saved.getResponsePatternId());
      dfsReplace(child, tree, responseId, idMap, responseEntity, result);
    }
  }

  /**
   * Adds a new tree of response patterns to a given response. The tree may include existing nodes,
   * and it will be validated against the existing data. Only new nodes (with negative IDs) are
   * inserted.
   *
   * @param responseId The response ID to associate the new patterns with.
   * @param responsePatterns The list of response pattern nodes including the new subtree.
   * @return The inserted subtree with newly assigned IDs.
   */
  public List<CreateResponsePatternDTO> addPatterns(
      Integer responseId, List<CreateResponsePatternDTO> responsePatterns) {
    logger.debug("Entering addPatterns by response id");

    if (responsePatterns.isEmpty()) {
      return new ArrayList<CreateResponsePatternDTO>();
    }

    List<ResponsePatternEntity> existing =
        Optional.ofNullable(responsePatternRepository.findByResponse_ResponseId(responseId))
            .orElse(Collections.emptyList());

    responsePatterns.addAll(parseToDTO(existing));

    logger.debug("{}", responsePatterns);
    if (!ResponsePatternTreeValidator.isValid(responsePatterns)) {
      throw new IllegalArgumentException("Invalid response pattern tree");
    }
    Map<Integer, List<CreateResponsePatternDTO>> tree =
        ResponsePatternTreeValidator.healAndGroupByParent(responsePatterns);

    return insertTree(tree, responseId);
  }

  /**
   * Updates a subset of response pattern nodes while preserving the existing tree structure. Only
   * nodes present in the update list are modified; all others are left unchanged. New nodes (with
   * negative IDs) are inserted and integrated.
   *
   * @param responseId The ID of the response to which the pattern tree belongs.
   * @param responsePatterns The list of nodes to be inserted or updated.
   * @return A list of all nodes that were inserted or updated, with corrected IDs.
   */
  public List<CreateResponsePatternDTO> updatePatterns(
      Integer responseId, List<CreateResponsePatternDTO> responsePatterns) {
    logger.debug("Starting partial tree update for response {}", responseId);

    if (responsePatterns.isEmpty()) {
      return new ArrayList<CreateResponsePatternDTO>();
    }

    // Fetch full tree from DB and convert to DTOs
    List<ResponsePatternEntity> existing =
        responsePatternRepository.findByResponse_ResponseId(responseId);
    List<CreateResponsePatternDTO> fullTree = parseToDTO(existing);

    // Merge updates into fullTree (replacing same-ID nodes)
    Map<Integer, CreateResponsePatternDTO> updateMap =
        responsePatterns.stream()
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
    responsePatterns.stream().filter(dto -> dto.getResponsePatternId() < 0).forEach(merged::add);

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

  /**
   * Replaces the entire set of response patterns for a response with a new tree structure. All
   * existing nodes are deleted before inserting the new ones.
   *
   * @param responseId The ID of the response whose tree should be replaced.
   * @param responsePatterns The new tree structure to insert.
   * @return A list of DTOs representing the newly inserted response pattern tree.
   */
  @Transactional
  public List<CreateResponsePatternDTO> replacePatterns(
      Integer responseId, List<CreateResponsePatternDTO> responsePatterns) {
    logger.debug("Entering replacePatterns by response id");

    userFilterRepository.deleteByResponsePattern_Response_ResponseId(responseId);
    responsePatternRepository.deleteByResponse_ResponseId(responseId);

    if (!ResponsePatternTreeValidator.isValid(responsePatterns)) {
      throw new IllegalArgumentException("Invalid response pattern tree");
    }
    Map<Integer, List<CreateResponsePatternDTO>> tree =
        ResponsePatternTreeValidator.healAndGroupByParent(responsePatterns);

    return replaceTree(tree, responseId);
  }
}
