package com.sanhz.dmx.infrastructure.adapter.out.external;

import com.sanhz.dmx.domain.port.out.ExchangeRates;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FrankfurterExchangeRateAdapterTest {

    @Test
    void shouldRetrieveExchangeRatesSuccessfully() throws IOException {

        try (MockWebServer server = new MockWebServer()) {

            String mockResponse = """
                    {
                      "base": "MXN",
                      "date": "2024-05-16",
                      "rates": {
                        "USD": 0.05756,
                        "EUR": 0.04950
                      }
                    }
                    """;

            server.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(mockResponse)
            );

            server.start();

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

            factory.setConnectTimeout(3000);
            factory.setReadTimeout(3000);

            RestClient restClient = RestClient.builder()
                    .baseUrl(server.url("/").toString())
                    .requestFactory(factory)
                    .build();

            FrankfurterExchangeRateAdapter adapter = new FrankfurterExchangeRateAdapter(restClient);

            ExchangeRates exchangeRates = adapter.getRates();
            Map<String, BigDecimal> rates = exchangeRates.rates();

            assertNotNull(rates);
            assertFalse(rates.isEmpty());

            assertEquals(0, new BigDecimal("0.05756").compareTo(rates.get("USD")));

            assertEquals(0, new BigDecimal("0.04950").compareTo(rates.get("EUR")));
        }
    }

    @Test
    void shouldReturnEmptyMapWhenServiceUnavailable() throws IOException {

        try (MockWebServer server = new MockWebServer()) {

            server.enqueue(new MockResponse()
                    .setResponseCode(500)
                    .setBody("Service Unavailable")
            );

            server.start();

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

            factory.setConnectTimeout(3000);
            factory.setReadTimeout(3000);

            RestClient restClient = RestClient.builder()
                    .baseUrl(server.url("/").toString())
                    .requestFactory(factory)
                    .build();

            FrankfurterExchangeRateAdapter adapter = new FrankfurterExchangeRateAdapter(restClient);

            ExchangeRates exchangeRates = adapter.getRates();
            Map<String, BigDecimal> rates = exchangeRates.rates();

            assertNotNull(rates);
            assertTrue(rates.isEmpty());
        }
    }

    @Test
    void shouldReturnEmptyMapWhenConnectionTimeout() throws IOException {

        try (MockWebServer server = new MockWebServer()) {

            server.enqueue(new MockResponse()
                    .setSocketPolicy(SocketPolicy.NO_RESPONSE)
            );

            server.start();

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

            factory.setConnectTimeout(1000);
            factory.setReadTimeout(1000);

            RestClient restClient = RestClient.builder()
                    .baseUrl(server.url("/").toString())
                    .requestFactory(factory)
                    .build();

            FrankfurterExchangeRateAdapter adapter = new FrankfurterExchangeRateAdapter(restClient);

            ExchangeRates exchangeRates = adapter.getRates();
            Map<String, BigDecimal> rates = exchangeRates.rates();

            assertNotNull(rates);
            assertTrue(rates.isEmpty());
        }
    }
}