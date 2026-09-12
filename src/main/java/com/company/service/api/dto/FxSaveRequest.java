package com.company.service.api.dto;

import java.util.ArrayList;
import java.util.List;

public class FxSaveRequest {

    private FxMasterDto master;
    private List<FxTrxDto> transactions = new ArrayList<>();

    public FxMasterDto getMaster() {
        return master;
    }

    public void setMaster(FxMasterDto master) {
        this.master = master;
    }

    public List<FxTrxDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<FxTrxDto> transactions) {
        this.transactions = transactions == null ? new ArrayList<>() : transactions;
    }
}
