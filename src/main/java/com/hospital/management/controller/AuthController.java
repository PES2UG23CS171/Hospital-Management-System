package com.hospital.management.controller;

import com.hospital.management.model.User;
import com.hospital.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam String role) {
        username = username.trim();

        if (username.length() < 3) {
            return "redirect:/register?error=username";
        }
        if (password.length() < 6) {
            return "redirect:/register?error=weak";
        }
        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=mismatch";
        }
        if (userRepository.existsByUsername(username)) {
            return "redirect:/register?error=exists";
        }

        User.Role userRole;
        try {
            userRole = User.Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            return "redirect:/register?error=role";
        }
        if (userRole == User.Role.ADMIN) {
            // Admin accounts are created by existing admins, not self-registration
            return "redirect:/register?error=role";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(userRole);
        userRepository.save(user);

        return "redirect:/login?registered=true";
    }
}
