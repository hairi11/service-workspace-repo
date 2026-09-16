package com.company.service.api;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.company.service.api.dto.FxEnquiryDto;
import com.company.service.api.dto.FxMasterDto;
import com.company.service.api.dto.FxReferenceDto;
import com.company.service.api.dto.FxSaveRequest;
import com.company.service.api.dto.FxSaveResponse;
import com.company.service.api.dto.FxTrxDto;
import com.company.service.api.dto.FxValidationResponse;
import com.company.service.api.dto.PageResponse;
import com.company.service.service.FxService;
import com.company.service.service.FxValidationService;

@RestController
@RequestMapping("/api")
public class FxController {

    private static final String STATUS_DRAFT = "DRAFT";

    private final FxService service;
    private final FxValidationService validationService;

    public FxController(FxService service, FxValidationService validationService) {
        this.service = service;
        this.validationService = validationService;
    }

    @GetMapping("/fx/enquiry")
    public PageResponse<FxEnquiryDto> enquiry(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        HttpServletRequest request
    ) {
        String[] sortValues = request.getParameterValues("sort");
        List<String> sort = sortValues == null ? null : Arrays.asList(sortValues);
        return service.findEnquiryRecords(page, size, sort);
    }

    @GetMapping("/fx/references")
    public List<FxReferenceDto> findReferences(@RequestParam String type) {
        return service.findReferences(type);
    }

    @GetMapping("/fx/validate-date")
    public FxValidationResponse validateDate(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date
    ) {
        return validationService.validateDate(date);
    }

    @GetMapping("/fx-masters")
    public List<FxMasterDto> findAllMasters() {
        return service.findAllMasters();
    }

    @GetMapping("/fx-masters/{id}")
    public FxMasterDto findMasterById(@PathVariable Long id) {
        return service.findMasterById(id);
    }

    @PostMapping("/fx-masters")
    @ResponseStatus(HttpStatus.CREATED)
    public FxMasterDto createMaster() {
        FxMasterDto input = new FxMasterDto();
        input.setStatus(STATUS_DRAFT);
        return service.createMaster(input);
    }

    @PostMapping("/fx-masters/{id}")
    public FxMasterDto updateMaster(@PathVariable Long id, @RequestBody FxMasterDto input) {
        return service.updateMaster(id, input);
    }

    @PostMapping("/fx-masters/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMaster(@PathVariable Long id) {
        service.deleteMaster(id);
    }

    @GetMapping("/fx-masters/{masterId}/transactions")
    public List<FxTrxDto> findTransactionsByMasterId(@PathVariable Long masterId) {
        return service.findTransactionsByMasterId(masterId);
    }

    @PostMapping("/fx-masters/{masterId}/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public FxTrxDto createTransaction(@PathVariable Long masterId, @RequestBody FxTrxDto input) {
        validationService.requireValidDate(input.getFxDate());
        FxMasterDto master = service.findMasterById(masterId);
        input.setStatus(master.getStatus());
        return service.createTransaction(masterId, input);
    }

    @GetMapping("/fx-transactions/{id}")
    public FxTrxDto findTransactionById(@PathVariable Long id) {
        return service.findTransactionById(id);
    }

    @PostMapping("/fx-transactions/{id}")
    public FxTrxDto updateTransaction(@PathVariable Long id, @RequestBody FxTrxDto input) {
        validationService.requireValidDate(input.getFxDate());
        return service.updateTransaction(id, input);
    }

    @PostMapping("/fx-transactions/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable Long id) {
        service.deleteTransaction(id);
    }

    @PostMapping("/fx/save")
    public FxSaveResponse save(@RequestBody FxSaveRequest request) {
        validationService.validateTransactions(
            request == null ? null : request.getTransactions()
        );
        return service.saveDraft(request);
    }

    @PostMapping("/fx/submit")
    public FxSaveResponse submit(@RequestBody FxSaveRequest request) {
        validationService.validateTransactions(
            request == null ? null : request.getTransactions()
        );
        return service.submit(request);
    }
}
