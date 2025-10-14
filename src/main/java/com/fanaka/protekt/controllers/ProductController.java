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
}
