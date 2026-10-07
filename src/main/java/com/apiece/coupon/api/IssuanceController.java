package com.apiece.coupon.api;

import com.apiece.coupon.api.dto.IssuanceResponse;
import com.apiece.coupon.application.IssuanceService;
import com.apiece.coupon.domain.Issuance;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issuances")
@RequiredArgsConstructor
public class IssuanceController {
    private final IssuanceService issuanceService;

    @PostMapping("/{issuanceId}/use")
    public ResponseEntity<IssuanceResponse> use(@PathVariable Long issuanceId, @RequestHeader(value = "X-User-Id") Long userId) {
        Issuance issuance = issuanceService.use(issuanceId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(IssuanceResponse.from(issuance));
    }
}

