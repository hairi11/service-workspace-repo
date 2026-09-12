package com.company.service.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.company.service.entity.FxMaster;
import com.company.service.entity.FxTrx;

public class FxSaveRequest {

    private FxMaster master;
    private List<FxTrx> transactions = new ArrayList<>();

    public FxMaster getMaster() {
        return master;
    }

    public void setMaster(FxMaster master) {
        this.master = master;
    }

    public List<FxTrx> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<FxTrx> transactions) {
        this.transactions = transactions == null ? new ArrayList<>() : transactions;
    }
}
