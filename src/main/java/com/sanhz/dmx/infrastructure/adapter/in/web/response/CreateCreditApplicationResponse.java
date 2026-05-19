package com.sanhz.dmx.infrastructure.adapter.in.web.response;

import com.sanhz.dmx.domain.enums.CreditStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateCreditApplicationResponse(

        UUID id,
        CreditStatus status,
        BigDecimal monthlyPayment,
        BigDecimal totalToPay,
        BigDecimal amountUsd,
        BigDecimal amountEur,
        LocalDate exchangeRateDate

) {
}
