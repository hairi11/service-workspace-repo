package com.company.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.service.api.dto.FxEnquiryDto;

public interface FxTrxRepositoryCustom {

    Page<FxEnquiryDto> findEnquiry(Pageable pageable);
}
