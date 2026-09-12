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

    private final FxMasterRepository masterRepository;
    private final FxTrxRepository trxRepository;

    public FxService(FxMasterRepository masterRepository, FxTrxRepository trxRepository) {
        this.masterRepository = masterRepository;
        this.trxRepository = trxRepository;
    }

    @Transactional
    public FxSaveResponse save(FxSaveRequest request) {
        if (request == null || request.getMaster() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FX master is required.");
        }

        FxMaster inputMaster = request.getMaster();
        FxMaster master;

        if (inputMaster.getId() == null) {
            master = new FxMaster();
        } else {
            master = masterRepository.findById(inputMaster.getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "FX master not found: " + inputMaster.getId()
                ));
        }

        master.setStatus(inputMaster.getStatus());
        master = masterRepository.save(master);

        List<FxTrx> savedTransactions = new ArrayList<>();
        int nextRecordNo = trxRepository.findTopByMasterIdOrderByRecordNoDesc(master.getId())
            .map(FxTrx::getRecordNo)
            .map(value -> value + 1)
            .orElse(1);

        for (FxTrx input : request.getTransactions()) {
            FxTrx trx;

            if (input.getId() == null) {
                trx = new FxTrx();
                trx.setMasterId(master.getId());
                trx.setRecordNo(nextRecordNo++);
            } else {
                trx = trxRepository.findById(input.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "FX transaction not found: " + input.getId()
                    ));

                if (!master.getId().equals(trx.getMasterId())) {
                    throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "FX transaction does not belong to master: " + master.getId()
                    );
                }
            }

            copyEditableFields(input, trx);
            savedTransactions.add(trxRepository.save(trx));
        }

        return new FxSaveResponse(master, savedTransactions);
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
