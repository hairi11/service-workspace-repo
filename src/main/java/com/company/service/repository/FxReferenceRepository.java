package com.company.service.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.service.entity.FxReference;

public interface FxReferenceRepository extends JpaRepository<FxReference, Long> {

    List<FxReference> findByRefTypeOrderByDescriptionAsc(String refType);

    List<FxReference> findByRefTypeIn(Collection<String> refTypes);

    boolean existsByRefTypeAndCode(String refType, String code);
}
