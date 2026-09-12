package com.company.service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.company.service.api.dto.FxEnquiryDto;
import com.company.service.api.dto.FxMasterDto;
import com.company.service.api.dto.FxSaveRequest;
import com.company.service.api.dto.FxSaveResponse;
import com.company.service.api.dto.FxTrxDto;
import com.company.service.api.dto.PageResponse;
import com.company.service.entity.FxMaster;
import com.company.service.entity.FxTrx;
import com.company.service.repository.FxMasterRepository;
import com.company.service.repository.FxTrxRepository;

@Service
public class FxService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final int MAX_PAGE_SIZE = 100;

    private final FxMasterRepository masterRepository;
    private final FxTrxRepository trxRepository;

    public FxService(FxMasterRepository masterRepository, FxTrxRepository trxRepository) {
        this.masterRepository = masterRepository;
        this.trxRepository = trxRepository;
    }

    public PageResponse<FxEnquiryDto> findEnquiryRecords(
        int page,
        int size,
        String sortBy,
        String sortDir
    ) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be 0 or greater.");
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Size must be between 1 and " + MAX_PAGE_SIZE + "."
            );
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir)
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

        String sortExpression = resolveEnquirySortExpression(sortBy);
        PageRequest pageable = PageRequest.of(
            page,
            size,
            JpaSort.unsafe(direction, sortExpression)
        );

        Page<FxEnquiryDto> result = trxRepository.findEnquiry(pageable);
        return new PageResponse<>(result);
    }

    private String resolveEnquirySortExpression(String sortBy) {
        String value = sortBy == null ? "reportDate" : sortBy.trim();

        switch (value) {
            case "reportDate":
                return "m.reportDate";
            case "recordNo":
                return "t.recordNo";
            case "fxCategory":
                return "t.fxCategory";
            case "fxCode":
                return "t.fxCode";
            case "fxType":
                return "t.fxType";
            case "fxAmount":
                return "t.fxAmount";
            case "fxDate":
                return "t.fxDate";
            default:
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported sort field: " + value.toLowerCase(Locale.ROOT)
                );
        }
    }

    public List<FxMasterDto> findAllMasters() {
        return masterRepository.findAll()
            .stream()
            .map(this::toMasterDto)
            .collect(Collectors.toList());
    }

    public FxMasterDto findMasterById(Long id) {
        return toMasterDto(findMasterEntityById(id));
    }

    public FxMasterDto createMaster(FxMasterDto input) {
        FxMaster master = new FxMaster();
        master.setStatus(input.getStatus());
        return toMasterDto(masterRepository.save(master));
    }

    public FxMasterDto updateMaster(Long id, FxMasterDto input) {
        FxMaster master = findMasterEntityById(id);
        master.setStatus(input.getStatus());
        return toMasterDto(masterRepository.save(master));
    }

    @Transactional
    public void deleteMaster(Long id) {
        findMasterEntityById(id);
        trxRepository.deleteByMasterId(id);
        masterRepository.deleteById(id);
    }

    public List<FxTrxDto> findTransactionsByMasterId(Long masterId) {
        requireMaster(masterId);
        return trxRepository.findByMasterIdOrderByRecordNoAsc(masterId)
            .stream()
            .map(this::toTrxDto)
            .collect(Collectors.toList());
    }

    public FxTrxDto findTransactionById(Long id) {
        return toTrxDto(findTransactionEntityById(id));
    }

    public FxTrxDto createTransaction(Long masterId, FxTrxDto input) {
        requireMaster(masterId);

        FxTrx trx = new FxTrx();
        trx.setMasterId(masterId);
        trx.setRecordNo(nextRecordNo(masterId));
        copyEditableFields(input, trx);
        trx.setStatus(input.getStatus());
        return toTrxDto(trxRepository.save(trx));
    }

    public FxTrxDto updateTransaction(Long id, FxTrxDto input) {
        FxTrx trx = findTransactionEntityById(id);
        copyEditableFields(input, trx);
        trx.setStatus(input.getStatus());
        return toTrxDto(trxRepository.save(trx));
    }

    public void deleteTransaction(Long id) {
        findTransactionEntityById(id);
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

        FxMasterDto inputMaster = request.getMaster();
        FxMaster master;

        if (inputMaster.getId() == null) {
            master = new FxMaster();
        } else {
            master = findMasterEntityById(inputMaster.getId());
        }

        master.setStatus(status);
        master = masterRepository.save(master);

        List<FxTrxDto> savedTransactions = new ArrayList<>();
        int nextRecordNo = nextRecordNo(master.getId());

        List<FxTrxDto> transactions = request.getTransactions();
        if (transactions == null) {
            transactions = new ArrayList<>();
        }

        for (FxTrxDto input : transactions) {
            FxTrx trx;

            if (input.getId() == null) {
                trx = new FxTrx();
                trx.setMasterId(master.getId());
                trx.setRecordNo(nextRecordNo++);
            } else {
                trx = findTransactionEntityById(input.getId());

                if (!master.getId().equals(trx.getMasterId())) {
                    throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "FX transaction does not belong to master: " + master.getId()
                    );
                }
            }

            copyEditableFields(input, trx);
            trx.setStatus(status);
            savedTransactions.add(toTrxDto(trxRepository.save(trx)));
        }

        return new FxSaveResponse(toMasterDto(master), savedTransactions);
    }

    private FxMaster findMasterEntityById(Long id) {
        return masterRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "FX master not found: " + id
            ));
    }

    private FxTrx findTransactionEntityById(Long id) {
        return trxRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "FX transaction not found: " + id
            ));
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

    private void copyEditableFields(FxTrxDto source, FxTrx target) {
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

    private FxMasterDto toMasterDto(FxMaster master) {
        FxMasterDto dto = new FxMasterDto();
        dto.setId(master.getId());
        dto.setStatus(master.getStatus());
        dto.setReportDate(master.getReportDate());
        return dto;
    }

    private FxTrxDto toTrxDto(FxTrx trx) {
        FxTrxDto dto = new FxTrxDto();
        dto.setId(trx.getId());
        dto.setMasterId(trx.getMasterId());
        dto.setRecordNo(trx.getRecordNo());
        dto.setStatus(trx.getStatus());
        dto.setFxDate(trx.getFxDate());
        dto.setFxCategory(trx.getFxCategory());
        dto.setFxCode(trx.getFxCode());
        dto.setFxType(trx.getFxType());
        dto.setFxRefno(trx.getFxRefno());
        dto.setFxParty(trx.getFxParty());
        dto.setFxPrincipal(trx.getFxPrincipal());
        dto.setFxCurrency(trx.getFxCurrency());
        dto.setFxAmount(trx.getFxAmount());
        dto.setFxRate(trx.getFxRate());
        dto.setFxDescription(trx.getFxDescription());
        return dto;
    }
}
