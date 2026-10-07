package com.apiece.coupon.api.dto;

import java.time.LocalDateTime;

public record CreateCouponRequest (
        String name,
        int totalQuantity,
        int validityDays,
        LocalDateTime startsAt
) {
    public CreateCouponRequest(String name, Integer totalQuantity, Integer validityDays, LocalDateTime startsAt) {
        this(name, totalQuantity != null ? totalQuantity : 5000, validityDays != null ? validityDays : 7, startsAt);
    }
}
