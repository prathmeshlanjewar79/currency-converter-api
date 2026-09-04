package com.miniproject.currencyconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Entry point for the Currency Converter API.
 *
 * Mini Project - Second Year Engineering (Mumbai University)
 * Topic: Currency Converter API with Java Backend
 *
 * Run with: mvn spring-boot:run
 * Then open http://localhost:8080 in your browser for the demo UI,
 * or call the REST endpoints directly (see README.md).
 */
@SpringBootApplication
@EnableCaching
public class CurrencyConverterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyConverterApplication.class, args);
    }
}
