package com.miniproject.currencyconverter;

import com.miniproject.currencyconverter.exception.InvalidCurrencyException;
import com.miniproject.currencyconverter.model.ExchangeRateApiResponse;
import com.miniproject.currencyconverter.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CurrencyService using a mocked RestTemplate,
 * so tests run without needing real network access.
 */
class CurrencyServiceTest {

    private RestTemplate restTemplate;
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        currencyService = new CurrencyService(restTemplate);
        ReflectionTestUtils.setField(currencyService, "exchangeApiBaseUrl", "https://fake.test/latest");
    }

    private ExchangeRateApiResponse fakeResponse() {
        ExchangeRateApiResponse response = new ExchangeRateApiResponse();
        response.setResult("success");
        response.setBaseCode("USD");
        response.setRates(Map.of("INR", new BigDecimal("83.50"), "EUR", new BigDecimal("0.92")));
        return response;
    }

    @Test
    void convert_returnsCorrectAmount() {
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateApiResponse.class)))
                .thenReturn(fakeResponse());

        var result = currencyService.convert("usd", "inr", new BigDecimal("100"));

        assertEquals("USD", result.getFrom());
        assertEquals("INR", result.getTo());
        assertEquals(new BigDecimal("8350.0000"), result.getConvertedAmount());
    }

    @Test
    void convert_unknownTargetCurrency_throws() {
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateApiResponse.class)))
                .thenReturn(fakeResponse());

        assertThrows(InvalidCurrencyException.class,
                () -> currencyService.convert("USD", "XXX", new BigDecimal("100")));
    }

    @Test
    void convert_negativeAmount_throws() {
        assertThrows(InvalidCurrencyException.class,
                () -> currencyService.convert("USD", "INR", new BigDecimal("-5")));
    }
}
