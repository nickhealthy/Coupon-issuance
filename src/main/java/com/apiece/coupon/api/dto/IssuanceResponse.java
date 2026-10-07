package com.apiece.coupon.api.dto;

import com.apiece.coupon.domain.Issuance;
import com.apiece.coupon.domain.IssuanceStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public record IssuanceResponse(
        Long id,
        Long couponId,
        Long userId,
        IssuanceStatus status,
        LocalDateTime issuedAt,
        LocalDateTime expiresAt,
        LocalDateTime usedAt
) {
    public static IssuanceResponse from(Issuance issuance) {
        return new IssuanceResponse(
                Objects.requireNonNull(issuance.getId()),
                issuance.getCouponId(),
                issuance.getUserId(),
                issuance.getStatus(),
                issuance.getIssuedAt(),
                issuance.getExpiresAt(),
                issuance.getUsedAt()
        );
    }
}
