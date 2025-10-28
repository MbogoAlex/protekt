package com.fanaka.protekt.controllers;

import com.fanaka.protekt.config.BuildResponse;
import com.fanaka.protekt.services.LoanContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanContractControllerImpl implements LoanContractController{

    private final LoanContractService loanContractService;
    private final BuildResponse buildResponse;

    @Autowired
    public LoanContractControllerImpl(
            LoanContractService loanContractService,
            BuildResponse buildResponse
    ) {
        this.loanContractService = loanContractService;
        this.buildResponse = buildResponse;
    }

    @GetMapping("/filter")
    @Override
    public ResponseEntity<Object> getLoans(
            @RequestParam(name = "customerId", required = false) Long customerId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "insured", required = false) Boolean insured,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "pageSize") Integer pageSize
    ) {
        try {
            var result = loanContractService.getLoans(customerId, status, insured, page, pageSize);
            return buildResponse.success(result, "Loan contracts retrieved successfully", null, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to retrieve loan contracts", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
