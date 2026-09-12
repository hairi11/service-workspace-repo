package com.company.service.api.dto;

public class FxReferenceDto {

    private String type;
    private String code;
    private String description;

    public FxReferenceDto() {
    }

    public FxReferenceDto(String type, String code, String description) {
        this.type = type;
        this.code = code;
        this.description = description;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
