package com.apiece.coupon.application;

import com.apiece.coupon.global.exception.SoldOutException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponIssuer {
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> script = RedisScript.of(new ClassPathResource("coupon-issuer.lua"), Long.class);

    public void tryIssue(Long couponId) {
        Long raw = redisTemplate.execute(
                script,
                List.of(stockKey(couponId))
        );
        if (raw == null) {
            throw new IllegalStateException("Lua 스크립트 결과가 null");
        }

        if (raw == 1L) {
            return;
        }
        if (raw == 0L) {
            throw new SoldOutException();
        }

        throw new IllegalStateException("Lua 스크립트 결과가 예상치 못한 값");
    }

    public void initStock(Long couponId, int totalQuantity) {
        redisTemplate.opsForValue().set(stockKey(couponId), String.valueOf(totalQuantity));
    }

    private String stockKey(Long couponId) {
        return "coupon:" + couponId + ":stock";
    }

}
