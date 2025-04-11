package com.autozone.cazss_backend.repository;
import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.RequestVariableTypeEnum;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CazssBackendApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RequestVariableRepositoryTest {

    @Autowired
    EndpointsRepository endpointsRepository;

    @Autowired
    RequestVariableRepository requestVariableRepository;

    @Test
    @Order(11)
    public void givenRequestVariableRepository_whenSaveAndRetreiveRequestVariable_thenOK(){
        Optional<EndpointsEntity> endpointOptional = endpointsRepository.findByName("EndpointOne");
        assertTrue(endpointOptional.isPresent(), "Endpoint should be present");
        EndpointsEntity endpoint = endpointOptional.get();

        RequestVaraibleEntity requestVaraible = requestVariableRepository
                .save(new RequestVaraibleEntity(
                        endpoint,
                        RequestVariableTypeEnum.BODY,
                        "id",
                        "",
                        true,
                        "Id description"
                ));

        Optional<RequestVaraibleEntity> foundRequestVaraibleEntityOptional = requestVariableRepository.findById(requestVaraible.getRequestVariableId());

        assertTrue(foundRequestVaraibleEntityOptional.isPresent(), "Request variable is present");

        RequestVaraibleEntity foundRequestVariable = foundRequestVaraibleEntityOptional.get();
        assertEquals(requestVaraible, foundRequestVariable);
    }
}
