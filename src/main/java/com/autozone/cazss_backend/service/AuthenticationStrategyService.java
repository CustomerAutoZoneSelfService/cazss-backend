package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.DTO.AuthenticationStrategyAttributeDTO;
import com.autozone.cazss_backend.DTO.AuthenticationStrategyDTO;
import com.autozone.cazss_backend.entity.AuthenticationStrategyAttributeEntity;
import com.autozone.cazss_backend.entity.AuthenticationStrategyEntity;
import com.autozone.cazss_backend.enumerator.AuthStrategyEnum;
import com.autozone.cazss_backend.exceptions.CustomException;
import com.autozone.cazss_backend.repository.AuthenticationStrategyRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationStrategyService {

  @Autowired private AuthenticationStrategyRepository authStrategyRepo;

  private boolean isAdminOrConfigurator() {
    // Hardocoded check for admin or configurator role.
    return true;
  }

  public List<AuthenticationStrategyDTO> getAllStrategies() {
    return authStrategyRepo.findAll().stream()
        .map(entity -> toDTO(entity, isAdminOrConfigurator()))
        .collect(Collectors.toList());
  }

  public Optional<AuthenticationStrategyDTO> getStrategyById(Integer id) {
    return authStrategyRepo.findById(id).map(entity -> toDTO(entity, isAdminOrConfigurator()));
  }

  public AuthenticationStrategyDTO createStrategy(AuthenticationStrategyDTO dto) {
    if (!isAdminOrConfigurator()) {
      throw new CustomException(
          "Only admin or configurator users can modify authentication strategies.");
    }

    AuthenticationStrategyEntity entity = new AuthenticationStrategyEntity();
    entity.setName(dto.getName());
    entity.setStrategy(AuthStrategyEnum.valueOf(dto.getType()));

    if (dto.getAttributes() != null) {
      for (AuthenticationStrategyAttributeDTO attrDto : dto.getAttributes()) {
        AuthenticationStrategyAttributeEntity attr = new AuthenticationStrategyAttributeEntity();
        attr.setKeyName(attrDto.getKey());
        attr.setValue(attrDto.getValue());
        entity.addAttribute(attr);
      }
    }

    AuthenticationStrategyEntity saved = authStrategyRepo.save(entity);
    return toDTO(saved, true);
  }

  public Optional<AuthenticationStrategyDTO> updateStrategy(
      Integer id, AuthenticationStrategyDTO dto) {
    if (!isAdminOrConfigurator()) {
      throw new CustomException(
          "Only admin or configurator users can modify authentication strategies.");
    }

    Optional<AuthenticationStrategyEntity> entityOpt = authStrategyRepo.findById(id);
    if (entityOpt.isEmpty()) return Optional.empty();

    AuthenticationStrategyEntity entity = entityOpt.get();
    entity.setName(dto.getName());
    entity.setStrategy(AuthStrategyEnum.valueOf(dto.getType()));
    entity.clearAttributes();

    if (dto.getAttributes() != null) {
      for (AuthenticationStrategyAttributeDTO attrDto : dto.getAttributes()) {
        AuthenticationStrategyAttributeEntity attr = new AuthenticationStrategyAttributeEntity();
        attr.setKeyName(attrDto.getKey());
        attr.setValue(attrDto.getValue());
        entity.addAttribute(attr);
      }
    }

    AuthenticationStrategyEntity saved = authStrategyRepo.save(entity);
    return Optional.of(toDTO(saved, true));
  }

  private AuthenticationStrategyDTO toDTO(
      AuthenticationStrategyEntity entity, boolean includeAttributes) {
    List<AuthenticationStrategyAttributeDTO> attrDtos =
        includeAttributes
            ? entity.getAttributes().stream()
                .map(
                    attr ->
                        new AuthenticationStrategyAttributeDTO(
                            attr.getAuthStrategyAttributeId(), attr.getKeyName(), attr.getValue()))
                .collect(Collectors.toList())
            : null;

    return new AuthenticationStrategyDTO(
        entity.getAuthStrategyId(), entity.getName(), entity.getStrategy().name(), attrDtos);
  }
}
