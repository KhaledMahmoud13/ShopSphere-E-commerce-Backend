package com.khaled.shopsphere;

import com.khaled.shopsphere.role.Role;
import com.khaled.shopsphere.role.RoleRepository;
import com.khaled.shopsphere.user.User;
import com.khaled.shopsphere.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class ShopSphereApplication {

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public static void main(String[] args) {
        SpringApplication.run(ShopSphereApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            final UserRepository userRepository,
            final RoleRepository roleRepository,
            final PasswordEncoder passwordEncoder
    ) {
        return _ -> {

            if (userRepository.findByEmailIgnoreCase(adminEmail).isPresent()) {
                return;
            }

            Role defaultRole = roleRepository.findByNameWithPermissions("ROLE_ADMIN")
                    .orElseThrow(() -> new EntityNotFoundException(
                            "ROLE_ADMIN not found"));

            final User user = User.builder()
                    .firstName("Admin")
                    .lastName("0")
                    .email(adminEmail)
                    .phoneNumber("+201234567891")
                    .password(passwordEncoder.encode(adminPassword))
                    .enabled(true)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .emailVerified(true)
                    .phoneVerified(true)
                    .build();

            user.getRoles().add(defaultRole);

            userRepository.save(user);
        };
    }
}
