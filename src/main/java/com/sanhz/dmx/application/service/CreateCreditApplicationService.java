package com.sanhz.dmx.application.service;

import com.sanhz.dmx.domain.enums.CreditStatus;
import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.domain.port.in.CreateCreditApplicationCommand;
import com.sanhz.dmx.domain.port.in.CreateCreditApplicationUseCase;
import com.sanhz.dmx.domain.port.out.CreditApplicationRepositoryPort;
import com.sanhz.dmx.domain.port.out.ExchangeRateProvider;
import com.sanhz.dmx.domain.service.CreditCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.sanhz.dmx.domain.port.out.ExchangeRates;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCreditApplicationService implements CreateCreditApplicationUseCase {

    private final CreditApplicationRepositoryPort repositoryPort;

    private final ExchangeRateProvider exchangeRateProvider;

    @Override
    public CreditApplication create(CreateCreditApplicationCommand command) {

        CreditCalculatorService calculator = new CreditCalculatorService();

        CreditApplication application = CreditApplication.builder()
                .customerName(command.customerName())
                .customerEmail(command.customerEmail())
                .customerRfc(command.customerRfc())
                .requestedAmount(command.requestedAmount())
                .currency(command.currency())
                .termMonths(command.termMonths())
                .annualInterestRate(command.annualInterestRate())
                .build();

        application.setId(UUID.randomUUID());
        application.setStatus(CreditStatus.CREATED);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        BigDecimal monthlyPayment = calculator.calculateMonthlyPayment(application.getRequestedAmount(), application.getAnnualInterestRate(), application.getTermMonths());

        application.setMonthlyPayment(monthlyPayment);

        application.setTotalToPay(calculator.calculateTotalToPay(monthlyPayment, application.getTermMonths()));

        try {

            ExchangeRates exchangeRates = exchangeRateProvider.getRates();

            if (exchangeRates != null && exchangeRates.rates() != null) {

                BigDecimal usdRate = exchangeRates.rates().get("USD");
                BigDecimal eurRate = exchangeRates.rates().get("EUR");

                if (usdRate != null) {
                    application.setAmountUsd(application.getRequestedAmount().multiply(usdRate));
                }

                if (eurRate != null) {
                    application.setAmountEur(application.getRequestedAmount().multiply(eurRate));
                }

                application.setExchangeRateDate(exchangeRates.date());
            }

        } catch (Exception ex) {
            log.warn(
                    "Exchange rate service unavailable: {}",
                    ex.getMessage()
            );
        }

        return repositoryPort.save(application);

    }

}
