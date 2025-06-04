package com.autozone.cazss_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserDetailsServiceImpl userDetailsService;

  private UserEntity userEntity;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity();
    userEntity.setUserId(1);
    userEntity.setEmail("test@example.com");
    userEntity.setUsername("testuser");
    userEntity.setPassword("hashedpassword");
    userEntity.setRole(UserRoleEnum.USER);
    userEntity.setActive(true);
  }

  @Test
  void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));

    UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

    assertNotNull(userDetails);
    assertEquals(
        String.valueOf(userEntity.getUserId()), userDetails.getUsername()); // Crucial check
    assertEquals(userEntity.getPassword(), userDetails.getPassword());
    assertTrue(
        userDetails.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
    assertTrue(userDetails.isEnabled());

    verify(userRepository).findByEmail("test@example.com");
  }

  @Test
  void loadUserByUsername_shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {
    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class,
        () -> {
          userDetailsService.loadUserByUsername("nonexistent@example.com");
        });

    verify(userRepository).findByEmail("nonexistent@example.com");
  }

  @Test
  void loadUserByUsername_shouldThrowIllegalStateException_whenUserHasNoPassword() {
    userEntity.setPassword(null);
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));

    assertThrows(
        IllegalStateException.class,
        () -> {
          userDetailsService.loadUserByUsername("test@example.com");
        });
  }

  @Test
  void loadUserById_shouldReturnUserDetails_whenUserExists() {
    when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));

    UserDetails userDetails = userDetailsService.loadUserById(1);

    assertNotNull(userDetails);
    assertEquals(String.valueOf(userEntity.getUserId()), userDetails.getUsername());
    assertEquals(userEntity.getPassword(), userDetails.getPassword());
    assertTrue(
        userDetails.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
    assertTrue(userDetails.isEnabled());

    verify(userRepository).findById(1);
  }

  @Test
  void loadUserById_shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {
    when(userRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class,
        () -> {
          userDetailsService.loadUserById(99);
        });

    verify(userRepository).findById(99);
  }
}
