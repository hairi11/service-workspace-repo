package com.company.service.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.company.service.api.dto.FxTrxDto;
import com.company.service.api.dto.FxValidationResponse;

@Service
public class FxValidationService {

    private static final int MAX_FX_DATE_DAYS_FROM_TODAY = 14;
    private static final String FX_DATE_MESSAGE =
        "FX Date cannot be more than 14 days from today.";

    public FxValidationResponse validateDate(LocalDate date) {
        boolean valid = date != null
            && !date.isAfter(LocalDate.now().plusDays(MAX_FX_DATE_DAYS_FROM_TODAY));

        return new FxValidationResponse(
            valid,
            valid ? null : FX_DATE_MESSAGE
        );
    }

    public void requireValidDate(LocalDate date) {
        if (date == null) {
            return;
        }

        FxValidationResponse validation = validateDate(date);
        if (!validation.isValid()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                validation.getMessage()
            );
        }
    }

    public void validateTransactions(List<FxTrxDto> transactions) {
        if (transactions == null) {
            return;
        }

        for (FxTrxDto transaction : transactions) {
            requireValidDate(transaction.getFxDate());
        }
    }
}
