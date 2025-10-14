package com.fanaka.protekt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lms_loan_contracts")
public class LoanContract {

    @Id
    @Column(name = "application")
    private Long application;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "approved_by", nullable = false)
    private Long approvedBy;

    @Column(name = "signed_by")
    private Long signedBy;

    @Column(name = "branch", nullable = false)
    private Long branch;

    @Column(name = "justification", nullable = false, columnDefinition = "TEXT")
    private String justification;

    @Column(name = "results", columnDefinition = "TEXT")
    private String results;

    @Column(name = "principal", nullable = false, precision = 18, scale = 8)
    private BigDecimal principal;

    @Column(name = "principal_paid", nullable = false, precision = 18, scale = 8)
    private BigDecimal principalPaid = BigDecimal.ZERO;

    @Column(name = "interest_type", nullable = false, length = 50)
    private String interestType;

    @Column(name = "interest_value", nullable = false, precision = 18, scale = 8)
    private BigDecimal interestValue;

    @Column(name = "interest_amount", nullable = false, precision = 18, scale = 8)
    private BigDecimal interestAmount;

    @Column(name = "interest_paid", nullable = false, precision = 18, scale = 8)
    private BigDecimal interestPaid = BigDecimal.ZERO;

    @Column(name = "processing_fee_type", nullable = false, length = 50)
    private String processingFeeType;

    @Column(name = "processing_fee_value", nullable = false, precision = 18, scale = 8)
    private BigDecimal processingFeeValue;

    @Column(name = "processing_fee", nullable = false, precision = 18, scale = 8)
    private BigDecimal processingFee;

    @Column(name = "processing_paid", nullable = false, precision = 18, scale = 8)
    private BigDecimal processingPaid = BigDecimal.ZERO;

    @Column(name = "insurance_type", nullable = false, length = 50)
    private String insuranceType;

    @Column(name = "insurance_value", nullable = false, precision = 18, scale = 8)
    private BigDecimal insuranceValue;

    @Column(name = "insurance_fee", nullable = false, precision = 18, scale = 8)
    private BigDecimal insuranceFee;

    @Column(name = "insurance_paid", nullable = false, precision = 18, scale = 8)
    private BigDecimal insurancePaid = BigDecimal.ZERO;

    @Column(name = "penalty_interest_rate", nullable = false, precision = 18, scale = 8)
    private BigDecimal penaltyInterestRate;

    @Column(name = "penalty_fee", nullable = false, precision = 18, scale = 8)
    private BigDecimal penaltyFee;

    @Column(name = "penalty_paid", nullable = false, precision = 18, scale = 8)
    private BigDecimal penaltyPaid = BigDecimal.ZERO;

    @Column(name = "repayment_mode", nullable = false, length = 50)
    private String repaymentMode;

    @Column(name = "payment_days_per_week", nullable = false)
    private Byte paymentDaysPerWeek = 0;

    @Column(name = "maturity_days", nullable = false)
    private Integer maturityDays;

    @Column(name = "grace_period_days", nullable = false)
    private Integer gracePeriodDays;

    @Column(name = "installment_count", nullable = false)
    private Integer installmentCount;

    @Column(name = "installment_amount", nullable = false, precision = 18, scale = 8)
    private BigDecimal installmentAmount;

    @Column(name = "total_disbursed", nullable = false, precision = 18, scale = 8)
    private BigDecimal totalDisbursed;

    @Column(name = "total_payable", nullable = false, precision = 18, scale = 8)
    private BigDecimal totalPayable;

    @Column(name = "total_paid", nullable = false, precision = 18, scale = 8)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(name = "disbursed_at")
    private Timestamp disbursedAt;

    @Column(name = "maturity_date")
    private Timestamp maturityDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "PENDING";

    @Column(name = "flag", nullable = false, length = 50)
    private String flag = "PENDING";

    @Column(name = "signed_at", nullable = false, insertable = false, updatable = false)
    private Timestamp signedAt;

    @Column(name = "computation_summary", columnDefinition = "JSON")
    private String computationSummary;

    @Column(name = "other", columnDefinition = "JSON")
    private String other;

    @Column(name = "old_loan_id")
    private Long oldLoanId;

    @Column(name = "bucket_tag", nullable = false, length = 50)
    private String bucketTag = "PENDING";
}
