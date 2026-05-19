package com.sanhz.dmx.infrastructure.adapter.in.web.response;

import java.time.LocalDateTime;

public record ApiErrorResponse(

        LocalDateTime timestamp,
        Integer status,
        String error,
        String code,
        String message,
        String path

) {
}