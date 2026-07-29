package com.hospital.management;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Sanity checks on which pages are public and which require authentication.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTests {

    @Autowired private MockMvc mockMvc;

    @Test
    void loginPageIsPubliclyReachable() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dhrushaj Hospital")))
                .andExpect(content().string(containsString("Sign In")));
    }

    @Test
    void registerPageIsPubliclyReachable() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create Account")));
    }

    @Test
    void rootSendsAnonymousVisitorsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rootServesTheDashboardForAuthenticatedUsers() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/dashboard", "/patients", "/doctors", "/staff",
            "/appointments", "/medicines", "/bills", "/medical-records",
            "/admin/dashboard", "/admin/reports"
    })
    void protectedPagesRedirectAnonymousUsersToLogin(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void authenticatedUserReachesDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminReportsPageIsReachableForAdmin() throws Exception {
        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reports"));
    }
}
