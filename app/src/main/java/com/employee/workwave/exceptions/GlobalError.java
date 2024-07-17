package com.employee.workwave.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

public class GlobalError {
    private HttpStatus status;
    private LocalDateTime timestamp;
    private Map<String, Object> errorMessages;
    private String exceptionClass;
    private String debugMessage;

    public GlobalError(HttpStatus status, Throwable ex) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.errorMessages = new HashMap<>();
        this.exceptionClass = ex.getClass().getSimpleName();
        this.debugMessage = ex.getLocalizedMessage();
    }

    // return the debug message for the logger
    public HttpStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public Map<String, Object> getErrorMessages() {
        return this.errorMessages;
    }

    public String getExceptionClass() {
        return this.exceptionClass;
    }

    public String getDebugMessage() {
        return this.debugMessage;
    }

    public void addErrorMessage(String field, Object errorMessage) {
        this.errorMessages.put(field, errorMessage);
    }
}
