package com.autozone.cazss_backend.controller;

import com.autozone.cazss_backend.DTO.CreateResponsePatternDTO;
import com.autozone.cazss_backend.service.ResponsePatternService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/responses")
public class ResponsesController {

  @Autowired ResponsePatternService responsePatternService;

  /**
   * POST /responses/{id}/response-patterns/ Creates patterns from scratch, assumes no patterns
   * registered keeps untouched response patterns.
   *
   * @param responseId
   * @return
   */
  @PostMapping("/{id}/response-patterns")
  public ResponseEntity<List<CreateResponsePatternDTO>> postPatterns(
      @PathVariable Integer id, @RequestBody List<CreateResponsePatternDTO> responsePatterns) {
    List<CreateResponsePatternDTO> resultingTree =
        responsePatternService.addPatterns(id, responsePatterns);
    return new ResponseEntity<>(resultingTree, HttpStatus.CREATED);
  }

  /**
   * PATCH /responses/{id}/response-patterns/ Updates all response patterns keeps untouched response
   * patterns.
   *
   * @param id
   * @return
   */
  @PatchMapping("/{id}/response-patterns")
  public ResponseEntity<List<CreateResponsePatternDTO>> patchPatterns(
      @PathVariable Integer id, @RequestBody List<CreateResponsePatternDTO> responsePatterns) {
    List<CreateResponsePatternDTO> requestResponseDTO =
        responsePatternService.updatePatterns(id, responsePatterns);
    return new ResponseEntity<>(requestResponseDTO, HttpStatus.OK);
  }

  /**
   * /responses/{id}/response-patterns/ Updates all response patterns but assumes untouched response
   * patterns have to be deleted.
   *
   * @param id
   * @return
   */
  @PutMapping("/{id}/response-patterns")
  public ResponseEntity<List<CreateResponsePatternDTO>> putPatterns(
      @PathVariable Integer id, @RequestBody List<CreateResponsePatternDTO> responsePatterns) {
    List<CreateResponsePatternDTO> requestResponseDTO =
        responsePatternService.replacePatterns(id, responsePatterns);
    return new ResponseEntity<>(requestResponseDTO, HttpStatus.OK);
  }
}
