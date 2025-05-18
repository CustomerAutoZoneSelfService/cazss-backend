package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.autozone.cazss_backend.projections.HistoryProjection;
import com.autozone.cazss_backend.repository.HistoryRepository;
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
    HistoryProjection history1 = mock(HistoryProjection.class);
    HistoryProjection history2 = mock(HistoryProjection.class);
    List<HistoryProjection> mockHistoryList = List.of(history1, history2);

    given(historyRepository.findAllProjected()).willReturn(mockHistoryList);

    // WHEN
    List<HistoryProjection> result = historyService.getAllHistory();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains(history1));
    assertTrue(result.contains(history2));

    // Verify repository interaction
    org.mockito.Mockito.verify(historyRepository).findAllProjected();
  }
}
