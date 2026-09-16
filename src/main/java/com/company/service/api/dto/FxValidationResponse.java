package com.company.service.api.dto;

public class FxValidationResponse {

    private final boolean valid;
    private final String message;

    public FxValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}
