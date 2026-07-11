package com.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Money Expense Tracker Spring Boot application.
 * Starts an embedded Tomcat server, connects to the SQL database,
 * and serves both the REST API (/api/**) and the static front end (/).
 */
@SpringBootApplication
public class LedgerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LedgerApplication.class, args);
    }
}
