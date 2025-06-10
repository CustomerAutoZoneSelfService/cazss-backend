package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.AuthStrategyEnum;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.HistoryDataTypeEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = CazssBackendApplication.class)
public class HistoryDataRepositoryTest {

  @Autowired private HistoryDataRepository historyDataRepository;

  @Autowired private HistoryRepository historyRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private EndpointsRepository endpointsRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private AuthenticationStrategyRepository authenticationStrategyRepository;

  private HistoryDataEntity createSampleHistoryData() {
    String suffix = String.valueOf(System.currentTimeMillis()); // Generar un sufijo único

    UserEntity user =
        userRepository.save(
            new UserEntity(
                "historydata+" + suffix + "@autozone.com",
                true,
                UserRoleEnum.USER,
                "testPassword123",
                "historyDataUser" + suffix));

    CategoryEntity category =
        categoryRepository.save(new CategoryEntity("HISTORY_DATA_" + suffix, "#FFFFFF"));

    AuthenticationStrategyEntity authStrategy =
        authenticationStrategyRepository.save(
            new AuthenticationStrategyEntity(
                "HistoryDataRepositoryTest_" + suffix, AuthStrategyEnum.Bearer, new ArrayList<>()));

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "History Endpoint " + suffix,
                "Test endpoint for history data",
                EndpointMethodEnum.GET,
                "https://historydata.example",
                authStrategy));

    HistoryEntity history = new HistoryEntity();
    history.setUser(user);
    history.setEndpoint(endpoint);
    history.setStatusCode(200);
    history.setCreatedAt(LocalDateTime.now());
    history = historyRepository.save(history);

    HistoryDataEntity historyData = new HistoryDataEntity();
    historyData.setHistory(history);
    historyData.setType(HistoryDataTypeEnum.REQUEST);
    historyData.setContent("{ \"key\": \"value\" }");

    return historyDataRepository.save(historyData);
  }

  @Transactional
  @Test
  public void givenHistoryDataRepository_whenSaveAndFind_thenOK() {
    HistoryDataEntity saved = createSampleHistoryData();

    Optional<HistoryDataEntity> foundOptional =
        historyDataRepository.findById(saved.getHistoryDataId());
    assertTrue(foundOptional.isPresent(), "HistoryData should be found");

    HistoryDataEntity found = foundOptional.get();
    assertEquals(saved.getType(), found.getType());
    assertEquals(saved.getContent(), found.getContent());
    assertEquals(saved.getHistory().getHistoryId(), found.getHistory().getHistoryId());
  }

  @Transactional
  @Test
  public void givenHistoryDataRepository_whenUpdate_thenOK() {
    HistoryDataEntity historyData = createSampleHistoryData();
    historyData.setType(HistoryDataTypeEnum.RESPONSE);
    historyData.setContent("{ \"response\": \"ok\" }");

    HistoryDataEntity updated = historyDataRepository.save(historyData);

    Optional<HistoryDataEntity> foundOptional =
        historyDataRepository.findById(updated.getHistoryDataId());
    assertTrue(foundOptional.isPresent());

    HistoryDataEntity found = foundOptional.get();
    assertEquals(HistoryDataTypeEnum.RESPONSE, found.getType());
    assertEquals("{ \"response\": \"ok\" }", found.getContent());
  }

  @Transactional
  @Test
  public void givenHistoryDataRepository_whenDelete_thenOK() {
    HistoryDataEntity historyData = createSampleHistoryData();
    Integer id = historyData.getHistoryDataId();

    historyDataRepository.deleteById(id);

    Optional<HistoryDataEntity> foundOptional = historyDataRepository.findById(id);
    assertFalse(foundOptional.isPresent(), "HistoryData should be deleted");
  }
}
