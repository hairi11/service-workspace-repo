package com.company.service.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.company.service.entity.FxMaster;
import com.company.service.entity.FxTrx;
import com.company.service.repository.FxMasterRepository;
import com.company.service.repository.FxTrxRepository;

@Component
@ConditionalOnProperty(name = "app.sample-data.enabled", havingValue = "true")
public class FxSampleDataInitializer implements CommandLineRunner {

    private final FxMasterRepository masterRepository;
    private final FxTrxRepository trxRepository;

    public FxSampleDataInitializer(FxMasterRepository masterRepository, FxTrxRepository trxRepository) {
        this.masterRepository = masterRepository;
        this.trxRepository = trxRepository;
    }

    @Override
    public void run(String... args) {
        if (masterRepository.count() > 0 || trxRepository.count() > 0) {
            return;
        }

        FxMaster master1 = createMaster("ACTIVE", LocalDate.of(2026, 9, 10));
        FxMaster master2 = createMaster("ACTIVE", LocalDate.of(2026, 9, 11));
        FxMaster master3 = createMaster("DRAFT", LocalDate.of(2026, 9, 12));

        createTrx(master1.getId(), 1, "ACTIVE", "SPOT", "USD", "BUY", "125000.00", "2026-09-10");
        createTrx(master1.getId(), 2, "ACTIVE", "FORWARD", "EUR", "SELL", "85000.00", "2026-09-10");
        createTrx(master2.getId(), 1, "ACTIVE", "SPOT", "SGD", "BUY", "64000.00", "2026-09-11");
        createTrx(master2.getId(), 2, "ACTIVE", "SWAP", "JPY", "SELL", "1500000.00", "2026-09-11");
        createTrx(master3.getId(), 1, "DRAFT", "FORWARD", "GBP", "BUY", "45000.00", "2026-09-12");
        createTrx(master3.getId(), 2, "DRAFT", "SPOT", "AUD", "SELL", "72000.00", "2026-09-12");
    }

    private FxMaster createMaster(String status, LocalDate reportDate) {
        FxMaster master = new FxMaster();
        master.setStatus(status);
        master.setReportDate(reportDate);
        return masterRepository.save(master);
    }

    private void createTrx(
        Long masterId,
        int recordNo,
        String status,
        String category,
        String code,
        String type,
        String amount,
        String fxDate
    ) {
        FxTrx trx = new FxTrx();
        trx.setMasterId(masterId);
        trx.setRecordNo(recordNo);
        trx.setStatus(status);
        trx.setFxDate(LocalDate.parse(fxDate));
        trx.setFxCategory(category);
        trx.setFxCode(code);
        trx.setFxType(type);
        trx.setFxCurrency(code);
        trx.setFxAmount(new BigDecimal(amount));
        trx.setFxRate(BigDecimal.ONE);
        trx.setFxDescription("Sample FX transaction");
        trxRepository.save(trx);
    }
}
