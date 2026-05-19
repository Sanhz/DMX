package com.sanhz.dmx.domain.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CreditCalculatorService {

    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    public BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal annualRate, Integer months) {

        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), MATH_CONTEXT);

        double denominator = Math.pow(BigDecimal.ONE.add(monthlyRate).doubleValue(), -months);

        BigDecimal numerator = principal.multiply(monthlyRate);

        BigDecimal divisor = BigDecimal.ONE.subtract(BigDecimal.valueOf(denominator));

        return numerator.divide(divisor, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalToPay(BigDecimal monthlyPayment, Integer months) {
        return monthlyPayment.multiply(BigDecimal.valueOf(months));
    }

}
