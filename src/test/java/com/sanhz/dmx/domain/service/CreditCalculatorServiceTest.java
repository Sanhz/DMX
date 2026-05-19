package com.sanhz.dmx.domain.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditCalculatorServiceTest {

    private final CreditCalculatorService service = new CreditCalculatorService();

    @Test
    void shouldCalculateMonthlyPaymentCorrectly() {

        BigDecimal result = service.calculateMonthlyPayment(new BigDecimal("150000"), new BigDecimal("0.12"), 24);

        assertEquals(new BigDecimal("7061.02"), result.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void shouldCalculateTotalToPayCorrectly() {

        BigDecimal result = service.calculateTotalToPay(new BigDecimal("7061.02"), 24);

        assertEquals(new BigDecimal("169464.48"), result.setScale(2, RoundingMode.HALF_UP));
    }
}
