package com.fanaka.protekt.controllers;

import com.fanaka.protekt.dto.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface ProductController {
    ResponseEntity<Object> createProduct(ProductCreationDto productCreationDto) throws Exception;
    ResponseEntity<Object> updateProduct(ProductUpdateDto productUpdateDto) throws Exception;
    ResponseEntity<Object> getProductById(Long id) throws Exception;
    ResponseEntity<Object> filterProducts(String provider, String name, String productBeneficiaryType, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize) throws Exception;

    ResponseEntity<Object> createProductTerm(ProductTermCreationDto productTermCreationDto) throws Exception;
    ResponseEntity<Object> updateProductTerm(ProductTermUpdateDto productTermUpdateDto) throws Exception;
    ResponseEntity<Object> getProductTermById(Long id) throws Exception;
    ResponseEntity<Object> filterProductTerms(Integer productId, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize) throws Exception;

    ResponseEntity<Object> createProductPolicy(ProductPolicyCreationDto productPolicyCreationDto) throws Exception;
    ResponseEntity<Object> updateProductPolicy(ProductPolicyUpdateDto productPolicyUpdateDto) throws Exception;
    ResponseEntity<Object> getProductPolicyById(Long id) throws Exception;
    ResponseEntity<Object> filterProductPolicies(Integer productId, Long loanId, Long customerId, String customerName, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, LocalDateTime policyStartDate, LocalDateTime policyEndDate, Integer page, Integer pageSize) throws Exception;

    ResponseEntity<Object> createProductProperty(ProductPropertyDto productProperty);
    ResponseEntity<Object> updateProductProperty(ProductPropertyDto productProperty);
    ResponseEntity<Object> getProductPropertyById(Long id);
    ResponseEntity<Object> getProductProperties(Integer productId);

    // Provider methods
    ResponseEntity<Object> createProvider(ProviderCreationDto providerCreationDto) throws Exception;
    ResponseEntity<Object> updateProvider(ProviderDto providerDto) throws Exception;
    ResponseEntity<Object> getProviderById(Long id) throws Exception;
    ResponseEntity<Object> getProviderByCode(String code) throws Exception;
    ResponseEntity<Object> filterProviders(String name, String code, Boolean isActive, Integer page, Integer pageSize) throws Exception;

    // Premium calculation methods
    ResponseEntity<Object> recalculatePremiumForPolicy(Long policyId) throws Exception;
    ResponseEntity<Object> getPremiumCalculationsByPolicyId(Long policyId) throws Exception;
}
