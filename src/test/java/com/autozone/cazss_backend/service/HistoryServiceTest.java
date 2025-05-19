package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.autozone.cazss_backend.DTO.HistoryDTO;
import com.autozone.cazss_backend.projections.HistoryProjection;
import com.autozone.cazss_backend.repository.HistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
public class HistoryServiceTest {

  @Mock private HistoryRepository historyRepository;

  @InjectMocks private HistoryService historyService;

  @Test
  void getAllHistory() {
    // GIVEN
    HistoryProjection history1 =
        new HistoryProjection() {
          @Override
          public Integer getHistoryId() {
            return 1;
          }

          @Override
          public String getEmail() {
            return "TEST1@autozone.com";
          }

          @Override
          public String getEndpointName() {
            return "TEST NAME 1";
          }

          @Override
          public String getEndpointDescription() {
            return "TEST DESC 1";
          }

          @Override
          public LocalDateTime getCreatedAt() {
            return LocalDateTime.of(2025, 1, 1, 0, 0, 0);
          }
        };

    HistoryProjection history2 =
        new HistoryProjection() {
          @Override
          public Integer getHistoryId() {
            return 2;
          }

          @Override
          public String getEmail() {
            return "TEST2@autozone.com";
          }

          @Override
          public String getEndpointName() {
            return "TEST NAME 2";
          }

          @Override
          public String getEndpointDescription() {
            return "TEST DESC 2";
          }

          @Override
          public LocalDateTime getCreatedAt() {
            return LocalDateTime.of(2025, 1, 1, 0, 0, 0);
          }
        };
    List<HistoryProjection> history = List.of(history1, history2);

    given(historyRepository.findAllProjected()).willReturn(history);

    // WHEN
    List<HistoryDTO> result = historyService.getAllHistory();

    // THEN
    assertEquals(history.size(), result.size());
    HistoryDTO result1 = result.get(0);
    assertEquals(history1.getHistoryId(), result1.getHistoryId());
    assertEquals(history1.getEmail(), result1.getEmail());
    assertEquals(history1.getEndpointDescription(), result1.getEndpointDescription());
    assertEquals(history1.getEndpointName(), result1.getEndpointName());
    assertEquals(history1.getCreatedAt(), result1.getCreatedAt());

    assertEquals(history.size(), result.size());
    HistoryDTO result2 = result.get(1);
    assertEquals(history2.getHistoryId(), result2.getHistoryId());
    assertEquals(history2.getEmail(), result2.getEmail());
    assertEquals(history2.getEndpointDescription(), result2.getEndpointDescription());
    assertEquals(history2.getEndpointName(), result2.getEndpointName());
    assertEquals(history2.getCreatedAt(), result2.getCreatedAt());
  }

  @Test
  void getAllHistoryWithUserFilter() {
    // GIVEN
    HistoryProjection history1 =
        new HistoryProjection() {
          @Override
          public Integer getHistoryId() {
            return 1;
          }

          @Override
          public String getEmail() {
            return "TEST1@autozone.com";
          }

          @Override
          public String getEndpointName() {
            return "TEST NAME 1";
          }

          @Override
          public String getEndpointDescription() {
            return "TEST DESC 1";
          }

          @Override
          public LocalDateTime getCreatedAt() {
            return LocalDateTime.of(2025, 1, 1, 0, 0, 0);
          }
        };
    List<HistoryProjection> history = List.of(history1);

    given(historyRepository.findByUserId(1)).willReturn(history);

    // WHEN
    List<HistoryDTO> result = historyService.getHistoryByUserId(1);

    // THEN
    assertEquals(history.size(), result.size());
    HistoryDTO result1 = result.get(0);
    assertEquals(history1.getHistoryId(), result1.getHistoryId());
    assertEquals(history1.getEmail(), result1.getEmail());
    assertEquals(history1.getEndpointDescription(), result1.getEndpointDescription());
    assertEquals(history1.getEndpointName(), result1.getEndpointName());
    assertEquals(history1.getCreatedAt(), result1.getCreatedAt());
  }
}
