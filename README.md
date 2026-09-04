# Currency Converter API

**Mini Project — Second Year Engineering (Mumbai University)**
**Backend:** Java 17 + Spring Boot 3
**Live exchange rates from:** [open.er-api.com](https://www.exchangerate-api.com/docs/free) (free, no API key required)

A REST API that converts an amount from one currency to another using
live exchange rates, with a small built-in web page to demo it in a
browser.

---

## 1. Features

- `GET /api/currencies` — list of popular supported currency codes
- `GET /api/rates?base=USD` — full exchange-rate table for a base currency
- `GET /api/rate?from=USD&to=INR` — single exchange rate between two currencies
- `GET /api/convert?from=USD&to=INR&amount=100` — converts an amount and returns the result
- In-memory caching of rate lookups (per base currency) so repeated
  conversions don't hit the external API every time
- Centralized error handling with clean JSON error responses
- Simple demo web page at `http://localhost:8080` (dropdown + amount box)
- Unit tests for the conversion logic (with the external API mocked out)

## 2. Project Structure

```
currency-converter-api/
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/miniproject/currencyconverter/
    │   │   ├── CurrencyConverterApplication.java   (main class)
    │   │   ├── config/AppConfig.java               (RestTemplate bean)
    │   │   ├── controller/CurrencyController.java  (REST endpoints)
    │   │   ├── service/CurrencyService.java        (business logic + caching)
    │   │   ├── model/                              (DTOs / response objects)
    │   │   └── exception/                          (custom exception + global handler)
    │   └── resources/
    │       ├── application.properties
    │       └── static/index.html                   (demo UI)
    └── test/java/.../CurrencyServiceTest.java
```

This follows a standard **layered architecture**:

```
Browser / Postman
      │
      ▼
Controller  (CurrencyController)   -> handles HTTP requests/responses
      │
      ▼
Service     (CurrencyService)      -> business logic, validation, caching
      │
      ▼
External API (open.er-api.com)     -> live exchange rate data
```

## 3. Prerequisites

- JDK 17 or newer
- Maven 3.8+ (or use an IDE like IntelliJ / Eclipse / VS Code with Maven support)
- Internet connection (the API fetches live rates)

## 4. How to Run

### Option A: Command line
```bash
cd currency-converter-api
mvn spring-boot:run
```

### Option B: IDE
Import as a Maven project in IntelliJ IDEA / Eclipse, then run
`CurrencyConverterApplication.java` directly.

### Option C: Build a runnable JAR
```bash
mvn clean package
java -jar target/currency-converter-api.jar
```

Once running, open **http://localhost:8080** in a browser for the demo UI,
or call the endpoints directly (examples below).

## 5. API Examples

**Convert 100 USD to INR**
```
GET http://localhost:8080/api/convert?from=USD&to=INR&amount=100
```
Response:
```json
{
  "from": "USD",
  "to": "INR",
  "amount": 100,
  "rate": 83.45,
  "convertedAmount": 8345.0000,
  "timestamp": "2026-09-01T10:15:30"
}
```

**Get the exchange rate only**
```
GET http://localhost:8080/api/rate?from=EUR&to=JPY
```

**List supported currencies**
```
GET http://localhost:8080/api/currencies
```

**Get full rate table for a base currency**
```
GET http://localhost:8080/api/rates?base=GBP
```

**Error example** (invalid currency code):
```json
{
  "status": 400,
  "message": "Unknown target currency code: XYZ",
  "timestamp": "2026-09-01T10:16:02"
}
```

## 6. Running Tests
```bash
mvn test
```
`CurrencyServiceTest` mocks the external API call so tests run offline
and deterministically.

## 7. Possible Extensions (for a "future scope" section in your report)

- Persist conversion history to a database (e.g. MySQL + Spring Data JPA)
- Add user accounts and saved favorite currency pairs
- Add a scheduled job to pre-fetch and cache rates every few minutes
- Support historical exchange rates (rate on a specific past date)
- Deploy to a cloud platform (Render, Railway, AWS) and add a proper frontend (React)
- Add rate-limiting/throttling on the API for production use

## 8. Notes for Your Report / Viva

- **Why Spring Boot?** It's the industry-standard Java framework for
  building REST APIs quickly, with built-in dependency injection,
  embedded server (Tomcat), and JSON handling via Jackson.
- **Why an external exchange rate API instead of hardcoded rates?**
  Real exchange rates fluctuate constantly; fetching live data makes
  the project realistic and demonstrates API-to-API integration
  (your Java backend consuming a third-party REST API).
- **Why caching?** Avoids hitting the external API on every single
  request, which is both faster and more considerate of the free
  provider's rate limits.
- **Layered architecture** (Controller → Service → external API) is a
  standard design pattern — it separates HTTP handling from business
  logic, which examiners will recognize.
