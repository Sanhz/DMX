package com.sanhz.dmx.domain.model;

import lombok.Generated;

import java.util.List;

@Generated
public record CreditApplicationPage(

        List<CreditApplication> content,
        int page,
        int size,
        long totalElements,
        int totalPages

) {
}