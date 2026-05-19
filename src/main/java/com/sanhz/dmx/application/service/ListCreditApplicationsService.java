package com.sanhz.dmx.application.service;

import com.sanhz.dmx.domain.model.CreditApplicationPage;
import com.sanhz.dmx.domain.port.in.ListCreditApplicationsUseCase;
import com.sanhz.dmx.domain.port.out.CreditApplicationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListCreditApplicationsService
        implements ListCreditApplicationsUseCase {

    private final CreditApplicationRepositoryPort repositoryPort;

    @Override
    public CreditApplicationPage list(int page, int size) {

        return repositoryPort.findAll(page, size);

    }
}