package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class ResponsePatternParser {

  private final Integer ROOT_PARENT_ID = 0;

  /**
   * Groups patterns by parent ID to generate a tree like structure where parentID -> [Entities]
   *
   * @param patterns list of pattern entities to be transformed
   * @return Tree like Map
   */
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

  public ResponsePatternParser() {}

  /**
   * Recursive parsing of content based on a pattern tree
   *
   * @param content String containing the text to be parsed
   * @param patterns List of patterns to be matched against the content
   * @param responsePatternDict Tree like structure where parentID -> [Entities]
   * @param extractedPatternValues Resulting values from pattern matching
   */
  public void parseRecursive(
      String content,
      List<ResponsePatternEntity> patterns,
      Map<Integer, List<ResponsePatternEntity>> responsePatternDict,
      Map<Integer, List<String>> extractedPatternValues) {

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
              .computeIfAbsent(patternItem.getResponsePatternId(), k -> new ArrayList<>())
              .add(innerContent != null ? innerContent.trim() : "");
        }
      }
    }
  }

  /**
   * Parser entry point which evaluates content with a the ResponsePatterns
   *
   * @param patterns List of rules which will be used to evaluate
   * @param inputString content string to evaluate to
   * @return grouped matches by responsePattern id
   */
  public Map<Integer, List<String>> getResponsePatternMatches(
      List<ResponsePatternEntity> patterns, String inputString) {
    Map<Integer, List<String>> extractedPatternValues = new HashMap<>();

    Map<Integer, List<ResponsePatternEntity>> responsePatternDict = populateDict(patterns);

    List<ResponsePatternEntity> rootLevelPatterns = responsePatternDict.get(ROOT_PARENT_ID);
    parseRecursive(inputString, rootLevelPatterns, responsePatternDict, extractedPatternValues);

    return extractedPatternValues;
  }
}
