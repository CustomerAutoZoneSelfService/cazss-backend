package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.autozone.cazss_backend.DTO.HistoryDTO;
import com.autozone.cazss_backend.DTO.HistoryDetailedDTO;
import com.autozone.cazss_backend.exceptions.HistoryNotFoundException;
import com.autozone.cazss_backend.projections.HistoryDetailedProjection;
import com.autozone.cazss_backend.projections.HistoryProjection;
import com.autozone.cazss_backend.repository.HistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

  @Test
  void getHistoryById_shouldReturnHistoryDetailedDTO_WhenIdIsValid() {
    // Arrange
    Integer historyId = 1;

    HistoryDetailedProjection requestProjection = mock(HistoryDetailedProjection.class);
    HistoryDetailedProjection responseProjection = mock(HistoryDetailedProjection.class);

    when(requestProjection.getContent()).thenReturn("{\"id\":1}");
    when(responseProjection.getContent()).thenReturn("{\"ok\":true}");

    when(historyRepository.findHistoryRequestByHistoryId(historyId))
        .thenReturn(Optional.of(requestProjection));
    when(historyRepository.findHistoryResponseByHistoryId(historyId))
        .thenReturn(Optional.of(responseProjection));

    // Act
    HistoryDetailedDTO result = historyService.getHistoryById(historyId);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getHistoryData());
    assertEquals("{\"id\":1}", result.getHistoryData().getRequest());
    assertEquals("{\"ok\":true}", result.getHistoryData().getResponse());
  }

  @Test
  void getHistoryById_shouldThrowHistoryNotFoundException_WhenIdIsInvalid() {
    Integer invalidId = 999;

    when(historyRepository.findHistoryRequestByHistoryId(invalidId)).thenReturn(Optional.empty());

    assertThrows(HistoryNotFoundException.class, () -> historyService.getHistoryById(invalidId));
  }
}
