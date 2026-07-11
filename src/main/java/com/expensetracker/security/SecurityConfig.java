package com.expensetracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Defines the app's access rules:
 *  - /login, /register, and the H2 console are open to everyone
 *  - everything else (including the ledger page itself and every /api/** call)
 *    requires a logged-in session
 *  - successful login goes to "/" (the ledger); logout goes back to "/login"
 *
 * Note on CSRF: it's disabled here for simplicity, since the front end is a
 * plain JavaScript page calling the API with fetch() and doesn't forward a
 * CSRF token. Session cookies alone still keep requests scoped to the
 * logged-in user. If you deploy this somewhere with untrusted users or
 * embed it in another site, revisit this — see README.md for notes.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Required so the H2 console (served in a frame) still renders with security enabled
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
