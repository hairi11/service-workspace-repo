package com.company.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.company.service.api.dto.FxEnquiryDto;
import com.company.service.entity.FxTrx;

public interface FxTrxRepository extends JpaRepository<FxTrx, Long> {

    List<FxTrx> findByMasterIdOrderByRecordNoAsc(Long masterId);

    Optional<FxTrx> findTopByMasterIdOrderByRecordNoDesc(Long masterId);

    void deleteByMasterId(Long masterId);

    @Query(
        value = "select new com.company.service.api.dto.FxEnquiryDto(" +
            "m.reportDate, t.recordNo, t.fxCategory, t.fxCode, t.fxType, t.fxAmount, t.fxDate) " +
            "from FxTrx t, FxMaster m " +
            "where m.id = t.masterId",
        countQuery = "select count(t) from FxTrx t, FxMaster m where m.id = t.masterId"
    )
    Page<FxEnquiryDto> findEnquiry(Pageable pageable);
}
