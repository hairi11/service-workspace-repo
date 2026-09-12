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

import com.company.service.entity.FxMaster;
import com.company.service.service.FxMasterService;

@RestController
@RequestMapping("/api/fx-masters")
public class FxMasterController {

    private final FxMasterService service;

    public FxMasterController(FxMasterService service) {
        this.service = service;
    }

    @GetMapping
    public List<FxMaster> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public FxMaster findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FxMaster create(@RequestBody FxMaster input) {
        return service.create(input);
    }

    @PutMapping("/{id}")
    public FxMaster update(@PathVariable Long id, @RequestBody FxMaster input) {
        return service.update(id, input);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
