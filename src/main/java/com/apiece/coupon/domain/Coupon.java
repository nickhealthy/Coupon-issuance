package com.apiece.coupon.domain;

import com.apiece.coupon.api.dto.CreateCouponRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    private int totalQuantity;

    @Setter
    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private int validityDays;

    private LocalDateTime startsAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Coupon(String name, int totalQuantity, int issuedQuantity, int validityDays, LocalDateTime startsAt, LocalDateTime createdAt) {
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = issuedQuantity;
        this.validityDays = validityDays;
        this.startsAt = startsAt;
        this.createdAt = createdAt;
    }

    public static Coupon create(CreateCouponRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startsAt = request.startsAt() != null ? request.startsAt() : now;
        return new Coupon(
                request.name(),
                request.totalQuantity(),
                0,
                request.validityDays(),
                startsAt,
                now
        );
    }

    public boolean isBookingOpen(LocalDateTime now) {
        return !now.isBefore(startsAt);
    }

    public boolean isSoldOut() {
        return issuedQuantity >= totalQuantity;
    }

}
