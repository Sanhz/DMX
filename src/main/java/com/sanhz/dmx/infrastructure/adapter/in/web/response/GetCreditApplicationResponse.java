package com.sanhz.dmx.infrastructure.adapter.in.web.response;

import com.sanhz.dmx.domain.enums.CreditStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record GetCreditApplicationResponse (

       UUID id,
       String customerName,
       String customerEmail,
       String customerRfc,
       BigDecimal requestedAmount,
       String currency,
       Integer termMonths,
       BigDecimal annualInterestRate,
       BigDecimal monthlyPayment,
       BigDecimal totalToPay,
       BigDecimal amountUsd,
       BigDecimal amountEur,
       LocalDate exchangeRateDate,
       CreditStatus status,
       String statusReason,
       LocalDateTime createdAt

) {
}
