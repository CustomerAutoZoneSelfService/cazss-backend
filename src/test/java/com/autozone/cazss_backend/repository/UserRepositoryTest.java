package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import java.util.Optional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CazssBackendApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest {

  @Autowired UserRepository userRepository;

  @Test
  public void givenUserRepository_whenSaveAndRetreiveUser_thenOK() {
    UserEntity user =
        userRepository.save(new UserEntity("johndoe@autozone.com", true, UserRoleEnum.USER));

    Optional<UserEntity> foundUserOptional = userRepository.findById(user.getUserId());

    // Assert
    assertTrue(foundUserOptional.isPresent(), "User should be found");

    UserEntity foundUser = foundUserOptional.get();
    assertEquals(user, foundUser);
  }

  @Test
  public void givenUserRepository_whenUpdateUser_thenOK() {
    UserEntity user =
        userRepository.save(new UserEntity("janedoe@autozone.com", true, UserRoleEnum.USER));

    user.setEmail("newemail@autozone.com");
    user.setActive(false);

    UserEntity updatedUser = userRepository.save(user);

    Optional<UserEntity> foundUserOptional = userRepository.findById(updatedUser.getUserId());

    assertTrue(foundUserOptional.isPresent(), "Updated user should be found");

    UserEntity foundUser = foundUserOptional.get();
    assertEquals("newemail@autozone.com", foundUser.getEmail());
    assertFalse(foundUser.getActive());
  }

  @Test
  public void givenUserRepository_whenDeleteUser_thenOK() {
    UserEntity user =
        userRepository.save(new UserEntity("tobedeleted@autozone.com", true, UserRoleEnum.USER));

    Integer userId = user.getUserId();

    userRepository.deleteById(userId);

    Optional<UserEntity> foundUserOptional = userRepository.findById(userId);

    assertFalse(foundUserOptional.isPresent(), "User should be deleted");
  }
}
