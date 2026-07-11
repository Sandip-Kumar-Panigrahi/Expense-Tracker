package com.expensetracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Renders the login page. Spring Security's formLogin() posts credentials
 * to this same "/login" URL automatically, so no POST handler is needed here.
 */
@Controller
public class AuthViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
