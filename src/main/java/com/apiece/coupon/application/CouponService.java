package com.apiece.coupon.application;

import com.apiece.coupon.api.dto.CreateCouponRequest;
import com.apiece.coupon.domain.Coupon;
import com.apiece.coupon.domain.CouponRepository;
import com.apiece.coupon.domain.Issuance;
import com.apiece.coupon.domain.IssuanceRepository;
import com.apiece.coupon.global.exception.AlreadyIssuedException;
import com.apiece.coupon.global.exception.CouponNotFoundException;
import com.apiece.coupon.global.exception.NotStartedException;
import com.apiece.coupon.global.exception.SoldOutException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final IssuanceRepository issuanceRepository;
    private final CouponIssuer couponIssuer;

    @Transactional
    public Coupon createCoupon(CreateCouponRequest request) {
        Coupon coupon = couponRepository.save(Coupon.create(request));
        couponIssuer.initStock(coupon.getId(), coupon.getTotalQuantity());
        return coupon;
    }


    @Transactional
    public Issuance issue(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(CouponNotFoundException::new);

        LocalDateTime now = LocalDateTime.now();
        if (!coupon.isBookingOpen(now)) {
            throw new NotStartedException();
        }
        if (coupon.isSoldOut()) {
            throw new SoldOutException();
        }
        if (issuanceRepository.existsByCouponIdAndUserId(couponId, userId)) {
            throw new AlreadyIssuedException();
        }

        couponIssuer.tryIssue(couponId);
        couponRepository.incrementIssuedQuantity(couponId);

        return issuanceRepository.save(Issuance.create(coupon, userId, now));
    }
}
