package com.apiece.coupon.global.exception;


import org.springframework.http.HttpStatus;

public class SoldOutException extends DomainException {
    public SoldOutException() {
        this("이미 발급된 쿠폰입니다.");
    }

    public SoldOutException(String message) {
        super("SOLD_OUT", HttpStatus.CONFLICT, message);
    }
}
