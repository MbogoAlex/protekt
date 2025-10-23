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
public class ProductDto {
    private Integer id;
    private String provider;
    private Integer providerId;
    private String providerProductId;
    private String name;
    private String description;
    private String productBeneficiaryType;
    private Integer policyDuration;
    private String policyDurationType;
    private String premiumCalculationMethod;
    private Boolean requiresComplexCalculation;
    private List<ProductTermDto> productTerms;
    private List<ProductPropertyDto> productProperties;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
