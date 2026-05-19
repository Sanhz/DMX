package com.sanhz.dmx.infrastructure.adapter.in.web;

import com.sanhz.dmx.domain.model.CreditApplication;
import com.sanhz.dmx.domain.model.CreditApplicationPage;
import com.sanhz.dmx.domain.port.in.CreateCreditApplicationUseCase;
import com.sanhz.dmx.domain.port.in.GetCreditApplicationUseCase;
import com.sanhz.dmx.domain.port.in.ListCreditApplicationsUseCase;
import com.sanhz.dmx.domain.port.in.UpdateCreditApplicationStatusUseCase;
import com.sanhz.dmx.infrastructure.adapter.in.web.mapper.CreditApplicationWebMapper;
import com.sanhz.dmx.infrastructure.adapter.in.web.request.CreateCreditApplicationRequest;
import com.sanhz.dmx.infrastructure.adapter.in.web.request.UpdateCreditApplicationStatusRequest;
import com.sanhz.dmx.infrastructure.adapter.in.web.response.CreateCreditApplicationResponse;
import com.sanhz.dmx.infrastructure.adapter.in.web.response.GetCreditApplicationResponse;
import com.sanhz.dmx.infrastructure.adapter.in.web.response.ListCreditApplicationsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.math.BigDecimal;
import com.sanhz.dmx.domain.enums.CreditStatus;

@RestController
@RequestMapping("/api/v1/credit-applications")
@RequiredArgsConstructor
public class CreditApplicationController {

    private final CreateCreditApplicationUseCase useCase;
    private final ListCreditApplicationsUseCase listUseCase;
    private final GetCreditApplicationUseCase getUseCase;
    private final UpdateCreditApplicationStatusUseCase updateStatusUseCase;
    private final CreditApplicationWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCreditApplicationResponse create(@Valid @RequestBody com.sanhz.dmx.domain.port.in.CreateCreditApplicationCommand command) {
        CreditApplication created = useCase.create(command);
        return mapper.toCreateResponse(created);
    }

    @GetMapping
    public ListCreditApplicationsResponse list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CreditStatus status,
            @RequestParam(required = false) String customerRfc,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {

        CreditApplicationPage result = listUseCase.list(page, size);

        return new ListCreditApplicationsResponse(
                result.content()
                        .stream()
                        .map(mapper::toResponse)
                        .toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    @GetMapping("/{id}")
    public GetCreditApplicationResponse getById(@PathVariable UUID id) {
        return mapper.toResponse(getUseCase.getById(id));
    }

    @PatchMapping("/{id}/status")
    public GetCreditApplicationResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateCreditApplicationStatusRequest request) {
        CreditApplication updated = updateStatusUseCase.updateStatus(id, request.status(), request.reason());
        return mapper.toResponse(updated);
    }

}
