// service/UserService.java
package com.hospital.management.service;

import com.hospital.management.model.User;
import com.hospital.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;

    // ✅ LOGGED_OUT → LOGGING_IN → ACTIVE (or back to LOGGED_OUT)
    public User login(String username, String rawPassword) {
        Optional<User> opt = userRepository.findByUsername(username);

        if (opt.isEmpty()) return null;

        User user = opt.get();
        user.setState(User.UserState.LOGGING_IN);    // intermediate state
        userRepository.save(user);

        if (user.getPassword().equals(rawPassword)) {
            // Use BCryptPasswordEncoder.matches() if you hash passwords
            user.setState(User.UserState.ACTIVE);
            userRepository.save(user);
            return user;
        } else {
            user.setState(User.UserState.LOGGED_OUT);
            userRepository.save(user);
            return null;
        }
    }

    // ✅ ACTIVE → CHANGING_PASSWORD → ACTIVE
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

        user.setState(User.UserState.CHANGING_PASSWORD);
        userRepository.save(user);

        user.setPassword(newPassword);   // hash with BCrypt in production!
        user.setState(User.UserState.ACTIVE);
        userRepository.save(user);
    }

    // ✅ ACTIVE → LOGGED_OUT
    public void logout(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setState(User.UserState.LOGGED_OUT);
        userRepository.save(user);
    }

    public User save(User user) { return userRepository.save(user); }
}