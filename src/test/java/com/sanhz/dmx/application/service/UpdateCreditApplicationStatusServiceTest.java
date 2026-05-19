package com.sanhz.dmx.application.service;

import com.sanhz.dmx.domain.enums.CreditStatus;
import com.sanhz.dmx.domain.exception.CreditApplicationNotFoundException;
import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.domain.port.out.CreditApplicationRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateCreditApplicationStatusServiceTest {

    private CreditApplicationRepositoryPort repositoryPort;

    private UpdateCreditApplicationStatusService service;

    @BeforeEach
    void setUp() {

        repositoryPort = mock(CreditApplicationRepositoryPort.class);

        service = new UpdateCreditApplicationStatusService(repositoryPort);

    }

    @Test
    void shouldUpdateStatusSuccessfully() {

        UUID id = UUID.randomUUID();

        CreditApplication application =
                CreditApplication.builder()
                        .id(id)
                        .status(CreditStatus.CREATED)
                        .createdAt(LocalDateTime.now())
                        .build();

        when(repositoryPort.findById(id)).thenReturn(Optional.of(application));

        when(repositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CreditApplication result =
                service.updateStatus(
                        id,
                        CreditStatus.UNDER_REVIEW,
                        "Documents received"
                );

        assertEquals(CreditStatus.UNDER_REVIEW, result.getStatus());

        assertEquals("Documents received", result.getStatusReason());

        assertNotNull(result.getUpdatedAt());

        verify(repositoryPort, times(1)).save(any(CreditApplication.class));

    }

    @Test
    void shouldThrowExceptionWhenApplicationDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(repositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                CreditApplicationNotFoundException.class,
                () -> service.updateStatus(
                        id,
                        CreditStatus.APPROVED,
                        "Approved"
                )
        );

        verify(repositoryPort, never()).save(any());

    }
}