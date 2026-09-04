package com.miniproject.currencyconverter.service;

import com.miniproject.currencyconverter.exception.InvalidCurrencyException;
import com.miniproject.currencyconverter.model.ConversionResponse;
import com.miniproject.currencyconverter.model.ExchangeRateApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Business logic for the currency converter.
 *
 * Live exchange rates are fetched from a free, no-API-key-required
 * provider (open.er-api.com) and cached in memory for a short time
 * so repeated conversions between the same base currency don't hit
 * the external API on every request.
 */
@Service
public class CurrencyService {

    private final RestTemplate restTemplate;

    @Value("${exchangerate.api.base-url}")
    private String exchangeApiBaseUrl;

    /** A small, commonly-used subset shown by the /api/currencies endpoint. */
    private static final Set<String> POPULAR_CURRENCIES = new LinkedHashSet<>(Set.of(
            "USD", "INR", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF",
            "CNY", "SGD", "AED", "NZD", "ZAR", "RUB", "BRL"
    ));

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches the full rate table for a given base currency.
     * Cached per base currency to reduce external calls.
     */
    @Cacheable(value = "rates", key = "#baseCurrency")
    public Map<String, BigDecimal> getRates(String baseCurrency) {
        String base = normalize(baseCurrency);
        String url = exchangeApiBaseUrl + "/" + base;

        ExchangeRateApiResponse response;
        try {
            response = restTemplate.getForObject(url, ExchangeRateApiResponse.class);
        } catch (RestClientException ex) {
            throw new InvalidCurrencyException(
                    "Could not fetch exchange rates. Check the currency code or your network connection.");
        }

        if (response == null || !"success".equalsIgnoreCase(response.getResult()) || response.getRates() == null) {
            throw new InvalidCurrencyException("Unknown base currency code: " + base);
        }

        return response.getRates();
    }

    /**
     * Converts an amount from one currency to another using the
     * latest available exchange rate.
     */
    public ConversionResponse convert(String from, String to, BigDecimal amount) {
        String fromCode = normalize(from);
        String toCode = normalize(to);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidCurrencyException("Amount must be zero or greater.");
        }

        Map<String, BigDecimal> rates = getRates(fromCode);
        BigDecimal rate = rates.get(toCode);

        if (rate == null) {
            throw new InvalidCurrencyException("Unknown target currency code: " + toCode);
        }

        BigDecimal converted = amount.multiply(rate).setScale(4, RoundingMode.HALF_UP);

        return new ConversionResponse(fromCode, toCode, amount, rate, converted, LocalDateTime.now());
    }

    /** Returns the exchange rate between two currencies without converting an amount. */
    public BigDecimal getRate(String from, String to) {
        String fromCode = normalize(from);
        String toCode = normalize(to);
        Map<String, BigDecimal> rates = getRates(fromCode);
        BigDecimal rate = rates.get(toCode);
        if (rate == null) {
            throw new InvalidCurrencyException("Unknown target currency code: " + toCode);
        }
        return rate;
    }

    public Set<String> getPopularCurrencies() {
        return POPULAR_CURRENCIES;
    }

    private String normalize(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidCurrencyException("Currency code must not be empty.");
        }
        return code.trim().toUpperCase();
    }
}
