package com.apiece.coupon.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "issuance",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_issuance_user_coupon",
                columnNames = {"user_id", "coupon_id"}),
        indexes = {
                @Index(name = "idx_issuance_status", columnList = "status"),
                @Index(name = "idx_issuance_coupon_id", columnList = "coupon_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Issuance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long couponId;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private IssuanceStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    public Issuance(Long userId, Long couponId, IssuanceStatus status, LocalDateTime issuedAt, LocalDateTime expiresAt, LocalDateTime usedAt) {
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
    }

    public static Issuance create(Coupon coupon, Long userId, LocalDateTime now) {
        return new Issuance(
                userId,
                coupon.getId(),
                IssuanceStatus.ISSUED,
                now,
                now.plusDays(coupon.getValidityDays()),
                null
        );
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(expiresAt);
    }

    public void markUsed(LocalDateTime now) {
        this.usedAt = now;
        this.status = IssuanceStatus.USED;
    }
}
