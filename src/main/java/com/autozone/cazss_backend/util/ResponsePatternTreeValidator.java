package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.DTO.CreateResponsePatternDTO;
import com.autozone.cazss_backend.exceptions.ResponsePatternTreeValidatorExceptions.*;
import com.autozone.cazss_backend.service.ResponsePatternService;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponsePatternTreeValidator {

  private static final int NUMBER_OF_ROOTS = 1;
  private static final Logger logger = LoggerFactory.getLogger(ResponsePatternService.class);

  /**
   * Validates that a list of CreateResponsePatternDTO objects form a valid tree structure.
   *
   * <p>The tree must meet the following conditions: - All node IDs are unique - Exactly one root
   * node exists (node with parentId == null) - The tree contains no cycles
   *
   * @param responsePatterns the list of response pattern DTOs to validate
   * @return true if the input list forms a valid tree
   * @throws DuplicateResponsePatternIdException if duplicate IDs are found
   * @throws InvalidRootNodeException if there is not exactly one root node
   * @throws CycleDetectedException if a cycle is detected in the tree
   */
  public static boolean isValid(List<CreateResponsePatternDTO> responsePatterns) {
    Map<Integer, CreateResponsePatternDTO> idMap = new HashMap<>();
    Map<Integer, List<CreateResponsePatternDTO>> tree = new HashMap<>();

    if (responsePatterns.isEmpty()) { // Allow no response patterns at all
      return true;
    }

    // Populate maps and check for duplicate IDs
    for (CreateResponsePatternDTO dto : responsePatterns) {
      Integer id = dto.getResponsePatternId();
      if (idMap.containsKey(id)) {
        logger.error("Duplicate ID found: {}", id);
        throw new DuplicateResponsePatternIdException(id);
      }
      idMap.put(id, dto);
      tree.computeIfAbsent(dto.getParentId(), k -> new ArrayList<>()).add(dto);
    }

    // Check root existence
    List<CreateResponsePatternDTO> rootNodes = tree.get(null);
    if (rootNodes == null || rootNodes.size() != NUMBER_OF_ROOTS) {
      logger.error("Invalid number of roots found: {}", rootNodes == null ? 0 : rootNodes.size());
      int size = null == rootNodes ? 0 : rootNodes.size();
      throw new InvalidRootNodeException(size);
    }

    // Check for cycles
    Set<Integer> visited = new HashSet<>();
    if (hasCycle(rootNodes.get(0), tree, visited)) {
      logger.error("Cycle detected in response pattern tree");
      throw new CycleDetectedException();
    }

    return true;
  }

  /**
   * Builds a tree structure grouping CreateResponsePatternDTO nodes by their parentId, and updates
   * each node's isLeaf value to match the actual hierarchy.
   *
   * <p>This method is useful when isLeaf values might be outdated or incorrect.
   *
   * @param dtos the list of response pattern DTOs
   * @return a map of parent ID to list of child DTOs
   * @throws IllegalArgumentException if the number of root nodes is not exactly one
   */
  public static Map<Integer, List<CreateResponsePatternDTO>> healAndGroupByParent(
      List<CreateResponsePatternDTO> dtos) {

    if (dtos.isEmpty()) {
      return new HashMap<>();
    }

    // Build tree map once and validate root
    Map<Integer, List<CreateResponsePatternDTO>> tree = new HashMap<>();
    for (CreateResponsePatternDTO dto : dtos) {
      tree.computeIfAbsent(dto.getParentId(), k -> new ArrayList<>()).add(dto);
    }

    List<CreateResponsePatternDTO> rootNodes = tree.get(null);
    if (rootNodes == null || rootNodes.size() != NUMBER_OF_ROOTS) {
      throw new IllegalArgumentException("Invalid number of root nodes");
    }

    // Heal isLeaf values
    dfsSetIsLeaf(rootNodes.get(0), tree);

    return tree;
  }

  /**
   * Recursively checks for cycles in the tree using depth-first traversal.
   *
   * @param node the current node being visited
   * @param tree the tree structure grouped by parent ID
   * @param visited a set of node IDs already visited during traversal
   * @return true if a cycle is found, false otherwise
   */
  private static boolean hasCycle(
      CreateResponsePatternDTO node,
      Map<Integer, List<CreateResponsePatternDTO>> tree,
      Set<Integer> visited) {
    int id = node.getResponsePatternId();
    if (!visited.add(id)) {
      return true; // cycle detected
    }
    for (CreateResponsePatternDTO child : tree.getOrDefault(id, List.of())) {
      if (hasCycle(child, tree, visited)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Recursively updates the isLeaf value of each node based on whether it has children.
   *
   * @param node the current node being visited
   * @param tree the tree structure grouped by parent ID
   */
  private static void dfsSetIsLeaf(
      CreateResponsePatternDTO node, Map<Integer, List<CreateResponsePatternDTO>> tree) {
    int id = node.getResponsePatternId();
    List<CreateResponsePatternDTO> children = tree.getOrDefault(id, List.of());

    node.setIsLeaf(children.isEmpty());

    for (CreateResponsePatternDTO child : children) {
      dfsSetIsLeaf(child, tree);
    }
  }
}
