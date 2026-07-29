package com.hospital.management;

import com.hospital.management.model.User;
import com.hospital.management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Registration flow: validation rules, password hashing, and logging in afterwards.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RegistrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private void register(String username, String password, String confirm, String role,
                          String expectedRedirect) throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", username)
                        .param("password", password)
                        .param("confirmPassword", confirm)
                        .param("role", role))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirect));
    }

    @Test
    void registersNewPatientAccount() throws Exception {
        register("alice", "secret123", "secret123", "PATIENT", "/login?registered=true");

        User created = userRepository.findByUsername("alice").orElse(null);
        assertThat(created).isNotNull();
        assertThat(created.getRole()).isEqualTo(User.Role.PATIENT);
    }

    @Test
    void storesPasswordAsBcryptHash() throws Exception {
        register("bob", "secret123", "secret123", "DOCTOR", "/login?registered=true");

        User created = userRepository.findByUsername("bob").orElseThrow();
        assertThat(created.getPassword()).isNotEqualTo("secret123");
        assertThat(passwordEncoder.matches("secret123", created.getPassword())).isTrue();
    }

    @Test
    void trimsSurroundingWhitespaceFromUsername() throws Exception {
        register("  carol  ", "secret123", "secret123", "STAFF", "/login?registered=true");

        assertThat(userRepository.existsByUsername("carol")).isTrue();
    }

    @Test
    void rejectsDuplicateUsername() throws Exception {
        register("dave", "secret123", "secret123", "PATIENT", "/login?registered=true");
        register("dave", "different1", "different1", "STAFF", "/register?error=exists");

        User existing = userRepository.findByUsername("dave").orElseThrow();
        assertThat(existing.getRole()).isEqualTo(User.Role.PATIENT);
    }

    @Test
    void rejectsMismatchedPasswords() throws Exception {
        register("erin", "secret123", "secret456", "PATIENT", "/register?error=mismatch");

        assertThat(userRepository.existsByUsername("erin")).isFalse();
    }

    @Test
    void rejectsShortPassword() throws Exception {
        register("frank", "123", "123", "PATIENT", "/register?error=weak");

        assertThat(userRepository.existsByUsername("frank")).isFalse();
    }

    @Test
    void rejectsShortUsername() throws Exception {
        register("gg", "secret123", "secret123", "PATIENT", "/register?error=username");

        assertThat(userRepository.existsByUsername("gg")).isFalse();
    }

    @Test
    void rejectsSelfRegistrationAsAdmin() throws Exception {
        register("hacker", "secret123", "secret123", "ADMIN", "/register?error=role");

        assertThat(userRepository.existsByUsername("hacker")).isFalse();
    }

    @Test
    void rejectsUnknownRole() throws Exception {
        register("ivan", "secret123", "secret123", "SUPERUSER", "/register?error=role");

        assertThat(userRepository.existsByUsername("ivan")).isFalse();
    }

    @Test
    void registeredUserCanAuthenticate() throws Exception {
        register("judy", "secret123", "secret123", "RECEPTIONIST", "/login?registered=true");

        mockMvc.perform(formLogin("/login").user("judy").password("secret123"))
                .andExpect(authenticated().withUsername("judy").withRoles("RECEPTIONIST"));
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        register("kevin", "secret123", "secret123", "PATIENT", "/login?registered=true");

        mockMvc.perform(formLogin("/login").user("kevin").password("wrong-password"))
                .andExpect(redirectedUrl("/login?error=true"));
    }
}
