package com.company.service.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fx_trx")
public class FxTrx {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "master_id", nullable = false)
    private Long masterId;

    @Column(name = "record_no", nullable = false)
    private Integer recordNo;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "fx_date")
    private LocalDate fxDate;

    @Column(name = "fx_category", length = 100)
    private String fxCategory;

    @Column(name = "fx_code", length = 100)
    private String fxCode;

    @Column(name = "fx_type", length = 100)
    private String fxType;

    @Column(name = "fx_refno", length = 100)
    private String fxRefno;

    @Column(name = "fx_party", length = 255)
    private String fxParty;

    @Column(name = "fx_principal", length = 255)
    private String fxPrincipal;

    @Column(name = "fx_currency", length = 20)
    private String fxCurrency;

    @Column(name = "fx_amount", precision = 20, scale = 4)
    private BigDecimal fxAmount;

    @Column(name = "fx_rate", precision = 19, scale = 8)
    private BigDecimal fxRate;

    @Column(name = "fx_description", length = 1000)
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
