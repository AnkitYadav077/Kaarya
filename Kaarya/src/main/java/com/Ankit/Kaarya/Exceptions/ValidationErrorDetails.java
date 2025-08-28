package com.Ankit.Kaarya.Exceptions;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
class ValidationErrorDetails extends ErrorDetails {
    private Map<String, String> validationErrors;

    public ValidationErrorDetails(LocalDateTime timestamp, String message,
                                  String details, String errorCode,
                                  Map<String, String> validationErrors) {
        super(timestamp, message, details, errorCode);
        this.validationErrors = validationErrors;
    }
}