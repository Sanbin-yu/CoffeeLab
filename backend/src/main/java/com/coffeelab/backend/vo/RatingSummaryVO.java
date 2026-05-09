package com.coffeelab.backend.vo;

import java.util.Map;

public record RatingSummaryVO(double averageRating, int ratingCount, Map<Integer, Integer> scoreDistribution) {
}
