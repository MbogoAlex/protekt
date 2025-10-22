package com.fanaka.protekt.services;

import com.fanaka.protekt.dto.*;
import com.fanaka.protekt.entities.ProductProperty;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductCreationDto productCreationDto) throws Exception;
    ProductDto updateProduct(ProductUpdateDto productUpdateDto) throws Exception;
    ProductDto getProductById(Long id) throws Exception;
    PaginationDto<ProductDto> filterProducts(String provider, String name, String productBeneficiaryType, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize) throws Exception;

    ProductTermDto createProductTerm(ProductTermCreationDto productTermCreationDto) throws Exception;
    ProductTermDto updateProductTerm(ProductTermUpdateDto productTermUpdateDto) throws Exception;
    ProductTermDto getProductTermById(Long id) throws Exception;
    PaginationDto<ProductTermDto> filterProductTerms(Integer productId, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize) throws Exception;

    ProductPolicyDto createProductPolicy(ProductPolicyCreationDto productPolicyCreationDto) throws Exception;
    ProductPolicyDto updateProductPolicy(ProductPolicyUpdateDto productPolicyUpdateDto) throws Exception;
    ProductPolicyDto getProductPolicyById(Long id) throws Exception;
    PaginationDto<ProductPolicyDto> filterProductPolicies(Integer productId, Long loanId, Long customerId, String customerName, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, LocalDateTime policyStartDate, LocalDateTime policyEndDate, Integer page, Integer pageSize) throws Exception;

    ProductPropertyDto createProductProperty(ProductPropertyDto productProperty);
    ProductPropertyDto updateProductProperty(ProductPropertyDto productProperty);
    ProductPropertyDto getProductPropertyById(Long id);
    List<ProductPropertyDto> getProductProperties(Integer productId);

    // Provider methods
    ProviderDto createProvider(ProviderCreationDto providerCreationDto) throws Exception;
    ProviderDto updateProvider(ProviderDto providerDto) throws Exception;
    ProviderDto getProviderById(Long id) throws Exception;
    ProviderDto getProviderByCode(String code) throws Exception;
    PaginationDto<ProviderDto> filterProviders(String name, String code, Boolean isActive, Integer page, Integer pageSize) throws Exception;

    // Premium calculation methods
    PremiumCalculationDto recalculatePremiumForPolicy(Long policyId) throws Exception;
    List<PremiumCalculationDto> getPremiumCalculationsByPolicyId(Long policyId) throws Exception;
}
