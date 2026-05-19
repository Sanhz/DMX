package com.sanhz.dmx.infrastructure.adapter.in.web.request;

import com.sanhz.dmx.domain.enums.CreditStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCreditApplicationStatusRequest (

        @NotNull
        CreditStatus status,
        String reason

) {
}
