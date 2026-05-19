package com.sanhz.dmx.domain.port.in;

import com.sanhz.dmx.domain.model.CreditApplication;

/**
 * Use case input port for creating credit applications. Receives a command (DTO)
 * and returns the created domain model.
 */
public interface CreateCreditApplicationUseCase {

    CreditApplication create(CreateCreditApplicationCommand command);

}
