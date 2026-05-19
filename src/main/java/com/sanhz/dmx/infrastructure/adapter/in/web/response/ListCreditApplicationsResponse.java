package com.sanhz.dmx.infrastructure.adapter.in.web.response;

import java.util.List;

public record ListCreditApplicationsResponse(

        List<GetCreditApplicationResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages

) {
}
