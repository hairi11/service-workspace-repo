package com.company.service.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.company.service.entity.FxTrx;
import com.company.service.service.FxTrxService;

@RestController
@RequestMapping("/api")
public class FxTrxController {

    private final FxTrxService service;

    public FxTrxController(FxTrxService service) {
        this.service = service;
    }

    @GetMapping("/fx-masters/{masterId}/transactions")
    public List<FxTrx> findByMasterId(@PathVariable Long masterId) {
        return service.findByMasterId(masterId);
    }

    @PostMapping("/fx-masters/{masterId}/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public FxTrx create(@PathVariable Long masterId, @RequestBody FxTrx input) {
        return service.create(masterId, input);
    }

    @GetMapping("/fx-transactions/{id}")
    public FxTrx findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/fx-transactions/{id}")
    public FxTrx update(@PathVariable Long id, @RequestBody FxTrx input) {
        return service.update(id, input);
    }

    @DeleteMapping("/fx-transactions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
