package com.khaled.shopsphere;

import com.khaled.shopsphere.auth.request.RegistrationRequest;
import com.khaled.shopsphere.role.Role;
import com.khaled.shopsphere.role.RoleRepository;
import com.khaled.shopsphere.user.User;
import com.khaled.shopsphere.user.UserMapper;
import com.khaled.shopsphere.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
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
            final UserMapper userMapper
    ) {
        return _ -> {

            if (userRepository.findByEmailIgnoreCase(adminEmail).isPresent()) {
                return;
            }

            Role defaultRole = roleRepository.findByNameWithPermissions("ROLE_ADMIN")
                    .orElseThrow(() -> new EntityNotFoundException(
                            "ROLE_ADMIN not found"));

            final User user = userMapper.toUser(RegistrationRequest.builder()
                    .email(adminEmail)
                    .firstName("Admin").
                    lastName("0").
                    phoneNumber("+201234567891").
                    password(adminPassword)
                    .confirmPassword(adminPassword)
                    .build());

            user.getRoles().add(defaultRole);

            userRepository.save(user);
        };
    }
}
