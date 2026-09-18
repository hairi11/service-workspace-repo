package com.company.service.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class FxEnquiryDto {

    private Long id;
    private Long masterId;
    private LocalDate reportDate;
    private Integer recordNo;
    private String fxCategory;
    private String fxCategoryDescription;
    private String fxCode;
    private String fxCodeDescription;
    private String fxType;
    private String fxTypeDescription;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal fxAmount;

    private LocalDate fxDate;

    public FxEnquiryDto() {
    }

    public FxEnquiryDto(
        Long id,
        Long masterId,
        LocalDate reportDate,
        Integer recordNo,
        String fxCategory,
        String fxCode,
        String fxType,
        BigDecimal fxAmount,
        LocalDate fxDate
    ) {
        this.id = id;
        this.masterId = masterId;
        this.reportDate = reportDate;
        this.recordNo = recordNo;
        this.fxCategory = fxCategory;
        this.fxCode = fxCode;
        this.fxType = fxType;
        this.fxAmount = fxAmount;
        this.fxDate = fxDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMasterId() { return masterId; }
    public void setMasterId(Long masterId) { this.masterId = masterId; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public Integer getRecordNo() { return recordNo; }
    public void setRecordNo(Integer recordNo) { this.recordNo = recordNo; }
    public String getFxCategory() { return fxCategory; }
    public void setFxCategory(String fxCategory) { this.fxCategory = fxCategory; }
    public String getFxCategoryDescription() { return fxCategoryDescription; }
    public void setFxCategoryDescription(String fxCategoryDescription) { this.fxCategoryDescription = fxCategoryDescription; }
    public String getFxCode() { return fxCode; }
    public void setFxCode(String fxCode) { this.fxCode = fxCode; }
    public String getFxCodeDescription() { return fxCodeDescription; }
    public void setFxCodeDescription(String fxCodeDescription) { this.fxCodeDescription = fxCodeDescription; }
    public String getFxType() { return fxType; }
    public void setFxType(String fxType) { this.fxType = fxType; }
    public String getFxTypeDescription() { return fxTypeDescription; }
    public void setFxTypeDescription(String fxTypeDescription) { this.fxTypeDescription = fxTypeDescription; }
    public BigDecimal getFxAmount() { return fxAmount; }
    public void setFxAmount(BigDecimal fxAmount) { this.fxAmount = fxAmount; }
    public LocalDate getFxDate() { return fxDate; }
    public void setFxDate(LocalDate fxDate) { this.fxDate = fxDate; }
}
