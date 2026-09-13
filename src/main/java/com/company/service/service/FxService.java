package com.company.service.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.company.service.api.dto.FxEnquiryDto;
import com.company.service.api.dto.FxMasterDto;
import com.company.service.api.dto.FxReferenceDto;
import com.company.service.api.dto.FxSaveRequest;
import com.company.service.api.dto.FxSaveResponse;
import com.company.service.api.dto.FxTrxDto;
import com.company.service.api.dto.PageResponse;
import com.company.service.common.FxReferenceType;
import com.company.service.common.page.PageRequestBuilder;
import com.company.service.entity.FxMaster;
import com.company.service.entity.FxReference;
import com.company.service.entity.FxTrx;
import com.company.service.repository.FxMasterRepository;
import com.company.service.repository.FxReferenceRepository;
import com.company.service.repository.FxTrxRepository;

@Service
public class FxService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_ACTIVE = "ACTIVE";

    private final FxMasterRepository masterRepository;
    private final FxTrxRepository trxRepository;
    private final FxReferenceRepository referenceRepository;

    public FxService(
        FxMasterRepository masterRepository,
        FxTrxRepository trxRepository,
        FxReferenceRepository referenceRepository
    ) {
        this.masterRepository = masterRepository;
        this.trxRepository = trxRepository;
        this.referenceRepository = referenceRepository;
    }

    public PageResponse<FxEnquiryDto> findEnquiryRecords(int page, int size, List<String> sort) {
        Pageable pageable = PageRequestBuilder.builder()
            .page(page)
            .size(size)
            .maxSize(100)
            .sort(sort)
            .allowedSorts(
                "reportDate",
                "recordNo",
                "fxCategory",
                "fxCode",
                "fxType",
                "fxAmount",
                "fxDate"
            )
            .defaultSort("reportDate,desc", "recordNo,asc")
            .build();

        Page<FxEnquiryDto> result = trxRepository.findEnquiry(pageable);
        applyReferenceDescriptions(result.getContent());
        return new PageResponse<>(result);
    }

    public List<FxReferenceDto> findReferences(String type) {
        String resolvedType = normalizeReferenceType(type);
        return referenceRepository.findByRefTypeOrderByDescriptionAsc(resolvedType)
            .stream()
            .map(ref -> new FxReferenceDto(ref.getRefType(), ref.getCode(), ref.getDescription()))
            .collect(Collectors.toList());
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
        validateReferenceFields(input);

        FxTrx trx = new FxTrx();
        trx.setMasterId(masterId);
        trx.setRecordNo(nextRecordNo(masterId));
        copyEditableFields(input, trx);
        trx.setStatus(input.getStatus());
        return toTrxDto(trxRepository.save(trx));
    }

    public FxTrxDto updateTransaction(Long id, FxTrxDto input) {
        validateReferenceFields(input);
        FxTrx trx = findTransactionEntityById(id);
        copyEditableFields(input, trx);
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
            validateReferenceFields(input);
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

    private void applyReferenceDescriptions(List<FxEnquiryDto> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }

        List<String> types = Arrays.asList(
            FxReferenceType.CATEGORY,
            FxReferenceType.CODE,
            FxReferenceType.TYPE
        );
        Map<String, String> descriptions = new HashMap<>();

        for (FxReference ref : referenceRepository.findByRefTypeIn(types)) {
            descriptions.put(referenceKey(ref.getRefType(), ref.getCode()), ref.getDescription());
        }

        for (FxEnquiryDto row : rows) {
            row.setFxCategoryDescription(resolveDescription(
                descriptions,
                FxReferenceType.CATEGORY,
                row.getFxCategory()
            ));
            row.setFxCodeDescription(resolveDescription(
                descriptions,
                FxReferenceType.CODE,
                row.getFxCode()
            ));
            row.setFxTypeDescription(resolveDescription(
                descriptions,
                FxReferenceType.TYPE,
                row.getFxType()
            ));
        }
    }

    private String resolveDescription(Map<String, String> descriptions, String type, String code) {
        if (code == null || code.trim().isEmpty()) {
            return "";
        }
        return descriptions.getOrDefault(referenceKey(type, code), code);
    }

    private String referenceKey(String type, String code) {
        return type + "|" + code;
    }

    private String normalizeReferenceType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reference type is required.");
        }

        String value = type.trim().toUpperCase();
        if (!FxReferenceType.CATEGORY.equals(value)
            && !FxReferenceType.CODE.equals(value)
            && !FxReferenceType.CURRENCY.equals(value)
            && !FxReferenceType.TYPE.equals(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported reference type: " + value);
        }
        return value;
    }

    private void validateReferenceFields(FxTrxDto input) {
        requireReference(FxReferenceType.CATEGORY, input.getFxCategory(), "FX category");
        requireReference(FxReferenceType.CODE, input.getFxCode(), "FX code");
        requireReference(FxReferenceType.CURRENCY, input.getFxCurrency(), "FX currency");
        requireReference(FxReferenceType.TYPE, input.getFxType(), "FX type");
    }

    private void requireReference(String type, String code, String label) {
        if (code == null || code.trim().isEmpty()) {
            return;
        }
        if (!referenceRepository.existsByRefTypeAndCode(type, code)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                label + " reference not found: " + code
            );
        }
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
