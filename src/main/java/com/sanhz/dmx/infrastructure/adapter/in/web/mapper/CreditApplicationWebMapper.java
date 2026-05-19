package com.sanhz.dmx.infrastructure.adapter.in.web.mapper;

import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.infrastructure.adapter.in.web.request.CreateCreditApplicationRequest;
import com.sanhz.dmx.infrastructure.adapter.in.web.response.GetCreditApplicationResponse;
import org.springframework.stereotype.Component;

@Component
public class CreditApplicationWebMapper {

    public CreditApplication toDomainModel(CreateCreditApplicationRequest request) {
        return CreditApplication.builder()
                .customerName(request.customerName())
                .customerEmail(request.customerEmail())
                .customerRfc(request.customerRfc())
                .requestedAmount(request.requestedAmount())
                .currency(request.currency())
                .termMonths(request.termMonths())
                .annualInterestRate(request.annualInterestRate())
                .build();
    }

    public GetCreditApplicationResponse toResponse(CreditApplication application) {
        return new GetCreditApplicationResponse(
                application.getId(),
                application.getCustomerName(),
                application.getCustomerEmail(),
                application.getCustomerRfc(),
                application.getRequestedAmount(),
                application.getCurrency(),
                application.getTermMonths(),
                application.getAnnualInterestRate(),
                application.getMonthlyPayment(),
                application.getTotalToPay(),
                application.getAmountUsd(),
                application.getAmountEur(),
                application.getExchangeRateDate(),
                application.getStatus(),
                application.getStatusReason(),
                application.getCreatedAt()
        );
    }

    public com.sanhz.dmx.infrastructure.adapter.in.web.response.CreateCreditApplicationResponse toCreateResponse(CreditApplication created) {
        return new com.sanhz.dmx.infrastructure.adapter.in.web.response.CreateCreditApplicationResponse(
                created.getId(),
                created.getStatus(),
                created.getMonthlyPayment(),
                created.getTotalToPay(),
                created.getAmountUsd(),
                created.getAmountEur(),
                created.getExchangeRateDate()
        );
    }
}