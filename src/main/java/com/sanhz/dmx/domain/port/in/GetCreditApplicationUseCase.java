package com.sanhz.dmx.domain.port.in;

import com.sanhz.dmx.domain.model.CreditApplication;

import java.util.UUID;

public interface GetCreditApplicationUseCase {
    CreditApplication getById(UUID id);
}
