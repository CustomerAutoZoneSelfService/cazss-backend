package com.autozone.cazss_backend.repository;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = CazssBackendApplication.class)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void givenUserRepository_whenSaveAndRetreiveUser_thenOK() {
        UserEntity user = userRepository
                .save(new UserEntity("johndoe@autozone.com",true, UserRoleEnum.USER));

        Optional<UserEntity> foundUserOptional = userRepository.findById(user.getUserId());

        // Assert
        assertTrue(foundUserOptional.isPresent(), "User should be found");

        UserEntity foundUser = foundUserOptional.get();
        assertEquals(user, foundUser);

    }
}
