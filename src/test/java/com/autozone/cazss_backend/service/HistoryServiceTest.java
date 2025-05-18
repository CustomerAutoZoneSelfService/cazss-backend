package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.autozone.cazss_backend.DTO.HistoryDTO;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.EndpointsEntity;
import com.autozone.cazss_backend.entity.HistoryEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
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
    UserEntity user = new UserEntity();
    user.setEmail("test@autozone.com");
    user.setActive(true);
    user.setRole(UserRoleEnum.ADMIN);

    CategoryEntity category = new CategoryEntity();
    category.setName("TEST");
    category.setColor("#FFFFFF");

    EndpointsEntity endpoint = new EndpointsEntity();
    endpoint.setCategory(category);
    endpoint.setUser(user);
    endpoint.setActive(true);
    endpoint.setName("TEST NAME");
    endpoint.setDescription("TEST DESC");
    endpoint.setMethod(EndpointMethodEnum.GET);
    endpoint.setUrl("localhost/TEST");

    HistoryEntity history = new HistoryEntity();
    history.setHistoryId(1);
    history.setUser(user);
    history.setEndpoint(endpoint);
    history.setStatusCode(200);
    history.setCreatedAt(LocalDateTime.now());
    history.setHistoryData(null);

    given(historyRepository.findAll()).willReturn(List.of(history));

    // WHEN
    List<HistoryDTO> actualHistory = historyService.getAllHistory();

    // THEN
    assertEquals(history.getHistoryId(), actualHistory.get(0).getHistoryId());
    assertEquals(history.getEndpoint().getName(), actualHistory.get(0).getEndpoint().getName());
    assertEquals(
        history.getEndpoint().getDescription(),
        actualHistory.get(0).getEndpoint().getDescription());
    assertEquals(history.getCreatedAt(), actualHistory.get(0).getCreatedAt());
  }
}
