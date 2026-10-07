package com.apiece.coupon.global.exception;

import org.springframework.http.HttpStatus;


public class CouponNotFoundException extends DomainException {
    public CouponNotFoundException() {
        this("쿠폰 정보를 찾을 수 없습니다.");
    }

    public CouponNotFoundException(String message) {
        super("COUPON_NOT_FOUND", HttpStatus.NOT_FOUND, message);
    }
}
