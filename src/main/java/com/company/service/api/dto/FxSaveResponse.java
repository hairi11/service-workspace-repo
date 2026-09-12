package com.company.service.api.dto;

import java.util.List;

import com.company.service.entity.FxMaster;
import com.company.service.entity.FxTrx;

public class FxSaveResponse {

    private FxMaster master;
    private List<FxTrx> transactions;

    public FxSaveResponse(FxMaster master, List<FxTrx> transactions) {
        this.master = master;
        this.transactions = transactions;
    }

    public FxMaster getMaster() {
        return master;
    }

    public List<FxTrx> getTransactions() {
        return transactions;
    }
}
