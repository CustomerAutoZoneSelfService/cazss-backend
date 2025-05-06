package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.CategoryEntity;
import com.autozone.cazss_backend.entity.FlowEntity;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
public class FlowRepositoryTest {

  @Autowired private FlowRepository flowRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private UserRepository userRepository;

  @Test
  public void givenFlowRepository_whenSaveAndRetrieveFlow_thenOK() {
    CategoryEntity category = categoryRepository.save(new CategoryEntity("FLOW_TEST", "#123456"));
    UserEntity user =
        userRepository.save(new UserEntity("flowuser@autozone.com", true, UserRoleEnum.USER));

    FlowEntity flow = new FlowEntity();
    flow.setName("Sample Flow");
    flow.setDescription("Flow Description");
    flow.setScript("console.log('Hello AutoZone');");
    flow.setCategory(category);
    flow.setUser(user);

    FlowEntity savedFlow = flowRepository.save(flow);

    Optional<FlowEntity> foundFlowOptional = flowRepository.findById(savedFlow.getFlowId());
    assertTrue(foundFlowOptional.isPresent(), "Flow should be found");

    FlowEntity foundFlow = foundFlowOptional.get();
    assertEquals(savedFlow.getName(), foundFlow.getName());
    assertEquals(savedFlow.getDescription(), foundFlow.getDescription());
    assertEquals(savedFlow.getScript(), foundFlow.getScript());
    assertEquals(savedFlow.getUser(), foundFlow.getUser());
    assertEquals(savedFlow.getCategory(), foundFlow.getCategory());
  }

  @Test
  public void givenFlowRepository_whenUpdateFlow_thenOK() {
    CategoryEntity category = categoryRepository.save(new CategoryEntity("FLOW_UPDATE", "#ABCDEF"));
    UserEntity user =
        userRepository.save(new UserEntity("updateuser@autozone.com", true, UserRoleEnum.ADMIN));

    FlowEntity flow =
        flowRepository.save(
            new FlowEntity(
                null,
                category,
                user,
                "Initial Flow",
                "Initial Description",
                "console.log('Init');"));

    flow.setName("Updated Flow");
    flow.setDescription("Updated Description");
    flow.setScript("console.log('Updated');");

    FlowEntity updatedFlow = flowRepository.save(flow);

    Optional<FlowEntity> foundFlowOptional = flowRepository.findById(updatedFlow.getFlowId());
    assertTrue(foundFlowOptional.isPresent(), "Updated flow should be found");

    FlowEntity foundFlow = foundFlowOptional.get();
    assertEquals("Updated Flow", foundFlow.getName());
    assertEquals("Updated Description", foundFlow.getDescription());
    assertEquals("console.log('Updated');", foundFlow.getScript());
  }

  @Test
  public void givenFlowRepository_whenDeleteFlow_thenOK() {
    CategoryEntity category = categoryRepository.save(new CategoryEntity("FLOW_DELETE", "#000000"));
    UserEntity user =
        userRepository.save(new UserEntity("deleteuser@autozone.com", true, UserRoleEnum.USER));

    FlowEntity flow =
        flowRepository.save(
            new FlowEntity(null, category, user, "Flow To Delete", "Desc", "console.log('Bye');"));

    Integer flowId = flow.getFlowId();
    flowRepository.deleteById(flowId);

    Optional<FlowEntity> foundFlowOptional = flowRepository.findById(flowId);
    assertFalse(foundFlowOptional.isPresent(), "Flow should be deleted");
  }
}
