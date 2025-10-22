package com.fanaka.protekt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductPolicyDto {
    private Long policyId;
    private String productName;
    private Integer productId;
    private Long customerId;
    private String customerName;
    private Long loanId;
    private String loanPrincipal;
    private String loanDisbursed;
    private String premiumPercentage;
    private String premiumValue;
    private List<PremiumCalculationDto> premiumCalculations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime policyStartDate;
    private LocalDateTime policyEndDate;
}
