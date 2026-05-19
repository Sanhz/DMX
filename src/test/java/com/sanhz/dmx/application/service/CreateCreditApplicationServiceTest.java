package com.sanhz.dmx.application.service;

import com.sanhz.dmx.domain.enums.CreditStatus;
import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.domain.port.in.CreateCreditApplicationCommand;
import com.sanhz.dmx.domain.port.out.CreditApplicationRepositoryPort;
import com.sanhz.dmx.domain.port.out.ExchangeRateProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.time.LocalDate;
import com.sanhz.dmx.domain.port.out.ExchangeRates;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateCreditApplicationServiceTest {

    private CreditApplicationRepositoryPort repositoryPort;

    private ExchangeRateProvider exchangeRateProvider;

    private CreateCreditApplicationService service;

    @BeforeEach
    void setUp() {

        repositoryPort = mock(CreditApplicationRepositoryPort.class);

        exchangeRateProvider = mock(ExchangeRateProvider.class);

        service = new CreateCreditApplicationService(repositoryPort, exchangeRateProvider);

    }

    @Test
    void shouldCreateCreditApplicationSuccessfully() {

        CreateCreditApplicationCommand application = new CreateCreditApplicationCommand(
                "Hector Sanchez",
                "hector@test.com",
                "SAHH900101ABC",
                new BigDecimal("150000"),
                "MXN",
                24,
                new BigDecimal("0.12")
        );

        when(exchangeRateProvider.getRates())
                .thenReturn(
                        new ExchangeRates(
                                Map.of(
                                        "USD", new BigDecimal("0.05756"),
                                        "EUR", new BigDecimal("0.04950")
                                ),
                                LocalDate.now()
                        )
                );

        when(repositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CreditApplication result = service.create(application);
        assertNotNull(result.getId());
        assertEquals(CreditStatus.CREATED, result.getStatus());
        assertNotNull(result.getMonthlyPayment());
        assertNotNull(result.getTotalToPay());
        assertNotNull(result.getAmountUsd());
        assertNotNull(result.getAmountEur());
        verify(repositoryPort, times(1)).save(any(CreditApplication.class));

    }

    @Test
    void shouldContinueWhenExchangeServiceFails() {

        CreateCreditApplicationCommand application = new CreateCreditApplicationCommand(
                "Hector Sanchez",
                "hector@test.com",
                "SAHH900101ABC",
                new BigDecimal("150000"),
                "MXN",
                24,
                new BigDecimal("0.12")
        );

        when(exchangeRateProvider.getRates())
                .thenThrow(
                        new RuntimeException("Service unavailable")
                );

        when(repositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CreditApplication result = service.create(application);
        assertNotNull(result.getId());
        assertEquals(CreditStatus.CREATED, result.getStatus());
        assertNull(result.getAmountUsd());
        assertNull(result.getAmountEur());
        verify(repositoryPort, times(1)).save(any(CreditApplication.class));

    }
}