package com.sanhz.dmx.infrastructure.adapter.out.persistence;

import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.domain.model.CreditApplicationPage;
import com.sanhz.dmx.domain.port.out.CreditApplicationRepositoryPort;
import com.sanhz.dmx.infrastructure.adapter.out.persistence.entity.CreditApplicationEntity;
import com.sanhz.dmx.infrastructure.adapter.out.persistence.repository.SpringDataCreditApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreditApplicationPersistenceAdapter implements CreditApplicationRepositoryPort {

    private final SpringDataCreditApplicationRepository repository;

    @Override
    public CreditApplication save(CreditApplication application) {

        CreditApplicationEntity entity =
                CreditApplicationEntity.builder()
                        .id(application.getId())
                        .customerName(application.getCustomerName())
                        .customerEmail(application.getCustomerEmail())
                        .customerRfc(application.getCustomerRfc())
                        .requestedAmount(application.getRequestedAmount())
                        .currency(application.getCurrency())
                        .termMonths(application.getTermMonths())
                        .annualInterestRate(application.getAnnualInterestRate())
                        .monthlyPayment(application.getMonthlyPayment())
                        .totalToPay(application.getTotalToPay())
                        .amountUsd(application.getAmountUsd())
                        .amountEur(application.getAmountEur())
                        .exchangeRateDate(application.getExchangeRateDate())
                        .status(application.getStatus())
                        .statusReason(application.getStatusReason())
                        .createdAt(application.getCreatedAt())
                        .updatedAt(application.getUpdatedAt())
                        .build();

        CreditApplicationEntity saved = repository.save(entity);

        return mapToDomain(saved);

    }

    @Override
    public Optional<CreditApplication> findById(UUID id) {

        return repository.findById(id).map(this::mapToDomain);

    }

    private CreditApplication mapToDomain(CreditApplicationEntity entity) {

        return CreditApplication.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .customerEmail(entity.getCustomerEmail())
                .customerRfc(entity.getCustomerRfc())
                .requestedAmount(entity.getRequestedAmount())
                .currency(entity.getCurrency())
                .termMonths(entity.getTermMonths())
                .annualInterestRate(entity.getAnnualInterestRate())
                .monthlyPayment(entity.getMonthlyPayment())
                .totalToPay(entity.getTotalToPay())
                .amountUsd(entity.getAmountUsd())
                .amountEur(entity.getAmountEur())
                .exchangeRateDate(entity.getExchangeRateDate())
                .status(entity.getStatus())
                .statusReason(entity.getStatusReason())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

    }

    @Override
    public CreditApplicationPage findAll(int page, int size) {

        Page<CreditApplicationEntity> result = repository.findAll(PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending()));

        return new CreditApplicationPage(
                result.getContent()
                        .stream()
                        .map(this::toDomain)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private CreditApplication toDomain(CreditApplicationEntity entity) {

        CreditApplication application = new CreditApplication();

        application.setId(entity.getId());
        application.setCustomerName(entity.getCustomerName());
        application.setCustomerEmail(entity.getCustomerEmail());
        application.setCustomerRfc(entity.getCustomerRfc());
        application.setRequestedAmount(entity.getRequestedAmount());
        application.setCurrency(entity.getCurrency());
        application.setTermMonths(entity.getTermMonths());
        application.setAnnualInterestRate(entity.getAnnualInterestRate());
        application.setMonthlyPayment(entity.getMonthlyPayment());
        application.setTotalToPay(entity.getTotalToPay());
        application.setAmountUsd(entity.getAmountUsd());
        application.setAmountEur(entity.getAmountEur());
        application.setExchangeRateDate(entity.getExchangeRateDate());
        application.setStatus(entity.getStatus());
        application.setStatusReason(entity.getStatusReason());
        application.setCreatedAt(entity.getCreatedAt());
        application.setUpdatedAt(entity.getUpdatedAt());
        return application;
        
    }

}
