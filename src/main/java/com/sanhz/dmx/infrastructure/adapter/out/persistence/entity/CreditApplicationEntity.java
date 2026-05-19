package com.sanhz.dmx.infrastructure.adapter.out.persistence.entity;

import com.sanhz.dmx.domain.enums.CreditStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_applications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditApplicationEntity {

  @Id  private UUID id;
  @Column(name = "customer_name")
  private String customerName;
  @Column(name = "customer_email")
  private String customerEmail;
  @Column(name = "customer_rfc")
  private String customerRfc;
  @Column(name = "requested_amount", nullable = false, precision = 15, scale = 2)
  private BigDecimal requestedAmount;
  private String currency;
  @Column(name = "term_months")
  private Integer termMonths;
  @Column(name = "annual_interest_rate", nullable = false, precision = 5, scale = 4)
  private BigDecimal annualInterestRate;
  @Column(name = "monthly_payment", nullable = false, precision = 15, scale = 2)
  private BigDecimal monthlyPayment;
  @Column(name = "total_to_pay", nullable = false, precision = 15, scale = 2)
  private BigDecimal totalToPay;
  @Column(name = "amount_usd", nullable = true, precision = 15, scale = 2)
  private BigDecimal amountUsd;
  @Column(name = "amount_eur", nullable = true, precision = 15, scale = 2)
  private BigDecimal amountEur;
  @Column(name = "exchange_rate_date")
  private LocalDate exchangeRateDate;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CreditStatus status;
  @Column(name = "status_reason")
  private String statusReason;
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;


}
