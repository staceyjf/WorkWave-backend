package com.employee.workwave.exceptions;

import java.util.ArrayList;
import java.util.Map;

public class ServiceValidationException extends Exception {
    private ValidationErrors customErrors;

    public ServiceValidationException(ValidationErrors errors) {
        super("Service validation error occurred");
        this.customErrors = errors;
    }

    public Map<String, ArrayList<String>> getErrors() {
        return customErrors.getErrors();
    }

}
