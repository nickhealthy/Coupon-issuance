package com.apiece.coupon.domain;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssuanceRepository extends JpaRepository<Issuance, Long> {

    boolean existsByCouponIdAndUserId(Long couponId, Long userId);

    List<Issuance> findByUserIdOrderByIssuedAtDesc(Long userId);
}
