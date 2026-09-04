package com.miniproject.currencyconverter.exception;

/**
 * Thrown when a currency code is not recognized or the external
 * exchange rate provider could not be reached.
 */
public class InvalidCurrencyException extends RuntimeException {

    public InvalidCurrencyException(String message) {
        super(message);
    }
}
