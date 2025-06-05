package com.autozone.cazss_backend.util;

import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PasswordEncodingMigration implements CommandLineRunner {

  private final UserRepository userRepository;
  private final Argon2PasswordEncoder passwordEncoder;
  private static final String DEFAULT_PASSWORD = "defaultPassword123";

  @Autowired
  public PasswordEncodingMigration(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
  }

  @Override
  @Transactional
  public void run(String... args) {
    List<UserEntity> users = userRepository.findAll();
    System.out.println("Starting password migration...");

    for (UserEntity user : users) {
      if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
        System.out.println(
            "User " + user.getEmail() + " has empty/null password. Setting default password.");
        user.setPassword(DEFAULT_PASSWORD);
      }

      // Only encode if the password is not already encoded
      if (!isPasswordEncoded(user.getPassword())) {
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        System.out.println("Encoded password for user: " + user.getEmail());
      } else {
        System.out.println("Password already encoded for user: " + user.getEmail());
      }
    }
    System.out.println("Password migration completed.");
  }

  private boolean isPasswordEncoded(String password) {
    // Argon2 encoded passwords start with $argon2
    return password != null && password.startsWith("$argon2");
  }
}
