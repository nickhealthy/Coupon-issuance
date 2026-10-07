package com.apiece.coupon.global.exception;

import org.springframework.http.HttpStatus;

public class ExpiredException extends DomainException {
    public ExpiredException() {
        this("만료된 쿠폰입니다");
    }

    public ExpiredException(String message) {
        super("EXPIRED", HttpStatus.CONFLICT, message);
    }
}
