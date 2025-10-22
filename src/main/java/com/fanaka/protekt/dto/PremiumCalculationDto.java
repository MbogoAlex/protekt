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
public class PremiumCalculationDto {
    private Long id;
    private Long productPolicyId;
    private String baseAmount;
    private String premiumRate;
    private String grossPremium;
    private String taxRate;
    private String taxAmount;
    private String levyRate;
    private String levyAmount;
    private String adminFeeRate;
    private String adminFeeAmount;
    private String netPremium;
    private String totalPremium;
    private String calculationMethod;
    private String providerCalculationRef;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}