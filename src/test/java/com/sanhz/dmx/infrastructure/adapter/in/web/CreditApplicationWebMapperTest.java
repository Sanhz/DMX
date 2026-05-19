package com.sanhz.dmx.infrastructure.adapter.in.web;

import com.sanhz.dmx.domain.enums.CreditStatus;
import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.infrastructure.adapter.in.web.mapper.CreditApplicationWebMapper;
import com.sanhz.dmx.infrastructure.adapter.in.web.response.GetCreditApplicationResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreditApplicationWebMapperTest {

    private final CreditApplicationWebMapper mapper =
            new CreditApplicationWebMapper();

    @Test
    void shouldMapDomainToResponse() {

        UUID id = UUID.randomUUID();

        LocalDateTime createdAt =
                LocalDateTime.now();

        CreditApplication application =
                CreditApplication.builder()
                        .id(id)
                        .customerName("Hector Sanchez")
                        .customerEmail("hector@test.com")
                        .customerRfc("SAHH900101ABC")
                        .requestedAmount(BigDecimal.valueOf(150000))
                        .currency("MXN")
                        .termMonths(24)
                        .annualInterestRate(BigDecimal.valueOf(0.12))
                        .monthlyPayment(BigDecimal.valueOf(7061.02))
                        .totalToPay(BigDecimal.valueOf(169464.48))
                        .amountUsd(BigDecimal.valueOf(8634))
                        .amountEur(BigDecimal.valueOf(7425))
                        .exchangeRateDate(LocalDate.now())
                        .status(CreditStatus.CREATED)
                        .statusReason("Created successfully")
                        .createdAt(createdAt)
                        .build();

        GetCreditApplicationResponse response = mapper.toResponse(application);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Hector Sanchez", response.customerName());
        assertEquals("hector@test.com", response.customerEmail());
        assertEquals(CreditStatus.CREATED, response.status());
        assertEquals(createdAt, response.createdAt());
    }
}
