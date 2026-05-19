package com.sanhz.dmx.domain.port.in;

import com.sanhz.dmx.domain.enums.CreditStatus;
import com.sanhz.dmx.domain.model.CreditApplication;

import java.util.UUID;

public interface UpdateCreditApplicationStatusUseCase {

    CreditApplication updateStatus(UUID id, CreditStatus status, String reason);

}
