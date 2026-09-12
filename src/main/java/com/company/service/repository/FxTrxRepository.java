package com.company.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.service.entity.FxTrx;

public interface FxTrxRepository extends JpaRepository<FxTrx, Long> {

    List<FxTrx> findByMasterIdOrderByRecordNoAsc(Long masterId);

    Optional<FxTrx> findTopByMasterIdOrderByRecordNoDesc(Long masterId);

    void deleteByMasterId(Long masterId);
}
