package com.expensetracker.controller;

import com.expensetracker.dto.RegisterRequest;
import com.expensetracker.model.AppUser;
import com.expensetracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Handles new account creation. Each registered account gets its own
 * private set of expenses — nothing here grants extra privileges, every
 * user is equal, just isolated from each other's data.
 */
@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showForm(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest registerRequest, Model model) {
        String username = registerRequest.getUsername() == null ? "" : registerRequest.getUsername().trim();
        String password = registerRequest.getPassword() == null ? "" : registerRequest.getPassword();

        if (username.isEmpty() || username.length() < 3) {
            model.addAttribute("error", "Username must be at least 3 characters.");
            return "register";
        }
        if (password.length() < 4) {
            model.addAttribute("error", "Password must be at least 4 characters.");
            return "register";
        }
        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "That username is already taken — pick another.");
            return "register";
        }

        String displayName = registerRequest.getDisplayName() == null || registerRequest.getDisplayName().isBlank()
                ? username
                : registerRequest.getDisplayName().trim();

        AppUser user = new AppUser(username, passwordEncoder.encode(password), displayName);
        userRepository.save(user);

        return "redirect:/login?registered";
    }
}
