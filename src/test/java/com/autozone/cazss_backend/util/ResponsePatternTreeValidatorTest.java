package com.autozone.cazss_backend.util;

import static org.junit.Assert.*;

import com.autozone.cazss_backend.DTO.CreateResponsePatternDTO;
import com.autozone.cazss_backend.exceptions.ResponsePatternTreeValidatorExceptions.*;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class ResponsePatternTreeValidatorTest {

  private static CreateResponsePatternDTO dto(Integer id, Integer parentId, boolean isLeaf) {
    CreateResponsePatternDTO dto = new CreateResponsePatternDTO();
    dto.setResponsePatternId(id);
    dto.setParentId(parentId);
    dto.setName("Node " + id);
    dto.setDescription("Description " + id);
    dto.setPattern("Pattern " + id);
    dto.setIsLeaf(isLeaf);
    return dto;
  }

  @Test
  public void testIsValid_validTree_shouldReturnTrue() {
    List<CreateResponsePatternDTO> dtos =
        List.of(dto(1, null, false), dto(2, 1, true), dto(3, 1, true));

    assertTrue(ResponsePatternTreeValidator.isValid(dtos));
  }

  @Test(expected = DuplicateResponsePatternIdException.class)
  public void testIsValid_duplicateId_shouldThrowException() {
    List<CreateResponsePatternDTO> dtos =
        List.of(
            dto(1, null, false), dto(1, 1, true) // Duplicate ID
            );

    ResponsePatternTreeValidator.isValid(dtos);
  }

  @Test(expected = InvalidRootNodeException.class)
  public void testIsValid_multipleRoots_shouldThrowException() {
    List<CreateResponsePatternDTO> dtos =
        List.of(
            dto(1, 1, false), dto(2, 1, false) // Second root
            );

    ResponsePatternTreeValidator.isValid(dtos);
  }

  @Test
  public void testHealAndGroupByParent_shouldSetIsLeafCorrectly() {
    List<CreateResponsePatternDTO> dtos =
        List.of(dto(1, null, false), dto(2, 1, true), dto(3, 1, true));

    Map<Integer, List<CreateResponsePatternDTO>> tree =
        ResponsePatternTreeValidator.healAndGroupByParent(dtos);

    // Node 1 has children → not leaf
    assertFalse(dtos.get(0).getIsLeaf());

    // Nodes 2 and 3 are leaves
    assertTrue(dtos.get(1).getIsLeaf());
    assertTrue(dtos.get(2).getIsLeaf());

    // Tree map validation
    assertEquals(2, tree.get(1).size());
    assertEquals(1, tree.get(null).size()); // One root
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHealAndGroupByParent_multipleRoots_shouldThrowException() {
    List<CreateResponsePatternDTO> dtos = List.of(dto(1, null, true), dto(2, null, true));

    ResponsePatternTreeValidator.healAndGroupByParent(dtos);
  }
}
