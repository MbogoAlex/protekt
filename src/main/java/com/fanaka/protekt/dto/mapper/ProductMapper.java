package com.fanaka.protekt.dto.mapper;

import com.fanaka.protekt.dto.ProductDto;
import com.fanaka.protekt.dto.ProductPolicyDto;
import com.fanaka.protekt.dto.ProductPropertyDto;
import com.fanaka.protekt.dto.ProductTermDto;
import com.fanaka.protekt.entities.Product;
import com.fanaka.protekt.entities.ProductPolicy;
import com.fanaka.protekt.entities.ProductProperty;
import com.fanaka.protekt.entities.ProductTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final ProviderMapper providerMapper;

    private final PremiumCalculationMapper premiumCalculationMapper;

    @Autowired
    public ProductMapper(
            ProviderMapper providerMapper,
            PremiumCalculationMapper premiumCalculationMapper
    ) {
        this.providerMapper = providerMapper;
        this.premiumCalculationMapper = premiumCalculationMapper;
    }

    public ProductDto toProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .provider(product.getProvider())
                .providerId(product.getProviderEntity() != null ? product.getProviderEntity().getId() : null)
                .providerProductId(product.getProviderProductId())
                .name(product.getName())
                .description(product.getDescription())
                .productBeneficiaryType(product.getProductBeneficiaryType())
                .policyDuration(product.getPolicyDuration())
                .policyDurationType(product.getPolicyDurationType())
                .premiumCalculationMethod(product.getPremiumCalculationMethod())
                .requiresComplexCalculation(product.getRequiresComplexCalculation())
                .productTerms(product.getProductTerms() != null ? product.getProductTerms().stream().map(this::toProductTermDto).toList() : null)
                .createdAt(product.getCreatedAt() != null ? product.getCreatedAt().toLocalDateTime() : null)
                .updatedAt(product.getUpdatedAt() != null ? product.getUpdatedAt().toLocalDateTime() : null)
                .build();
    }

    public ProductTermDto toProductTermDto(ProductTerm productTerm) {
        return ProductTermDto.builder()
                .id(productTerm.getId())
                .term(productTerm.getTerm())
                .productName(productTerm.getProduct().getName())
                .productId(productTerm.getProduct().getId())
                .createdAt(productTerm.getCreatedAt().toLocalDateTime())
                .updatedAt(productTerm.getUpdatedAt().toLocalDateTime())
                .build();
    }

    public ProductPolicyDto toProductPolicyDto(ProductPolicy productPolicy) {

        String customerName = productPolicy.getCustomer().getMember().getFirstName() + " " +  productPolicy.getCustomer().getMember().getLastName();

        return ProductPolicyDto.builder()
                .policyId(productPolicy.getId())
                .productName(productPolicy.getProduct().getName())
                .productId(productPolicy.getProduct().getId())
                .customerId(productPolicy.getCustomer().getId())
                .customerName(customerName)
                .loanId(productPolicy.getLoanContract().getApplication())
                .loanPrincipal(String.valueOf(productPolicy.getLoanContract().getPrincipal()))
                .loanDisbursed(String.valueOf(productPolicy.getLoanContract().getTotalDisbursed()))
                .premiumPercentage(productPolicy.getPremiumPercentage())
                .premiumValue(productPolicy.getPremiumValue())
                .premiumCalculations(productPolicy.getPremiumCalculations() != null ?
                    productPolicy.getPremiumCalculations().stream()
                        .map(premiumCalculationMapper::toPremiumCalculationDto)
                        .toList() : null)
                .createdAt(productPolicy.getCreatedAt().toLocalDateTime())
                .updatedAt(productPolicy.getUpdatedAt().toLocalDateTime())
                .policyStartDate(productPolicy.getPolicyStartDate())
                .policyEndDate(productPolicy.getPolicyEndDate())
                .build();
    }

    public ProductPropertyDto toProductPropertyDto(ProductProperty productProperty) {
        return ProductPropertyDto.builder()
                .id(productProperty.getId())
                .key(productProperty.getKey())
                .value(productProperty.getValue())
                .valueType(productProperty.getValueType())
                .productId(productProperty.getProduct().getId())
                .createdAt(productProperty.getCreatedAt().toLocalDateTime())
                .updatedAt(productProperty.getUpdatedAt().toLocalDateTime())
                .build();
    }
}
