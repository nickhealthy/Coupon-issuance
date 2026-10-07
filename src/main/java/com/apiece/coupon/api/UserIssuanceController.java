package com.apiece.coupon.api;

import com.apiece.coupon.api.dto.IssuanceResponse;
import com.apiece.coupon.application.IssuanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/users/me/issuances")
@RequiredArgsConstructor
public class UserIssuanceController {
    private final IssuanceService issuanceService;

    @GetMapping
    public List<IssuanceResponse> listMine(@RequestHeader(value = "X-User-Id") Long userId) {
         return issuanceService.findByUser(userId).stream().map(IssuanceResponse::from).toList();
    }
}
