package com.sanhz.dmx.application.service;

import com.sanhz.dmx.domain.exception.CreditApplicationNotFoundException;
import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.domain.port.out.CreditApplicationRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetCreditApplicationServiceTest {

    private CreditApplicationRepositoryPort repositoryPort;

    private GetCreditApplicationService service;

    @BeforeEach
    void setUp() {

        repositoryPort = mock(CreditApplicationRepositoryPort.class);

        service = new GetCreditApplicationService(repositoryPort);

    }

    @Test
    void shouldReturnApplicationById() {

        UUID id = UUID.randomUUID();

        CreditApplication application =
                CreditApplication.builder()
                        .id(id)
                        .customerName("Hector")
                        .build();

        when(repositoryPort.findById(id)).thenReturn(Optional.of(application));

        CreditApplication result = service.getById(id);

        assertNotNull(result);

        assertEquals(id, result.getId());

        assertEquals("Hector", result.getCustomerName());

    }

    @Test
    void shouldThrowExceptionWhenApplicationNotExists() {

        UUID id = UUID.randomUUID();

        when(repositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                CreditApplicationNotFoundException.class,
                () -> service.getById(id)
        );
    }
}