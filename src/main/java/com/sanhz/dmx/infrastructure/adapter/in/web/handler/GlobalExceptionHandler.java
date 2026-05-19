package com.sanhz.dmx.infrastructure.adapter.in.web.handler;

import com.sanhz.dmx.domain.exception.CreditApplicationNotFoundException;
import com.sanhz.dmx.domain.exception.InvalidStatusTransitionException;
import com.sanhz.dmx.infrastructure.adapter.in.web.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        String message =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(this::formatFieldError)
                        .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request);

    }

    @ExceptionHandler(CreditApplicationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundException(CreditApplicationNotFoundException ex, HttpServletRequest request) {

        return buildResponse(HttpStatus.NOT_FOUND, "CREDIT_APPLICATION_NOT_FOUND", ex.getMessage(), request);

    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleInvalidTransition(InvalidStatusTransitionException ex, HttpServletRequest request) {

        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_STATUS_TRANSITION", ex.getMessage(), request);

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleGenericException(Exception ex, HttpServletRequest request) {

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex.getMessage(), request);

    }

    private ApiErrorResponse buildResponse(HttpStatus status, String code, String message, HttpServletRequest request) {

        return new ApiErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), code, message, request.getRequestURI());

    }

    private String formatFieldError(FieldError error) {

        return error.getField() + ": " + error.getDefaultMessage();

    }

}