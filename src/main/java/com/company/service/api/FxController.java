package com.company.service.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.company.service.api.dto.FxSaveRequest;
import com.company.service.api.dto.FxSaveResponse;
import com.company.service.entity.FxMaster;
import com.company.service.entity.FxTrx;
import com.company.service.service.FxService;

@RestController
@RequestMapping("/api")
public class FxController {

    private final FxService service;

    public FxController(FxService service) {
        this.service = service;
    }

    @GetMapping("/fx-masters")
    public List<FxMaster> findAllMasters() {
        return service.findAllMasters();
    }

    @GetMapping("/fx-masters/{id}")
    public FxMaster findMasterById(@PathVariable Long id) {
        return service.findMasterById(id);
    }

    @PostMapping("/fx-masters")
    @ResponseStatus(HttpStatus.CREATED)
    public FxMaster createMaster(@RequestBody FxMaster input) {
        return service.createMaster(input);
    }

    @PostMapping("/fx-masters/{id}")
    public FxMaster updateMaster(@PathVariable Long id, @RequestBody FxMaster input) {
        return service.updateMaster(id, input);
    }

    @PostMapping("/fx-masters/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMaster(@PathVariable Long id) {
        service.deleteMaster(id);
    }

    @GetMapping("/fx-masters/{masterId}/transactions")
    public List<FxTrx> findTransactionsByMasterId(@PathVariable Long masterId) {
        return service.findTransactionsByMasterId(masterId);
    }

    @PostMapping("/fx-masters/{masterId}/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public FxTrx createTransaction(@PathVariable Long masterId, @RequestBody FxTrx input) {
        return service.createTransaction(masterId, input);
    }

    @GetMapping("/fx-transactions/{id}")
    public FxTrx findTransactionById(@PathVariable Long id) {
        return service.findTransactionById(id);
    }

    @PostMapping("/fx-transactions/{id}")
    public FxTrx updateTransaction(@PathVariable Long id, @RequestBody FxTrx input) {
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
