package com.sanhz.dmx.application.service;


import com.sanhz.dmx.domain.exception.CreditApplicationNotFoundException;
import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.domain.port.in.GetCreditApplicationUseCase;
import com.sanhz.dmx.domain.port.out.CreditApplicationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCreditApplicationService implements GetCreditApplicationUseCase {

    private final CreditApplicationRepositoryPort repositoryPort;

    @Override
    public CreditApplication getById(UUID id) {
        return repositoryPort.findById(id)
                .orElseThrow(() ->
                        new CreditApplicationNotFoundException(
                                "Credit application not found: " + id
                        )
                );
    }
}
