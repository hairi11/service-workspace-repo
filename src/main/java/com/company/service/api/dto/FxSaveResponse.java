package com.company.service.api.dto;

import java.util.List;

public class FxSaveResponse {

    private FxMasterDto master;
    private List<FxTrxDto> transactions;

    public FxSaveResponse(FxMasterDto master, List<FxTrxDto> transactions) {
        this.master = master;
        this.transactions = transactions;
    }

    public FxMasterDto getMaster() {
        return master;
    }

    public List<FxTrxDto> getTransactions() {
        return transactions;
    }
}
