package com.fanaka.protekt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoanContractDto {
    private Long application;
    private Long productId;
    private Long approvedBy;
    private Long signedBy;
    private Long branch;
    private String justification;
    private String results;

    // PRINCIPAL & INTEREST - Clear naming with "Amount" suffix
    private String principalAmount;           // Amount customer requested
    private String principalPaidAmount;       // Amount of principal customer has paid back
    private String interestType;
    private String interestValue;
    private String interestAmount;            // Total interest to be paid
    private String interestPaidAmount;        // Amount of interest already paid

    // PROCESSING FEES - Clear naming with "Amount" suffix
    private String processingFeeType;
    private String processingFeeValue;
    private String processingFeeAmount;       // Processing fee deducted from loan
    private String processingFeePaidAmount;   // Amount of processing fee paid

    // INSURANCE/PREMIUM - Clear business terminology
    private String insuranceType;
    private String insuranceValue;
    private String premiumAmount;             // Insurance premium deducted (was "insuranceFee")
    private String premiumPaidAmount;         // Amount of premium already paid

    // PENALTIES - Clear naming with "Amount" suffix
    private String penaltyInterestRate;
    private String penaltyFeeAmount;          // Penalty fee applied
    private String penaltyPaidAmount;         // Amount of penalties paid

    // REPAYMENT TERMS
    private String repaymentMode;
    private Byte paymentDaysPerWeek;
    private Integer maturityDays;
    private Integer gracePeriodDays;
    private Integer installmentCount;
    private String installmentAmount;         // Amount per installment

    // CRITICAL MONEY FLOW FIELDS - Crystal clear naming
    private String disbursedAmount;           // Net cash given to customer (after deductions)
    private String totalPayableAmount;        // Total amount customer must repay
    private String totalPaidAmount;           // Total amount customer has paid so far

    // DATES & STATUS
    private LocalDateTime disbursedAt;
    private LocalDateTime maturityDate;
    private String status;

    // RELATIONSHIPS
    private ProductPolicyDto productPolicy;
}
