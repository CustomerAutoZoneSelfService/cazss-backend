package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.entity.ResponsePatternEntity;
import com.autozone.cazss_backend.repository.ResponsePatternRepository;
import com.autozone.cazss_backend.util.RegexParser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ResponsePatternService {

  private final ResponsePatternRepository responsePatternRepository;

  private final RegexParser regexParser;

  public ResponsePatternService(
      ResponsePatternRepository responsePatternRepository, RegexParser regexParser) {
    this.responsePatternRepository = responsePatternRepository;
    this.regexParser = regexParser;
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
}
