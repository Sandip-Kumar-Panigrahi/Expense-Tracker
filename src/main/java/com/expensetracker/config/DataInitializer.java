package com.expensetracker.config;

import com.expensetracker.model.AppUser;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.CategoryService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Runs once at application startup:
 *  - seeds the default category set (Shopping, Traveling, Eating, etc.)
 *  - seeds one demo login account so you can try the app immediately
 *    without registering first (username: demo / password: demo1234)
 *
 * The demo account is only created if the users table is completely
 * empty, so it never overwrites or interferes with real accounts you
 * create later.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CategoryService categoryService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        categoryService.seedDefaultsIfEmpty();

        if (userRepository.count() == 0) {
            AppUser demo = new AppUser("demo", passwordEncoder.encode("demo1234"), "Demo User");
            userRepository.save(demo);
            System.out.println("----------------------------------------------------------");
            System.out.println("No accounts found — created a demo login:");
            System.out.println("  username: demo");
            System.out.println("  password: demo1234");
            System.out.println("Sign in with this, or register your own account at /register");
            System.out.println("----------------------------------------------------------");
        }
    }
}
