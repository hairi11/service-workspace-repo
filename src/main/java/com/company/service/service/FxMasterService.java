package com.company.service.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.company.service.entity.FxMaster;
import com.company.service.repository.FxMasterRepository;
import com.company.service.repository.FxTrxRepository;

@Service
public class FxMasterService {

    private final FxMasterRepository masterRepository;
    private final FxTrxRepository trxRepository;

    public FxMasterService(FxMasterRepository masterRepository, FxTrxRepository trxRepository) {
        this.masterRepository = masterRepository;
        this.trxRepository = trxRepository;
    }

    public List<FxMaster> findAll() {
        return masterRepository.findAll();
    }

    public FxMaster findById(Long id) {
        return masterRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FX master not found: " + id));
    }

    public FxMaster create(FxMaster input) {
        FxMaster master = new FxMaster();
        master.setStatus(input.getStatus());
        return masterRepository.save(master);
    }

    public FxMaster update(Long id, FxMaster input) {
        FxMaster master = findById(id);
        master.setStatus(input.getStatus());
        return masterRepository.save(master);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        trxRepository.deleteByMasterId(id);
        masterRepository.deleteById(id);
    }
}
