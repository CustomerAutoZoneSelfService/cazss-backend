package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class RegexParser {

  private final Integer ROOT_PARENT_ID = 0;

  // /**
  // * Returns the smallest responsePatternId from the list
  // */
  // public int getMinId(List<ResponsePatternEntity> allPatterns) {
  // return allPatterns.stream()
  // .filter(p -> p.getResponsePatternId() != null)
  // .mapToInt(ResponsePatternEntity::getResponsePatternId)
  // .min()
  // .orElse(1); // Avoid division by 0
  // }

  /** Groups patterns by normalized parent ID. */
  public Map<Integer, List<ResponsePatternEntity>> populateDict(
      List<ResponsePatternEntity> patterns) {
    Map<Integer, List<ResponsePatternEntity>> dict = new HashMap<>();

    for (ResponsePatternEntity pattern : patterns) {
      Integer parentId = pattern.getParentId();
      Integer key = (parentId == null) ? ROOT_PARENT_ID : parentId;

      dict.computeIfAbsent(key, k -> new ArrayList<>()).add(pattern);
    }

    return dict;
  }

  public RegexParser() {}

  /** Recursive parsing of XML content based on pattern tree. */
  public void parseRecursive(
      String content,
      List<ResponsePatternEntity> patterns,
      Map<Integer, List<ResponsePatternEntity>> responsePatternDict,
      Map<String, List<String>> extractedPatternValues) {

    if (patterns == null) return;

    for (ResponsePatternEntity patternItem : patterns) {
      Pattern regex = Pattern.compile(patternItem.getPattern(), Pattern.DOTALL);
      Matcher matcher = regex.matcher(content);

      while (matcher.find()) {
        String innerContent;
        try {
          innerContent = matcher.group(1);
        } catch (IndexOutOfBoundsException e) {
          innerContent = null;
        }

        if (!patternItem.getIsLeaf()) {
          if (innerContent != null) {
            List<ResponsePatternEntity> children =
                responsePatternDict.get(patternItem.getResponsePatternId());
            parseRecursive(innerContent, children, responsePatternDict, extractedPatternValues);
          }
        } else {
          extractedPatternValues
              .computeIfAbsent(patternItem.getName(), k -> new ArrayList<>())
              .add(innerContent != null ? innerContent.trim() : "");
        }
      }
    }
  }

  /** Entrypoint: returns a map of extracted values by tag name. */
  public Map<String, List<String>> getResponsePatternMatches(
      List<ResponsePatternEntity> patterns, String inputString) {
    Map<String, List<String>> extractedPatternValues = new HashMap<>();

    Map<Integer, List<ResponsePatternEntity>> responsePatternDict = populateDict(patterns);

    List<ResponsePatternEntity> rootLevelPatterns = responsePatternDict.get(ROOT_PARENT_ID);
    parseRecursive(inputString, rootLevelPatterns, responsePatternDict, extractedPatternValues);

    return extractedPatternValues;
  }
}
