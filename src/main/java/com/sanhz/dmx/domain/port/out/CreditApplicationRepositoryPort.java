package com.sanhz.dmx.domain.port.out;

import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.domain.model.CreditApplicationPage;

import java.util.Optional;
import java.util.UUID;

public interface CreditApplicationRepositoryPort {

    CreditApplication save(CreditApplication application);

    Optional<CreditApplication> findById(UUID id);

    CreditApplicationPage findAll(int page, int size);

}
