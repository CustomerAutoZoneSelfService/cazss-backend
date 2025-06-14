package com.autozone.cazss_backend.config;

import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import com.autozone.cazss_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Initializes default users if they do not already exist.
 * This runner executes once the Spring application context is loaded.
 */
@Component
public class AdminUserInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    /**
     * The callback method executed on application startup.
     * It checks for the existence of an admin, configurator, auditor and user and creates one if not found.
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername("admin");
            user.setEmail("admin@autozone.com");
            user.setPassword("$argon2id$v=19$m=16,t=2,p=1$OEl6Mkh5OGZrbDJ3cExRRQ$XzCAkZiFKi8IJvV+TrEVQQ");
            user.setRole(UserRoleEnum.ADMIN);
            user.setActive(true);

            userRepository.save(user);
        }

        if (userRepository.findByUsername("configurator").isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername("configurator");
            user.setEmail("configurator@autozone.com");
            user.setPassword("$argon2id$v=19$m=16,t=2,p=1$eEp5WUlGck1qeUJtMzRVWQ$LrBU2GsLLNQDohdRc1KhfA");
            user.setRole(UserRoleEnum.CONFIG);
            user.setActive(true);

            userRepository.save(user);
        }

        if (userRepository.findByUsername("auditor").isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername("auditor");
            user.setEmail("auditor@autozone.com");
            user.setPassword("$argon2id$v=19$m=16,t=2,p=1$c0hFMkZPMU45R2djZjZkag$CEhLAWqYvArRAU6bqat5nw");
            user.setRole(UserRoleEnum.AUDITOR);
            user.setActive(true);

            userRepository.save(user);
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername("user");
            user.setEmail("user@autozone.com");
            user.setPassword("$argon2id$v=19$m=16,t=2,p=1$M1Fyc2E0UFlOTUZpanV6OA$GWspvESlBche2CvuL0qJsw");
            user.setRole(UserRoleEnum.USER);
            user.setActive(true);

            userRepository.save(user);
        }
    }
}