package com.autozone.cazss_backend.util;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegexParserTest {

  private RegexParser regexParser;

  @BeforeEach
  void setUp() {
    regexParser = new RegexParser();
  }

  private ResponsePatternEntity patternEntity(
      Integer id, Integer parentId, String pattern, String name, boolean isLeaf) {
    ResponsePatternEntity entity = new ResponsePatternEntity();
    entity.setResponsePatternId(id);
    entity.setParentId(parentId);
    entity.setPattern(pattern);
    entity.setName(name);
    entity.setIsLeaf(isLeaf);
    entity.setDescription("test");
    return entity;
  }

  @Test
  void testPopulateDictGroupsByParentId() {
    List<ResponsePatternEntity> patterns =
        List.of(
            patternEntity(1, null, "<root>(.*?)</root>", "root", false),
            patternEntity(2, 1, "<child>(.*?)</child>", "child", true));

    Map<Integer, List<ResponsePatternEntity>> dict = regexParser.populateDict(patterns);

    assertEquals(2, dict.size());
    assertTrue(dict.containsKey(0)); // root
    assertTrue(dict.containsKey(1)); // child under root
    assertEquals(1, dict.get(0).size());
    assertEquals(1, dict.get(1).size());
  }

  @Test
  void testParseRecursiveSimpleExtraction() {
    String input = "<root><child>value1</child><child>value2</child></root>";

    ResponsePatternEntity root = patternEntity(1, null, "<root>(.*?)</root>", "root", false);
    ResponsePatternEntity child = patternEntity(2, 1, "<child>(.*?)</child>", "child", true);

    List<ResponsePatternEntity> patterns = List.of(root, child);
    Map<Integer, List<ResponsePatternEntity>> dict = regexParser.populateDict(patterns);
    Map<String, List<String>> extracted = new HashMap<>();

    regexParser.parseRecursive(input, dict.get(0), dict, extracted);

    assertTrue(extracted.containsKey("child"));
    assertEquals(List.of("value1", "value2"), extracted.get("child"));
  }

  @Test
  void testGetResponsePatternMatchesFullIntegration() {
    String input = "<root><child>value</child></root>";

    ResponsePatternEntity root = patternEntity(1, null, "<root>(.*?)</root>", "root", false);
    ResponsePatternEntity child = patternEntity(2, 1, "<child>(.*?)</child>", "child", true);

    List<ResponsePatternEntity> patterns = List.of(root, child);

    Map<String, List<String>> result = regexParser.getResponsePatternMatches(patterns, input);

    assertTrue(result.containsKey("child"));
    assertEquals(List.of("value"), result.get("child"));
  }

  @Test
  void testNoMatchYieldsEmptyResult() {
    String input = "<invalid>test</invalid>";

    ResponsePatternEntity root = patternEntity(1, null, "<root>(.*?)</root>", "root", false);
    ResponsePatternEntity child = patternEntity(2, 1, "<child>(.*?)</child>", "child", true);

    List<ResponsePatternEntity> patterns = List.of(root, child);

    Map<String, List<String>> result = regexParser.getResponsePatternMatches(patterns, input);
    assertTrue(result.isEmpty());
  }

  @Test
  void testLeafPatternWithoutCapturingGroup() {
    String input = "<root><child>value</child></root>";

    ResponsePatternEntity root = patternEntity(1, null, "<root>(.*?)</root>", "root", false);
    // No capturing group: matcher.group(1) will fail
    ResponsePatternEntity child = patternEntity(2, 1, "<child>value</child>", "child", true);

    List<ResponsePatternEntity> patterns = List.of(root, child);

    Map<String, List<String>> result = regexParser.getResponsePatternMatches(patterns, input);
    assertTrue(result.containsKey("child"));
    assertEquals(List.of(""), result.get("child"));
  }

  @Test
  void testMultipleRootLevelPatterns() {
    String input = "<root1><data>abc</data></root1><root2><data>123</data></root2>";

    ResponsePatternEntity root1 = patternEntity(1, null, "<root1>(.*?)</root1>", "root1", false);
    ResponsePatternEntity root2 = patternEntity(2, null, "<root2>(.*?)</root2>", "root2", false);
    ResponsePatternEntity data = patternEntity(3, 1, "<data>(.*?)</data>", "data1", true);
    ResponsePatternEntity data2 = patternEntity(4, 2, "<data>(.*?)</data>", "data2", true);

    List<ResponsePatternEntity> patterns = List.of(root1, root2, data, data2);
    Map<String, List<String>> result = regexParser.getResponsePatternMatches(patterns, input);

    assertEquals(List.of("abc"), result.get("data1"));
    assertEquals(List.of("123"), result.get("data2"));
  }
}
