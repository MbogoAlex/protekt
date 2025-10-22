package com.fanaka.protekt.dto.mapper;

import com.fanaka.protekt.dto.PremiumCalculationDto;
import com.fanaka.protekt.entities.PremiumCalculation;
import org.springframework.stereotype.Component;

@Component
public class PremiumCalculationMapper {
    public PremiumCalculationDto toPremiumCalculationDto(PremiumCalculation premiumCalculation) {
        if (premiumCalculation == null) {
            return null;
        }

        return PremiumCalculationDto.builder()
                .id(premiumCalculation.getId())
                .productPolicyId(premiumCalculation.getProductPolicy() != null ? premiumCalculation.getProductPolicy().getId() : null)
                .baseAmount(premiumCalculation.getBaseAmount())
                .premiumRate(premiumCalculation.getPremiumRate())
                .grossPremium(premiumCalculation.getGrossPremium())
                .taxRate(premiumCalculation.getTaxRate())
                .taxAmount(premiumCalculation.getTaxAmount())
                .levyRate(premiumCalculation.getLevyRate())
                .levyAmount(premiumCalculation.getLevyAmount())
                .adminFeeRate(premiumCalculation.getAdminFeeRate())
                .adminFeeAmount(premiumCalculation.getAdminFeeAmount())
                .netPremium(premiumCalculation.getNetPremium())
                .totalPremium(premiumCalculation.getTotalPremium())
                .calculationMethod(premiumCalculation.getCalculationMethod())
                .providerCalculationRef(premiumCalculation.getProviderCalculationRef())
                .createdAt(premiumCalculation.getCreatedAt() != null ? premiumCalculation.getCreatedAt().toLocalDateTime() : null)
                .updatedAt(premiumCalculation.getUpdatedAt() != null ? premiumCalculation.getUpdatedAt().toLocalDateTime() : null)
                .build();
    }
}