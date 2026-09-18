package com.company.service.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FxTrxDto {

    private Long id;
    private Long masterId;
    private Integer recordNo;
    private String status;
    private LocalDate fxDate;
    private String fxCategory;
    private String fxCode;
    private String fxType;
    private String fxRefno;
    private String fxParty;
    private String fxPrincipal;
    private String fxCurrency;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal fxAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal fxRate;
    private String fxDescription;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMasterId() { return masterId; }
    public void setMasterId(Long masterId) { this.masterId = masterId; }
    public Integer getRecordNo() { return recordNo; }
    public void setRecordNo(Integer recordNo) { this.recordNo = recordNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getFxDate() { return fxDate; }
    public void setFxDate(LocalDate fxDate) { this.fxDate = fxDate; }
    public String getFxCategory() { return fxCategory; }
    public void setFxCategory(String fxCategory) { this.fxCategory = fxCategory; }
    public String getFxCode() { return fxCode; }
    public void setFxCode(String fxCode) { this.fxCode = fxCode; }
    public String getFxType() { return fxType; }
    public void setFxType(String fxType) { this.fxType = fxType; }
    public String getFxRefno() { return fxRefno; }
    public void setFxRefno(String fxRefno) { this.fxRefno = fxRefno; }
    public String getFxParty() { return fxParty; }
    public void setFxParty(String fxParty) { this.fxParty = fxParty; }
    public String getFxPrincipal() { return fxPrincipal; }
    public void setFxPrincipal(String fxPrincipal) { this.fxPrincipal = fxPrincipal; }
    public String getFxCurrency() { return fxCurrency; }
    public void setFxCurrency(String fxCurrency) { this.fxCurrency = fxCurrency; }
    public BigDecimal getFxAmount() { return fxAmount; }
    public void setFxAmount(BigDecimal fxAmount) { this.fxAmount = fxAmount; }
    public BigDecimal getFxRate() { return fxRate; }
    public void setFxRate(BigDecimal fxRate) { this.fxRate = fxRate; }
    public String getFxDescription() { return fxDescription; }
    public void setFxDescription(String fxDescription) { this.fxDescription = fxDescription; }
}
