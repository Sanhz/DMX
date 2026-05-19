package com.sanhz.dmx.infrastructure.adapter.in.web.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCreditApplicationRequest(

        @NotBlank(message = "Customer name is required")
        @Size(min = 3, max = 120, message = "Customer name must be between 3 and 120 characters")
        String customerName,

        @NotBlank(message = "Customer email is required")
        @Email(message = "Customer email format is invalid")
        @Size(max = 160, message = "Customer email must not exceed 160 characters")
        String customerEmail,

        @Pattern(regexp = "^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$", message = "Customer RFC format is invalid")
        String customerRfc,

        @NotNull(message = "Requested amount is required")
        @DecimalMin(value = "0.01", message = "Requested amount must be greater than 0")
        @DecimalMax(value = "5000000.00", message = "Requested amount must be less than or equal to 5000000.00")
        BigDecimal requestedAmount,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^MXN$", message = "Currency must be MXN")
        String currency,

        @NotNull(message = "Term months is required")
        @Min(value = 6, message = "Term months must be at least 6")
        @Max(value = 60, message = "Term months must not exceed 60")
        Integer termMonths,

        @NotNull(message = "Annual interest rate is required")
        @DecimalMin(value = "0.05", message = "Annual interest rate must be at least 0.05")
        @DecimalMax(value = "0.60", message = "Annual interest rate must not exceed 0.60")
        BigDecimal annualInterestRate

) {
}