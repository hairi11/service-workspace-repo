package com.company.service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.company.service.entity.FxTrx;
import com.company.service.repository.FxMasterRepository;
import com.company.service.repository.FxTrxRepository;

@Service
public class FxTrxService {

    private final FxMasterRepository masterRepository;
    private final FxTrxRepository trxRepository;

    public FxTrxService(FxMasterRepository masterRepository, FxTrxRepository trxRepository) {
        this.masterRepository = masterRepository;
        this.trxRepository = trxRepository;
    }

    public List<FxTrx> findByMasterId(Long masterId) {
        requireMaster(masterId);
        return trxRepository.findByMasterIdOrderByRecordNoAsc(masterId);
    }

    public FxTrx findById(Long id) {
        return trxRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FX transaction not found: " + id));
    }

    public FxTrx create(Long masterId, FxTrx input) {
        requireMaster(masterId);

        FxTrx trx = new FxTrx();
        trx.setMasterId(masterId);
        trx.setRecordNo(nextRecordNo(masterId));
        copyEditableFields(input, trx);
        return trxRepository.save(trx);
    }

    public FxTrx update(Long id, FxTrx input) {
        FxTrx trx = findById(id);
        copyEditableFields(input, trx);
        return trxRepository.save(trx);
    }

    public void delete(Long id) {
        findById(id);
        trxRepository.deleteById(id);
    }

    private int nextRecordNo(Long masterId) {
        return trxRepository.findTopByMasterIdOrderByRecordNoDesc(masterId)
            .map(FxTrx::getRecordNo)
            .map(value -> value + 1)
            .orElse(1);
    }

    private void requireMaster(Long masterId) {
        if (!masterRepository.existsById(masterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FX master not found: " + masterId);
        }
    }

    private void copyEditableFields(FxTrx source, FxTrx target) {
        target.setStatus(source.getStatus());
        target.setFxDate(source.getFxDate());
        target.setFxCategory(source.getFxCategory());
        target.setFxCode(source.getFxCode());
        target.setFxType(source.getFxType());
        target.setFxRefno(source.getFxRefno());
        target.setFxParty(source.getFxParty());
        target.setFxPrincipal(source.getFxPrincipal());
        target.setFxCurrency(source.getFxCurrency());
        target.setFxAmount(source.getFxAmount());
        target.setFxRate(source.getFxRate());
        target.setFxDescription(source.getFxDescription());
    }
}
