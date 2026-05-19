package com.sanhz.dmx.application.service;

import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.domain.model.CreditApplicationPage;
import com.sanhz.dmx.domain.port.out.CreditApplicationRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListCreditApplicationsServiceTest {

    private CreditApplicationRepositoryPort repositoryPort;

    private ListCreditApplicationsService service;

    @BeforeEach
    void setUp() {

        repositoryPort = mock(CreditApplicationRepositoryPort.class);

        service = new ListCreditApplicationsService(repositoryPort);

    }

    @Test
    void shouldReturnPaginatedApplications() {

        CreditApplication application =
                CreditApplication.builder()
                        .customerName("Hector")
                        .build();

        CreditApplicationPage page =
                new CreditApplicationPage(
                        List.of(application),
                        0,
                        10,
                        1,
                        1
                );

        when(repositoryPort.findAll(0, 10)).thenReturn(page);

        CreditApplicationPage result = service.list(0, 10);

        assertNotNull(result);

        assertEquals(1, result.content().size());

        assertEquals("Hector", result.content().getFirst().getCustomerName());

    }
}