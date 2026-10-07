package com.apiece.coupon.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(DomainException e, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getHttpStatus(), e.getMessage() != null ? e.getMessage() : "");
        problemDetail.setTitle(e.getCode());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("code", e.getCode());
        return ResponseEntity.status(e.getHttpStatus()).body(problemDetail);
    }
}
