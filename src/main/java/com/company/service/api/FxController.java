package com.company.service.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.service.api.dto.FxSaveRequest;
import com.company.service.api.dto.FxSaveResponse;
import com.company.service.service.FxService;

@RestController
@RequestMapping("/api/fx")
public class FxController {

    private final FxService service;

    public FxController(FxService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public FxSaveResponse save(@RequestBody FxSaveRequest request) {
        return service.saveDraft(request);
    }

    @PostMapping("/submit")
    public FxSaveResponse submit(@RequestBody FxSaveRequest request) {
        return service.submit(request);
    }
}
