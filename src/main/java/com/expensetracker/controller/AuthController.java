package com.expensetracker.controller;

import com.expensetracker.model.AppUser;
import com.expensetracker.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Small API the front-end JavaScript uses to find out who's logged in,
 * so it can show "Signed in as ..." without needing a full page template.
 */
@RestController
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/me")
    public Map<String, String> me(Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userRepository.findByUsername(username).orElse(null);
        String displayName = (user != null && user.getDisplayName() != null && !user.getDisplayName().isBlank())
                ? user.getDisplayName()
                : username;
        return Map.of("username", username, "displayName", displayName);
    }
}
