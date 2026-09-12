package com.company.service.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.company.service.api.dto.FxEnquiryDto;
import com.company.service.entity.FxMaster;
import com.company.service.entity.FxTrx;

public class FxTrxRepositoryCustomImpl implements FxTrxRepositoryCustom {

    private final EntityManager entityManager;

    public FxTrxRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<FxEnquiryDto> findEnquiry(Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<FxEnquiryDto> query = cb.createQuery(FxEnquiryDto.class);
        Root<FxTrx> trx = query.from(FxTrx.class);
        Root<FxMaster> master = query.from(FxMaster.class);

        Predicate join = cb.equal(master.get("id"), trx.get("masterId"));

        query.select(cb.construct(
            FxEnquiryDto.class,
            master.get("reportDate"),
            trx.get("recordNo"),
            trx.get("fxCategory"),
            trx.get("fxCode"),
            trx.get("fxType"),
            trx.get("fxAmount"),
            trx.get("fxDate")
        ));
        query.where(join);
        query.orderBy(resolveOrders(cb, trx, master, pageable.getSort()));

        TypedQuery<FxEnquiryDto> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<FxEnquiryDto> content = typedQuery.getResultList();
        long total = countEnquiry(cb);

        return new PageImpl<>(content, pageable, total);
    }

    private List<javax.persistence.criteria.Order> resolveOrders(
        CriteriaBuilder cb,
        Root<FxTrx> trx,
        Root<FxMaster> master,
        Sort sort
    ) {
        List<javax.persistence.criteria.Order> orders = new ArrayList<>();

        for (Sort.Order order : sort) {
            Expression<?> expression = resolveSortExpression(trx, master, order.getProperty());
            orders.add(order.isAscending() ? cb.asc(expression) : cb.desc(expression));
        }

        return orders;
    }

    private Expression<?> resolveSortExpression(
        Root<FxTrx> trx,
        Root<FxMaster> master,
        String property
    ) {
        switch (property) {
            case "reportDate":
                return master.get("reportDate");
            case "recordNo":
                return trx.get("recordNo");
            case "fxCategory":
                return trx.get("fxCategory");
            case "fxCode":
                return trx.get("fxCode");
            case "fxType":
                return trx.get("fxType");
            case "fxAmount":
                return trx.get("fxAmount");
            case "fxDate":
                return trx.get("fxDate");
            default:
                throw new IllegalArgumentException("Unsupported sort property: " + property);
        }
    }

    private long countEnquiry(CriteriaBuilder cb) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<FxTrx> trx = countQuery.from(FxTrx.class);
        Root<FxMaster> master = countQuery.from(FxMaster.class);

        countQuery.select(cb.count(trx));
        countQuery.where(cb.equal(master.get("id"), trx.get("masterId")));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
