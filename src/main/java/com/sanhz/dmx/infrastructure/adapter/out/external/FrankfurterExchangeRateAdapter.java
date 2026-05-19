package com.sanhz.dmx.infrastructure.adapter.out.external;

import com.sanhz.dmx.domain.port.out.ExchangeRateProvider;
import com.sanhz.dmx.domain.port.out.ExchangeRates;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class FrankfurterExchangeRateAdapter
        implements ExchangeRateProvider {

    private final RestClient restClient;

    @Override
    @Cacheable("exchangeRates")
    @Retry(name = "frankfurter")
    @CircuitBreaker(name = "frankfurter", fallbackMethod = "fallbackRates")
    public ExchangeRates getRates() {

        try {
            FrankfurterResponse response = restClient.get()
                    .uri("/latest?base=MXN&symbols=USD,EUR")
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            (httpRequest, httpResponse) -> {
                                throw new RuntimeException("Frankfurter API error");
                            }
                    )
                    .body(FrankfurterResponse.class);

            if (response == null || response.rates() == null) {
                return new ExchangeRates(Collections.emptyMap(), null);
            }

            return new ExchangeRates(response.rates(), response.date());

        } catch (Exception ex) {
            log.warn("Frankfurter error: {}", ex.getMessage());
            return new ExchangeRates(Collections.emptyMap(), null);
        }
    }

    public ExchangeRates fallbackRates(Throwable ex) {

        log.warn("Frankfurter unavailable. Using fallback. Cause: {}", ex.getMessage());

        return new ExchangeRates(Collections.emptyMap(), null);
    }
}