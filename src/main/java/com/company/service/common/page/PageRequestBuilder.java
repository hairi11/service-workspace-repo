package com.company.service.common.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PageRequestBuilder {

    private int page;
    private int size = 20;
    private int maxSize = 100;
    private List<String> sortSpecs;
    private final Set<String> allowedSorts = new HashSet<>();
    private final List<Sort.Order> defaultOrders = new ArrayList<>();

    private PageRequestBuilder() {
    }

    public static PageRequestBuilder builder() {
        return new PageRequestBuilder();
    }

    public PageRequestBuilder page(int page) {
        this.page = page;
        return this;
    }

    public PageRequestBuilder size(int size) {
        this.size = size;
        return this;
    }

    public PageRequestBuilder maxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public PageRequestBuilder sort(List<String> sortSpecs) {
        this.sortSpecs = sortSpecs;
        return this;
    }

    public PageRequestBuilder allowedSorts(String... fields) {
        this.allowedSorts.addAll(Arrays.asList(fields));
        return this;
    }

    public PageRequestBuilder defaultSort(String... specs) {
        this.defaultOrders.clear();
        for (String spec : specs) {
            this.defaultOrders.add(parseOrder(spec));
        }
        return this;
    }

    public Pageable build() {
        validatePage();
        validateSize();

        List<Sort.Order> orders = new ArrayList<>();
        if (sortSpecs != null) {
            for (String spec : sortSpecs) {
                if (spec != null && !spec.trim().isEmpty()) {
                    orders.add(parseOrder(spec));
                }
            }
        }

        if (orders.isEmpty()) {
            orders.addAll(defaultOrders);
        }

        return orders.isEmpty()
            ? PageRequest.of(page, size)
            : PageRequest.of(page, size, Sort.by(orders));
    }

    private Sort.Order parseOrder(String spec) {
        String[] parts = spec.split(",", 2);
        String property = parts[0].trim();
        String directionValue = parts.length > 1 ? parts[1].trim() : "asc";

        if (property.isEmpty()) {
            throw badRequest("Sort field is required.");
        }

        if (!allowedSorts.isEmpty() && !allowedSorts.contains(property)) {
            throw badRequest("Unsupported sort field: " + property.toLowerCase(Locale.ROOT));
        }

        Sort.Direction direction;
        if ("asc".equalsIgnoreCase(directionValue)) {
            direction = Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(directionValue)) {
            direction = Sort.Direction.DESC;
        } else {
            throw badRequest("Unsupported sort direction: " + directionValue.toLowerCase(Locale.ROOT));
        }

        return new Sort.Order(direction, property);
    }

    private void validatePage() {
        if (page < 0) {
            throw badRequest("Page must be 0 or greater.");
        }
    }

    private void validateSize() {
        if (maxSize < 1) {
            throw new IllegalStateException("Max page size must be greater than 0.");
        }
        if (size < 1 || size > maxSize) {
            throw badRequest("Size must be between 1 and " + maxSize + ".");
        }
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
