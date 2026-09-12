package com.company.service.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.company.service.common.FxReferenceType;
import com.company.service.entity.FxMaster;
import com.company.service.entity.FxReference;
import com.company.service.entity.FxTrx;
import com.company.service.repository.FxMasterRepository;
import com.company.service.repository.FxReferenceRepository;
import com.company.service.repository.FxTrxRepository;

@Component
@ConditionalOnProperty(name = "app.sample-data.enabled", havingValue = "true")
public class FxSampleDataInitializer implements CommandLineRunner {

    private static final String[] CATEGORIES = {"SPOT", "FORWARD", "SWAP"};
    private static final String[] CURRENCIES = {"USD", "EUR", "SGD", "JPY", "GBP", "AUD", "CHF", "CAD"};
    private static final String[] TYPES = {"BUY", "SELL"};

    private final FxMasterRepository masterRepository;
    private final FxTrxRepository trxRepository;
    private final FxReferenceRepository referenceRepository;

    public FxSampleDataInitializer(
        FxMasterRepository masterRepository,
        FxTrxRepository trxRepository,
        FxReferenceRepository referenceRepository
    ) {
        this.masterRepository = masterRepository;
        this.trxRepository = trxRepository;
        this.referenceRepository = referenceRepository;
    }

    @Override
    public void run(String... args) {
        seedReferences();

        if (masterRepository.count() > 0 || trxRepository.count() > 0) {
            return;
        }

        LocalDate startDate = LocalDate.of(2026, 8, 25);

        for (int masterIndex = 0; masterIndex < 20; masterIndex++) {
            LocalDate reportDate = startDate.plusDays(masterIndex);
            String status = masterIndex % 5 == 4 ? "DRAFT" : "ACTIVE";
            FxMaster master = createMaster(status, reportDate);

            int transactionCount = 4 + (masterIndex % 3);
            for (int recordNo = 1; recordNo <= transactionCount; recordNo++) {
                int seed = masterIndex * 7 + recordNo;
                String category = CATEGORIES[seed % CATEGORIES.length];
                String currency = CURRENCIES[seed % CURRENCIES.length];
                String type = TYPES[seed % TYPES.length];
                BigDecimal amount = BigDecimal.valueOf(25000L + (seed * 13750L));
                BigDecimal rate = BigDecimal.valueOf(0.75000000 + ((seed % 25) * 0.13750000));

                createTrx(
                    master.getId(),
                    recordNo,
                    status,
                    category,
                    currency,
                    type,
                    amount,
                    rate,
                    reportDate
                );
            }
        }
    }

    private void seedReferences() {
        saveReference(FxReferenceType.CATEGORY, "SPOT", "Spot Transaction");
        saveReference(FxReferenceType.CATEGORY, "FORWARD", "Forward Contract");
        saveReference(FxReferenceType.CATEGORY, "SWAP", "Foreign Exchange Swap");

        saveReference(FxReferenceType.TYPE, "BUY", "Buy");
        saveReference(FxReferenceType.TYPE, "SELL", "Sell");

        saveCurrencyReference("USD", "US Dollar");
        saveCurrencyReference("EUR", "Euro");
        saveCurrencyReference("SGD", "Singapore Dollar");
        saveCurrencyReference("JPY", "Japanese Yen");
        saveCurrencyReference("GBP", "Pound Sterling");
        saveCurrencyReference("AUD", "Australian Dollar");
        saveCurrencyReference("CHF", "Swiss Franc");
        saveCurrencyReference("CAD", "Canadian Dollar");
    }

    private void saveCurrencyReference(String code, String description) {
        saveReference(FxReferenceType.CODE, code, description);
        saveReference(FxReferenceType.CURRENCY, code, description);
    }

    private void saveReference(String type, String code, String description) {
        if (referenceRepository.existsByRefTypeAndCode(type, code)) {
            return;
        }

        FxReference reference = new FxReference();
        reference.setRefType(type);
        reference.setCode(code);
        reference.setDescription(description);
        referenceRepository.save(reference);
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
        BigDecimal amount,
        BigDecimal rate,
        LocalDate fxDate
    ) {
        FxTrx trx = new FxTrx();
        trx.setMasterId(masterId);
        trx.setRecordNo(recordNo);
        trx.setStatus(status);
        trx.setFxDate(fxDate);
        trx.setFxCategory(category);
        trx.setFxCode(code);
        trx.setFxType(type);
        trx.setFxRefno("FX-" + masterId + "-" + recordNo);
        trx.setFxParty("Counterparty " + ((recordNo % 5) + 1));
        trx.setFxPrincipal("Principal " + ((recordNo % 3) + 1));
        trx.setFxCurrency(code);
        trx.setFxAmount(amount);
        trx.setFxRate(rate);
        trx.setFxDescription("Sample " + category + " " + type + " transaction");
        trxRepository.save(trx);
    }
}
