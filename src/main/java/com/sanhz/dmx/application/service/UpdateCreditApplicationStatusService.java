package com.sanhz.dmx.application.service;

import com.sanhz.dmx.domain.enums.CreditStatus;
import com.sanhz.dmx.domain.exception.CreditApplicationNotFoundException;
import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.domain.port.in.UpdateCreditApplicationStatusUseCase;
import com.sanhz.dmx.domain.port.out.CreditApplicationRepositoryPort;
import com.sanhz.dmx.domain.service.CreditStatusTransitionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCreditApplicationStatusService implements UpdateCreditApplicationStatusUseCase {

    private final CreditApplicationRepositoryPort repositoryPort;

    @Override
    public CreditApplication updateStatus(
            UUID id,
            CreditStatus status,
            String reason
    ) {

        CreditApplication application =
                repositoryPort.findById(id)
                        .orElseThrow(() ->
                                new CreditApplicationNotFoundException(
                                        "Credit application not found: " + id
                                )
                        );

        CreditStatusTransitionValidator validator =
                new CreditStatusTransitionValidator();

        validator.validate(
                application.getStatus(),
                status
        );

        application.setStatus(status);

        application.setStatusReason(reason);

        application.setUpdatedAt(LocalDateTime.now());

        return repositoryPort.save(application);
    }

}
