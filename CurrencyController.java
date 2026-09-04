package com.miniproject.currencyconverter.controller;

import com.miniproject.currencyconverter.model.ConversionResponse;
import com.miniproject.currencyconverter.service.CurrencyService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * REST API for currency conversion.
 *
 * Endpoints:
 *  GET /api/currencies                              -> list of supported currency codes
 *  GET /api/rates?base=USD                           -> full rate table for a base currency
 *  GET /api/rate?from=USD&to=INR                      -> single exchange rate
 *  GET /api/convert?from=USD&to=INR&amount=100        -> converted amount
 */
@RestController
@RequestMapping("/api")
@Validated
@CrossOrigin(origins = "*")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/currencies")
    public Set<String> getCurrencies() {
        return currencyService.getPopularCurrencies();
    }

    @GetMapping("/rates")
    public Map<String, BigDecimal> getRates(@RequestParam(defaultValue = "USD") @NotBlank String base) {
        return currencyService.getRates(base);
    }

    @GetMapping("/rate")
    public Map<String, Object> getRate(@RequestParam @NotBlank String from,
                                        @RequestParam @NotBlank String to) {
        BigDecimal rate = currencyService.getRate(from, to);
        return Map.of("from", from.toUpperCase(), "to", to.toUpperCase(), "rate", rate);
    }

    @GetMapping("/convert")
    public ConversionResponse convert(@RequestParam @NotBlank String from,
                                       @RequestParam @NotBlank String to,
                                       @RequestParam BigDecimal amount) {
        return currencyService.convert(from, to, amount);
    }
}
