package com.company.service.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FxEnquiryDto {

    private LocalDate reportDate;
    private Integer recordNo;
    private String fxCategory;
    private String fxCode;
    private String fxType;
    private BigDecimal fxAmount;
    private LocalDate fxDate;

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public Integer getRecordNo() { return recordNo; }
    public void setRecordNo(Integer recordNo) { this.recordNo = recordNo; }
    public String getFxCategory() { return fxCategory; }
    public void setFxCategory(String fxCategory) { this.fxCategory = fxCategory; }
    public String getFxCode() { return fxCode; }
    public void setFxCode(String fxCode) { this.fxCode = fxCode; }
    public String getFxType() { return fxType; }
    public void setFxType(String fxType) { this.fxType = fxType; }
    public BigDecimal getFxAmount() { return fxAmount; }
    public void setFxAmount(BigDecimal fxAmount) { this.fxAmount = fxAmount; }
    public LocalDate getFxDate() { return fxDate; }
    public void setFxDate(LocalDate fxDate) { this.fxDate = fxDate; }
}
