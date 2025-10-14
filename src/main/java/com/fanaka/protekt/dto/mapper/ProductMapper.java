package com.fanaka.protekt.dto.mapper;

import com.fanaka.protekt.dto.ProductDto;
import com.fanaka.protekt.dto.ProductPolicyDto;
import com.fanaka.protekt.dto.ProductTermDto;
import com.fanaka.protekt.entities.Product;
import com.fanaka.protekt.entities.ProductPolicy;
import com.fanaka.protekt.entities.ProductTerm;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class ProductMapper {
    public ProductDto toProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .provider(product.getProvider())
                .providerProductId(product.getProviderProductId())
                .name(product.getName())
                .description(product.getDescription())
                .productBeneficiaryType(product.getProductBeneficiaryType())
                .policyDuration(product.getPolicyDuration())
                .policyDurationType(product.getPolicyDurationType())
                .productTerms(product.getProductTerms().stream().map(this::toProductTermDto).toList())
                .createdAt(product.getCreatedAt().toLocalDateTime())
                .updatedAt(product.getUpdatedAt().toLocalDateTime())
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
                .loanAmount(String.valueOf(productPolicy.getLoanContract().getTotalDisbursed()))
                .premiumPercentage(productPolicy.getPremiumPercentage())
                .premiumValue(productPolicy.getPremiumValue())
                .createdAt(productPolicy.getCreatedAt().toLocalDateTime())
                .updatedAt(productPolicy.getUpdatedAt().toLocalDateTime())
                .policyStartDate(productPolicy.getPolicyStartDate())
                .policyEndDate(productPolicy.getPolicyEndDate())
                .build();
    }
}
