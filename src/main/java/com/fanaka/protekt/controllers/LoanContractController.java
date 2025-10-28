package com.fanaka.protekt.controllers;

import org.springframework.http.ResponseEntity;

public interface LoanContractController {
    ResponseEntity<Object> getLoans(Long customerId, String status, Boolean insured, Integer page, Integer pageSize);
}
