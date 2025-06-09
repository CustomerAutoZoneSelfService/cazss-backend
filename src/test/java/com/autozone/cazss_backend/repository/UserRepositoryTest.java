package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest {

  @Autowired UserRepository userRepository;

  @Transactional
  @Test
  public void givenUserRepository_whenSaveAndRetreiveUser_thenOK() {
    UserEntity user =
        userRepository.save(
            new UserEntity(
                "userRepoTestUser@autozone.com",
                true,
                UserRoleEnum.USER,
                "testPassword123",
                "userRepoTestUser"));

    Optional<UserEntity> foundUserOptional = userRepository.findById(user.getUserId());

    // Assert
    assertTrue(foundUserOptional.isPresent(), "User should be found");

    UserEntity foundUser = foundUserOptional.get();
    assertEquals(user, foundUser);
  }

  @Transactional
  @Test
  public void givenUserRepository_whenUpdateUser_thenOK() {
    UserEntity user =
        userRepository.save(
            new UserEntity(
                "oldUserEmail@autozone.com",
                true,
                UserRoleEnum.USER,
                "testPassword123",
                "oldUserName"));

    user.setEmail("newUserEmail@autozone.com");
    user.setActive(false);
    user.setUsername("newUserName");

    UserEntity updatedUser = userRepository.save(user);

    Optional<UserEntity> foundUserOptional = userRepository.findById(updatedUser.getUserId());

    assertTrue(foundUserOptional.isPresent(), "Updated user should be found");

    UserEntity foundUser = foundUserOptional.get();
    assertEquals("newUserEmail@autozone.com", foundUser.getEmail());
    assertFalse(foundUser.getActive());
    assertEquals("newUserName", foundUser.getUsername());
  }

  @Transactional
  @Test
  public void givenUserRepository_whenDeleteUser_thenOK() {
    UserEntity user =
        userRepository.save(
            new UserEntity(
                "tobedeleted@autozone.com",
                true,
                UserRoleEnum.USER,
                "testPassword123",
                "toBeDeletedUser"));

    Integer userId = user.getUserId();

    userRepository.deleteById(userId);

    Optional<UserEntity> foundUserOptional = userRepository.findById(userId);

    assertFalse(foundUserOptional.isPresent(), "User should be deleted");
  }
}
