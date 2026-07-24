package com.hospital.management.config;

import com.hospital.management.model.User;
import com.hospital.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedAdminUser(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    @Value("${spring.security.user.name}") String adminUsername,
                                    @Value("${spring.security.user.password}") String adminPassword) {
        return args -> {
            if (!userRepository.existsByUsername(adminUsername)) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(User.Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
