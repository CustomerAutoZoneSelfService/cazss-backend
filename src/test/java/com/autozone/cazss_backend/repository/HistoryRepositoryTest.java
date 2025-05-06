package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
public class HistoryRepositoryTest {

  @Autowired private HistoryRepository historyRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private EndpointsRepository endpointsRepository;

  private HistoryEntity createSampleHistory() {
    String suffix = String.valueOf(System.currentTimeMillis());

    UserEntity user =
        userRepository.save(
            new UserEntity("history+" + suffix + "@autozone.com", true, UserRoleEnum.USER));

    CategoryEntity category =
        categoryRepository.save(new CategoryEntity("HISTORY_CAT_" + suffix, "#CCCCCC"));

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "History Endpoint " + suffix,
                "Description",
                EndpointMethodEnum.POST,
                "https://test.history.endpoint"));

    HistoryEntity history = new HistoryEntity();
    history.setUser(user);
    history.setEndpoint(endpoint);
    history.setStatusCode(200);
    history.setCreatedAt(LocalDateTime.now());

    return historyRepository.save(history);
  }

  @Test
  public void givenHistoryRepository_whenSaveAndFind_thenOK() {
    HistoryEntity savedHistory = createSampleHistory();

    Optional<HistoryEntity> foundHistoryOptional =
        historyRepository.findById(savedHistory.getHistoryId());
    assertTrue(foundHistoryOptional.isPresent(), "History should be found");

    HistoryEntity foundHistory = foundHistoryOptional.get();
    assertEquals(savedHistory.getUser().getEmail(), foundHistory.getUser().getEmail());
    assertEquals(savedHistory.getEndpoint().getName(), foundHistory.getEndpoint().getName());
    assertEquals(200, foundHistory.getStatusCode());
  }

  @Test
  public void givenHistoryRepository_whenUpdate_thenOK() {
    HistoryEntity history = createSampleHistory();
    history.setStatusCode(404);

    HistoryEntity updatedHistory = historyRepository.save(history);

    Optional<HistoryEntity> foundOptional =
        historyRepository.findById(updatedHistory.getHistoryId());
    assertTrue(foundOptional.isPresent());

    HistoryEntity found = foundOptional.get();
    assertEquals(404, found.getStatusCode());
  }

  @Test
  public void givenHistoryRepository_whenDelete_thenOK() {
    HistoryEntity history = createSampleHistory();
    Integer id = history.getHistoryId();

    historyRepository.deleteById(id);

    Optional<HistoryEntity> foundOptional = historyRepository.findById(id);
    assertFalse(foundOptional.isPresent(), "History should be deleted");
  }
}
