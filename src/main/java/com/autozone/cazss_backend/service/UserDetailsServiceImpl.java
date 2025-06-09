package com.autozone.cazss_backend.service;

import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Carga un usuario por su email. Este método es el requerido por la interfaz UserDetailsService
   * cuando Spring Security intenta autenticar con email/password. El UserDetails devuelto tendrá el
   * ID del usuario (como String) como el "username" para consistencia interna con cómo se manejarán
   * los JWTs en frontend.
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity userEntity =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email));
    return buildUserDetails(userEntity);
  }

  @Transactional(readOnly = true)
  public UserDetails loadUserById(Integer userId) throws UsernameNotFoundException {
    UserEntity userEntity =
        userRepository
            .findById(userId) // Asumiendo que UserRepository tiene findById
            .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
    return buildUserDetails(userEntity);
  }

  private UserDetails buildUserDetails(UserEntity userEntity) {

    if (userEntity.getPassword() == null) {
      throw new IllegalStateException(
          "UserEntity para " + userEntity.getEmail() + " no tiene contraseña definida.");
    }
    String passwordHash = userEntity.getPassword();

    List<GrantedAuthority> authorities =
        Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name()));

    return new User(
        String.valueOf(userEntity.getUserId()),
        passwordHash,
        userEntity.getActive(),
        true,
        true,
        true,
        authorities);
  }
}
