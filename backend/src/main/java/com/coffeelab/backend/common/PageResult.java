package com.coffeelab.backend.common;

import java.util.List;

public record PageResult<T>(List<T> records, int page, int pageSize, long total, int totalPages) {
    public static <T> PageResult<T> of(List<T> allRecords, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(pageSize, 1);
        int from = Math.min((safePage - 1) * safeSize, allRecords.size());
        int to = Math.min(from + safeSize, allRecords.size());
        int totalPages = (int) Math.ceil(allRecords.size() / (double) safeSize);
        return new PageResult<>(allRecords.subList(from, to), safePage, safeSize, allRecords.size(), totalPages);
    }
}
