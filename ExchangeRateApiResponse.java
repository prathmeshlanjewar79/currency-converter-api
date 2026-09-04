package com.miniproject.currencyconverter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Maps the JSON returned by the external exchange rate provider
 * (https://open.er-api.com/v6/latest/{BASE}).
 *
 * Example response:
 * {
 *   "result": "success",
 *   "base_code": "USD",
 *   "time_last_update_utc": "...",
 *   "rates": { "INR": 83.4, "EUR": 0.92, ... }
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateApiResponse {

    private String result;

    @JsonProperty("base_code")
    private String baseCode;

    @JsonProperty("time_last_update_utc")
    private String timeLastUpdateUtc;

    private Map<String, BigDecimal> rates;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public String getTimeLastUpdateUtc() {
        return timeLastUpdateUtc;
    }

    public void setTimeLastUpdateUtc(String timeLastUpdateUtc) {
        this.timeLastUpdateUtc = timeLastUpdateUtc;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }
}
