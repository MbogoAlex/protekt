package com.fanaka.protekt.services;

import com.fanaka.protekt.entities.Product;
import com.fanaka.protekt.entities.ProductPolicy;
import com.fanaka.protekt.entities.ProductProperty;
import com.fanaka.protekt.entities.PremiumCalculation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PremiumCalculationHelper {

    public PremiumCalculation calculatePremium(ProductPolicy policy) {
        Product product = policy.getProduct();
        String calculationMethod = product.getPremiumCalculationMethod();

        // Use the premium calculation method instead of provider name
        if ("HOLLARD_STANDARD".equalsIgnoreCase(calculationMethod)) {
            return calculateHollardPremium(policy);
        } else if ("TURACO_STANDARD".equalsIgnoreCase(calculationMethod)) {
            return calculateTuracoPremium(policy);
        } else {
            return calculateStandardPremium(policy);
        }
    }

    private PremiumCalculation calculateHollardPremium(ProductPolicy policy) {
        Product product = policy.getProduct();
        Map<String, String> properties = getProductPropertiesMap(product);

        // Get SUM ASSURED amount (principal from LoanContract - this is what premium is calculated on)
        BigDecimal sumAssured = policy.getLoanContract().getPrincipal();

        // Get Hollard-specific rates from product properties
        BigDecimal premiumRate = getDecimalProperty(properties, "PREMIUM_RATE", "0.0045"); // Default 0.45%
        BigDecimal levyRate = getDecimalProperty(properties, "LEVY_RATE", "0.05"); // Default 5% LEVY AND TAX
        BigDecimal adminFeeRate = getDecimalProperty(properties, "ADMIN_FEE_RATE", "0.25"); // Default 25% ADMIN FEE

        // Hollard calculation logic based on the document:
        // 1. GROSS PREMIUM = SUM_ASSURED × PREMIUM_RATE
        BigDecimal grossPremium = sumAssured.multiply(premiumRate);

        // 2. LEVY @ 5% = GROSS_PREMIUM × LEVY_RATE
        BigDecimal levyAmount = grossPremium.multiply(levyRate);

        // 3. Net IPL = GROSS_PREMIUM - LEVY
        BigDecimal netIPL = grossPremium.subtract(levyAmount);

        // 4. ADMIN FEE @25% = Net_IPL × ADMIN_FEE_RATE
        BigDecimal adminFeeAmount = netIPL.multiply(adminFeeRate);

        // 5. NET PREMIUM PAYABLE = Net_IPL - ADMIN_FEE
        BigDecimal finalPremium = netIPL.subtract(adminFeeAmount);

        return PremiumCalculation.builder()
                .productPolicy(policy)
                .baseAmount(sumAssured.toString()) // SUM ASSURED amount (principal)
                .premiumRate(premiumRate.toString())
                .grossPremium(grossPremium.setScale(2, RoundingMode.HALF_UP).toString())
                .levyRate(levyRate.toString()) // Hollard calls this LEVY @ 5%
                .levyAmount(levyAmount.setScale(2, RoundingMode.HALF_UP).toString()) // LEVY amount
                .adminFeeRate(adminFeeRate.toString())
                .adminFeeAmount(adminFeeAmount.setScale(2, RoundingMode.HALF_UP).toString())
                .netPremium(netIPL.setScale(2, RoundingMode.HALF_UP).toString()) // Net IPL
                .totalPremium(finalPremium.setScale(2, RoundingMode.HALF_UP).toString()) // NET PREMIUM PAYABLE
                .calculationMethod("HOLLARD_STANDARD")
                .createdAt(Timestamp.from(Instant.now()))
                .build();
    }

    private PremiumCalculation calculateTuracoPremium(ProductPolicy policy) {
        // Placeholder for Turaco calculation
        // Similar structure but different logic
        Product product = policy.getProduct();
        Map<String, String> properties = getProductPropertiesMap(product);

        BigDecimal baseAmount = new BigDecimal(policy.getLoanAmount() != null ? policy.getLoanAmount() : "0");
        BigDecimal premiumRate = getDecimalProperty(properties, "PREMIUM_RATE", "0.02"); // Different default

        BigDecimal totalPremium = baseAmount.multiply(premiumRate);

        return PremiumCalculation.builder()
                .productPolicy(policy)
                .baseAmount(baseAmount.toString())
                .premiumRate(premiumRate.toString())
                .grossPremium(totalPremium.setScale(2, RoundingMode.HALF_UP).toString())
                .totalPremium(totalPremium.setScale(2, RoundingMode.HALF_UP).toString())
                .calculationMethod("TURACO_STANDARD")
                .createdAt(Timestamp.from(Instant.now()))
                .build();
    }

    private PremiumCalculation calculateStandardPremium(ProductPolicy policy) {
        // For internal or default calculations
        BigDecimal baseAmount = new BigDecimal(policy.getLoanAmount() != null ? policy.getLoanAmount() : "0");
        BigDecimal premiumRate = new BigDecimal(policy.getPremiumPercentage() != null ? policy.getPremiumPercentage() : "0.01");

        BigDecimal totalPremium = baseAmount.multiply(premiumRate.divide(new BigDecimal("100")));

        return PremiumCalculation.builder()
                .productPolicy(policy)
                .baseAmount(baseAmount.toString())
                .premiumRate(premiumRate.toString())
                .grossPremium(totalPremium.setScale(2, RoundingMode.HALF_UP).toString())
                .totalPremium(totalPremium.setScale(2, RoundingMode.HALF_UP).toString())
                .calculationMethod("INTERNAL_STANDARD")
                .createdAt(Timestamp.from(Instant.now()))
                .build();
    }

    private Map<String, String> getProductPropertiesMap(Product product) {
        if (product.getProductProperties() == null) {
            return Map.of();
        }

        return product.getProductProperties().stream()
                .collect(Collectors.toMap(
                    ProductProperty::getKey,
                    ProductProperty::getValue,
                    (existing, replacement) -> existing
                ));
    }

    private BigDecimal getDecimalProperty(Map<String, String> properties, String key, String defaultValue) {
        String value = properties.getOrDefault(key, defaultValue);
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return new BigDecimal(defaultValue);
        }
    }

    public boolean isComplexCalculationRequired(Product product) {
        return product.getRequiresComplexCalculation() != null && product.getRequiresComplexCalculation();
    }

    public String getCalculationMethodForProvider(String provider) {
        return switch (provider.toUpperCase()) {
            case "HOLLARD" -> "HOLLARD_STANDARD";
            case "TURACO" -> "TURACO_STANDARD";
            default -> "INTERNAL_STANDARD";
        };
    }
}