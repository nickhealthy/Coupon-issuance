package com.apiece.coupon.api;

import com.apiece.coupon.api.dto.CouponResponse;
import com.apiece.coupon.api.dto.CreateCouponRequest;
import com.apiece.coupon.api.dto.IssuanceResponse;
import com.apiece.coupon.application.CouponService;
import com.apiece.coupon.domain.Coupon;
import com.apiece.coupon.domain.Issuance;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponResponse> create(@RequestBody CreateCouponRequest request) {
        Coupon coupon = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CouponResponse.from(coupon));
    }

    @PostMapping("{couponId}/issue")
    public ResponseEntity<IssuanceResponse> issue(@PathVariable Long couponId,
                                                  @RequestHeader(value = "X-User-Id") Long userId) {
        Issuance issuance = couponService.issue(couponId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(IssuanceResponse.from(issuance));
    }
}
