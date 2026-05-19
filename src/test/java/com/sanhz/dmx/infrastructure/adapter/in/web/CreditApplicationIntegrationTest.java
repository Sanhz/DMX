package com.sanhz.dmx.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class CreditApplicationIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("dmxdb_test")
            .withUsername("dmx")
            .withPassword("dmx");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCreditApplicationAndRetrieveIt() throws Exception {

        String createRequest = """
                {
                  "customerName": "Hector Sanchez",
                  "customerEmail": "hector@test.com",
                  "customerRfc": "SAHH900101ABC",
                  "requestedAmount": 150000,
                  "currency": "MXN",
                  "termMonths": 24,
                  "annualInterestRate": 0.12
                }
                """;

        var createResult = mockMvc.perform(post("/api/v1/credit-applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createRequest)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.monthlyPayment").isNotEmpty())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String applicationId = objectMapper.readTree(responseBody).get("id").asText();

        mockMvc.perform(get("/api/v1/credit-applications/{id}", applicationId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(applicationId))
                .andExpect(jsonPath("$.customerName").value("Hector Sanchez"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void shouldListCreditApplicationsWithPagination() throws Exception {

        mockMvc.perform(get("/api/v1/credit-applications?page=0&size=10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", isA(java.util.List.class)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").isNotEmpty());
    }

    @Test
    void shouldUpdateCreditApplicationStatus() throws Exception {

        String createRequest = """
                {
                  "customerName": "Juan Perez",
                  "customerEmail": "juan@test.com",
                  "customerRfc": "PEJJ900101ABC",
                  "requestedAmount": 200000,
                  "currency": "MXN",
                  "termMonths": 36,
                  "annualInterestRate": 0.15
                }
                """;

        var createResult = mockMvc.perform(post("/api/v1/credit-applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createRequest)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String applicationId = objectMapper.readTree(responseBody).get("id").asText();

        String updateRequest = """
                {
                  "status": "UNDER_REVIEW",
                  "reason": "Documents received"
                }
                """;

        mockMvc.perform(patch("/api/v1/credit-applications/{id}/status", applicationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNDER_REVIEW"))
                .andExpect(jsonPath("$.statusReason").value("Documents received"));
    }

    @Test
    void shouldReturnNotFoundWhenApplicationDoesNotExist() throws Exception {

        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/credit-applications/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CREDIT_APPLICATION_NOT_FOUND"));
    }

    @Test
    void shouldRejectInvalidStatusTransition() throws Exception {

        String createRequest = """
                {
                  "customerName": "Maria Lopez",
                  "customerEmail": "maria@test.com",
                  "customerRfc": "LOMM900101ABC",
                  "requestedAmount": 100000,
                  "currency": "MXN",
                  "termMonths": 12,
                  "annualInterestRate": 0.10
                }
                """;

        var createResult = mockMvc.perform(
                        post("/api/v1/credit-applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createRequest)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String applicationId = objectMapper.readTree(responseBody).get("id").asText();

        String invalidUpdateRequest = """
                {
                  "status": "APPROVED",
                  "reason": "Invalid transition"
                }
                """;

        mockMvc.perform(
                        patch("/api/v1/credit-applications/{id}/status", applicationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidUpdateRequest)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_STATUS_TRANSITION"));
    }
}