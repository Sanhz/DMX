package com.sanhz.dmx.domain.model;

import com.sanhz.dmx.domain.enums.CreditStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditApplication {  private UUID id;
  private String customerName;
  private String customerEmail;
  private String customerRfc;
  private BigDecimal requestedAmount;
  private String currency;
  private Integer termMonths;
  private BigDecimal annualInterestRate;
  private BigDecimal monthlyPayment;
  private BigDecimal totalToPay;
  private BigDecimal amountUsd;
  private BigDecimal amountEur;
  private LocalDate exchangeRateDate;
  private CreditStatus status;
  private String statusReason;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
