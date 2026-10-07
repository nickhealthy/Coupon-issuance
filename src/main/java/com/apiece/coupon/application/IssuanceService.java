package com.apiece.coupon.application;

import com.apiece.coupon.domain.Issuance;
import com.apiece.coupon.domain.IssuanceRepository;
import com.apiece.coupon.global.exception.AlreadyUsedException;
import com.apiece.coupon.global.exception.ExpiredException;
import com.apiece.coupon.global.exception.IssuanceNotFoundException;
import com.apiece.coupon.global.exception.NotOwnerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssuanceService {
    private final IssuanceRepository issuanceRepository;

    @Transactional
    public Issuance use(Long issuanceId, Long userId) {
        Issuance issuance = issuanceRepository.findById(issuanceId)
                .orElseThrow(IssuanceNotFoundException::new);

        if (issuance.getUserId().equals(userId)) {
            throw new NotOwnerException();
        }

        switch (issuance.getStatus()) {
            case USED -> throw new AlreadyUsedException();
            case EXPIRED -> throw new ExpiredException();
            case ISSUED -> {
            }
        }

        LocalDateTime now = LocalDateTime.now();
        if (issuance.isExpired(now)) {
            throw new ExpiredException();
        }

        issuance.markUsed(now);
        return issuance;
    }

    public List<Issuance> findByUser(Long userId) {
        return issuanceRepository.findByUserIdOrderByIssuedAtDesc(userId);
    }
}
