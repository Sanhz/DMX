package com.sanhz.dmx.domain.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Simple DTO returned by ExchangeRateProvider containing rates and the date.
 */
public record ExchangeRates(
        Map<String, BigDecimal> rates,
        LocalDate date
) {
}

