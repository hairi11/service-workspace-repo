package com.company.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
    name = "fx_reference",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_fx_reference_type_code",
        columnNames = {"ref_type", "code"}
    )
)
public class FxReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_type", nullable = false, length = 50)
    private String refType;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRefType() { return refType; }
    public void setRefType(String refType) { this.refType = refType; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
