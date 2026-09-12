package com.company.service.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.company.service.api.dto.FxSaveRequest;
import com.company.service.api.dto.FxSaveResponse;
import com.company.service.entity.FxMaster;
import com.company.service.entity.FxTrx;
import com.company.service.repository.FxMasterRepository;
import com.company.service.repository.FxTrxRepository;

@Service
public class FxService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_ACTIVE = "ACTIVE";

    private final FxMasterRepository masterRepository;
    private final FxTrxRepository trxRepository;

    public FxService(FxMasterRepository masterRepository, FxTrxRepository trxRepository) {
        this.masterRepository = masterRepository;
        this.trxRepository = trxRepository;
    }

    public List<FxMaster> findAllMasters() {
        return masterRepository.findAll();
    }

    public FxMaster findMasterById(Long id) {
        return masterRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "FX master not found: " + id
            ));
    }

    public FxMaster createMaster(FxMaster input) {
        FxMaster master = new FxMaster();
        master.setStatus(input.getStatus());
        return masterRepository.save(master);
    }

    public FxMaster updateMaster(Long id, FxMaster input) {
        FxMaster master = findMasterById(id);
        master.setStatus(input.getStatus());
        return masterRepository.save(master);
    }

    @Transactional
    public void deleteMaster(Long id) {
        findMasterById(id);
        trxRepository.deleteByMasterId(id);
        masterRepository.deleteById(id);
    }

    public List<FxTrx> findTransactionsByMasterId(Long masterId) {
        requireMaster(masterId);
        return trxRepository.findByMasterIdOrderByRecordNoAsc(masterId);
    }

    public FxTrx findTransactionById(Long id) {
        return trxRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "FX transaction not found: " + id
            ));
    }

    public FxTrx createTransaction(Long masterId, FxTrx input) {
        requireMaster(masterId);

        FxTrx trx = new FxTrx();
        trx.setMasterId(masterId);
        trx.setRecordNo(nextRecordNo(masterId));
        copyEditableFields(input, trx);
        trx.setStatus(input.getStatus());
        return trxRepository.save(trx);
    }

    public FxTrx updateTransaction(Long id, FxTrx input) {
        FxTrx trx = findTransactionById(id);
        copyEditableFields(input, trx);
        trx.setStatus(input.getStatus());
        return trxRepository.save(trx);
    }

    public void deleteTransaction(Long id) {
        findTransactionById(id);
        trxRepository.deleteById(id);
    }

    @Transactional
    public FxSaveResponse saveDraft(FxSaveRequest request) {
        return save(request, STATUS_DRAFT);
    }

    @Transactional
    public FxSaveResponse submit(FxSaveRequest request) {
        return save(request, STATUS_ACTIVE);
    }

    private FxSaveResponse save(FxSaveRequest request, String status) {
        if (request == null || request.getMaster() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FX master is required.");
        }

        FxMaster inputMaster = request.getMaster();
        FxMaster master;

        if (inputMaster.getId() == null) {
            master = new FxMaster();
        } else {
            master = findMasterById(inputMaster.getId());
        }

        master.setStatus(status);
        master = masterRepository.save(master);

        List<FxTrx> savedTransactions = new ArrayList<>();
        int nextRecordNo = nextRecordNo(master.getId());

        List<FxTrx> transactions = request.getTransactions();
        if (transactions == null) {
            transactions = new ArrayList<>();
        }

        for (FxTrx input : transactions) {
            FxTrx trx;

            if (input.getId() == null) {
                trx = new FxTrx();
                trx.setMasterId(master.getId());
                trx.setRecordNo(nextRecordNo++);
            } else {
                trx = findTransactionById(input.getId());

                if (!master.getId().equals(trx.getMasterId())) {
                    throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "FX transaction does not belong to master: " + master.getId()
                    );
                }
            }

            copyEditableFields(input, trx);
            trx.setStatus(status);
            savedTransactions.add(trxRepository.save(trx));
        }

        return new FxSaveResponse(master, savedTransactions);
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
