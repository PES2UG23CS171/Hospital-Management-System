package com.hospital.management;

import com.hospital.management.model.User;
import com.hospital.management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke tests: the application context starts and bootstraps its seed data.
 */
@SpringBootTest
class ManagementApplicationTests {

	@Autowired private UserRepository userRepository;
	@Autowired private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
	}

	@Test
	void adminAccountIsSeededOnStartup() {
		User admin = userRepository.findByUsername("admin").orElse(null);

		assertThat(admin).isNotNull();
		assertThat(admin.getRole()).isEqualTo(User.Role.ADMIN);
	}

	@Test
	void seededAdminPasswordIsHashedNotPlainText() {
		User admin = userRepository.findByUsername("admin").orElseThrow();

		assertThat(admin.getPassword()).isNotEqualTo("test-admin-password");
		assertThat(admin.getPassword()).startsWith("$2");
		assertThat(passwordEncoder.matches("test-admin-password", admin.getPassword())).isTrue();
	}
}
