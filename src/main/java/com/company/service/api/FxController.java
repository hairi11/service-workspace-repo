package com.company.service.api;

import java.util.List;

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
import com.company.service.api.dto.FxSaveRequest;
import com.company.service.api.dto.FxSaveResponse;
import com.company.service.api.dto.FxTrxDto;
import com.company.service.api.dto.PageResponse;
import com.company.service.service.FxService;

@RestController
@RequestMapping("/api")
public class FxController {

    private final FxService service;

    public FxController(FxService service) {
        this.service = service;
    }

    @GetMapping("/fx/enquiry")
    public PageResponse<FxEnquiryDto> enquiry(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) List<String> sort
    ) {
        return service.findEnquiryRecords(page, size, sort);
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
    public FxMasterDto createMaster(@RequestBody FxMasterDto input) {
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
        return service.createTransaction(masterId, input);
    }

    @GetMapping("/fx-transactions/{id}")
    public FxTrxDto findTransactionById(@PathVariable Long id) {
        return service.findTransactionById(id);
    }

    @PostMapping("/fx-transactions/{id}")
    public FxTrxDto updateTransaction(@PathVariable Long id, @RequestBody FxTrxDto input) {
        return service.updateTransaction(id, input);
    }

    @PostMapping("/fx-transactions/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable Long id) {
        service.deleteTransaction(id);
    }

    @PostMapping("/fx/save")
    public FxSaveResponse save(@RequestBody FxSaveRequest request) {
        return service.saveDraft(request);
    }

    @PostMapping("/fx/submit")
    public FxSaveResponse submit(@RequestBody FxSaveRequest request) {
        return service.submit(request);
    }
}
