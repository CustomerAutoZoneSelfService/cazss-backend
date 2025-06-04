package com.autozone.cazss_backend.exceptions;

public class ResponsePatternTreeValidatorExceptions {

  public static class DuplicateResponsePatternIdException extends RuntimeException {
    public DuplicateResponsePatternIdException(Integer id) {
      super("Duplicate ResponsePattern ID found: " + id);
    }
  }

  public static class InvalidRootNodeException extends RuntimeException {
    public InvalidRootNodeException(int foundRoots) {
      super("Expected exactly one root node, but found: " + foundRoots);
    }
  }

  public static class CycleDetectedException extends RuntimeException {
    public CycleDetectedException() {
      super("Cycle detected in the ResponsePattern tree");
    }
  }
}
