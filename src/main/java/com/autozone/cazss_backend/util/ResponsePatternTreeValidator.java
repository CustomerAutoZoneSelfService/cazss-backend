package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.DTO.CreateResponsePatternDTO;
import com.autozone.cazss_backend.service.ResponsePatternService;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponsePatternTreeValidator {

  private static final int NUMBER_OF_ROOTS = 1;
  private static final Logger logger = LoggerFactory.getLogger(ResponsePatternService.class);

  public static boolean isValid(List<CreateResponsePatternDTO> responsePatterns) {
    Map<Integer, CreateResponsePatternDTO> idMap = new HashMap<>();
    Map<Integer, List<CreateResponsePatternDTO>> tree = new HashMap<>();

    // Populate maps and check for duplicate IDs
    for (CreateResponsePatternDTO dto : responsePatterns) {
      Integer id = dto.getResponsePatternId();
      if (idMap.containsKey(id)) {
        logger.error("Duplicate ID found: {}", id);
        throw new IllegalArgumentException("Duplicate ID: " + id);
      }
      idMap.put(id, dto);
      tree.computeIfAbsent(dto.getParentId(), k -> new ArrayList<>()).add(dto);
    }

    // Check root existence
    List<CreateResponsePatternDTO> rootNodes = tree.get(null);
    if (rootNodes == null || rootNodes.size() != NUMBER_OF_ROOTS) {
      logger.error(
          "Expected exactly one root node, found: {}", rootNodes == null ? 0 : rootNodes.size());
      throw new IllegalArgumentException("Invalid number of root nodes");
    }

    // Check for cycles
    Set<Integer> visited = new HashSet<>();
    if (hasCycle(rootNodes.get(0), tree, visited)) {
      logger.error("Cycle detected in response pattern tree");
      throw new IllegalArgumentException("Cycle detected in tree");
    }

    return true;
  }

  public static Map<Integer, List<CreateResponsePatternDTO>> healAndGroupByParent(
      List<CreateResponsePatternDTO> dtos) {

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
