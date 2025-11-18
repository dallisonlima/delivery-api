package com.delivery_api.Projeto.Delivery.API.dto.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ValidationErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<String> errors;

    public ValidationErrorResponse(HttpStatus status, String message, String path, List<String> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
        this.errors = errors;
    }
}
