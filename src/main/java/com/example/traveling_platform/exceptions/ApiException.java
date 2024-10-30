package com.example.traveling_platform.exceptions;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public ApiException(String message, HttpStatusCode code) {
        super(message);
        this.statusCode = code;
    }
}