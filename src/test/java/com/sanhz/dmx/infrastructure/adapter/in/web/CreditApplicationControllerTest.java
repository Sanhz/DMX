package com.sanhz.dmx.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanhz.dmx.domain.enums.CreditStatus;
import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.domain.port.in.CreateCreditApplicationUseCase;
import com.sanhz.dmx.domain.port.in.GetCreditApplicationUseCase;
import com.sanhz.dmx.domain.port.in.ListCreditApplicationsUseCase;
import com.sanhz.dmx.domain.port.in.UpdateCreditApplicationStatusUseCase;
import com.sanhz.dmx.infrastructure.adapter.in.web.mapper.CreditApplicationWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreditApplicationController.class)
class CreditApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ MockitoBean
    private CreateCreditApplicationUseCase createUseCase;

    @ MockitoBean
    private ListCreditApplicationsUseCase listUseCase;

    @ MockitoBean
    private GetCreditApplicationUseCase getUseCase;

    @ MockitoBean
    private UpdateCreditApplicationStatusUseCase updateStatusUseCase;

    @ MockitoBean
    private CreditApplicationWebMapper mapper;

    @Test
    void shouldCreateCreditApplicationSuccessfully() throws Exception {

        CreditApplication application =
                CreditApplication.builder()
                        .id(UUID.randomUUID())
                        .status(CreditStatus.CREATED)
                        .monthlyPayment(new BigDecimal("7061.02"))
                        .totalToPay(new BigDecimal("169464.48"))
                        .amountUsd(new BigDecimal("8634.00"))
                        .amountEur(new BigDecimal("7425.00"))
                        .exchangeRateDate(LocalDate.now())
                        .build();

        when(createUseCase.create(any()))
                .thenReturn(application);

        when(mapper.toCreateResponse(any())).thenReturn(
                new com.sanhz.dmx.infrastructure.adapter.in.web.response.CreateCreditApplicationResponse(
                        application.getId(),
                        application.getStatus(),
                        application.getMonthlyPayment(),
                        application.getTotalToPay(),
                        application.getAmountUsd(),
                        application.getAmountEur(),
                        application.getExchangeRateDate()
                )
        );

        String request = """
                {
                  "customerName": "Hector Sanchez",
                  "customerEmail": "hector@test.com",
                  "customerRfc": "SAHH900101ABC",
                  "requestedAmount": 150000,
                  "currency": "MXN",
                  "termMonths": 24,
                  "annualInterestRate": 0.12
                }
                """;

        mockMvc.perform(post("/api/v1/credit-applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.monthlyPayment").value(7061.02))
                .andExpect(jsonPath("$.totalToPay").value(169464.48));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        String request = """
                {
                  "customerName": "",
                  "customerEmail": "invalid-email",
                  "customerRfc": "BADRFC",
                  "requestedAmount": 10,
                  "currency": "",
                  "termMonths": 1,
                  "annualInterestRate": 0.01
                }
                """;

        mockMvc.perform(post("/api/v1/credit-applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}