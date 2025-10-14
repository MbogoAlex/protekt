package com.fanaka.protekt.controllers;

import com.fanaka.protekt.config.BuildResponse;
import com.fanaka.protekt.dto.*;
import com.fanaka.protekt.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products/")
public class ProductControllerImpl implements ProductController{

    private final BuildResponse buildResponse;
    private final ProductService productService;

    public ProductControllerImpl(
            BuildResponse buildResponse,
            ProductService productService
    ) {
        this.buildResponse = buildResponse;
        this.productService = productService;
    }

    @PostMapping
    @Override
    public ResponseEntity<Object> createProduct(@RequestBody ProductCreationDto productCreationDto) throws Exception {
        try {
            ProductDto result = productService.createProduct(productCreationDto);
            return buildResponse.success(result, "Product created successfully", null, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to create product", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    @Override
    public ResponseEntity<Object> updateProduct(@RequestBody ProductUpdateDto productUpdateDto) throws Exception {
        try {
            ProductDto result = productService.updateProduct(productUpdateDto);
            return buildResponse.success(result, "Product updated successfully", null, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to update product", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<Object> getProductById(@PathVariable Long id) throws Exception {
        try {
            ProductDto result = productService.getProductById(id);
            if (result == null) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("general", "Product not found");
                return buildResponse.error("Product not found", errors, HttpStatus.NOT_FOUND);
            }
            return buildResponse.success(result, "Product found", null, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to retrieve product", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @Override
    public ResponseEntity<Object> filterProducts(
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String productBeneficiaryType,
            @RequestParam(required = false) LocalDateTime createdAtStartDate,
            @RequestParam(required = false) LocalDateTime createdAtEndDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) throws Exception {
        try {
            PaginationDto<ProductDto> result = productService.filterProducts(provider, name, productBeneficiaryType, createdAtStartDate, createdAtEndDate, page, pageSize);
            
            Map<String, Object> meta = new HashMap<>();
            meta.put("page", result.getPageNumber());
            meta.put("size", result.getPageSize());
            meta.put("total", result.getTotalElements());
            
            return buildResponse.success(result.getContent(), "Products retrieved successfully", meta, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to retrieve products", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/terms")
    @Override
    public ResponseEntity<Object> createProductTerm(@RequestBody ProductTermCreationDto productTermCreationDto) throws Exception {
        try {
            ProductTermDto result = productService.createProductTerm(productTermCreationDto);
            return buildResponse.success(result, "Product term created successfully", null, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to create product term", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/terms")
    @Override
    public ResponseEntity<Object> updateProductTerm(@RequestBody ProductTermUpdateDto productTermUpdateDto) throws Exception {
        try {
            ProductTermDto result = productService.updateProductTerm(productTermUpdateDto);
            return buildResponse.success(result, "Product term updated successfully", null, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to update product term", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/terms/{id}")
    @Override
    public ResponseEntity<Object> getProductTermById(@PathVariable Long id) throws Exception {
        try {
            ProductTermDto result = productService.getProductTermById(id);
            if (result == null) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("general", "Product term not found");
                return buildResponse.error("Product term not found", errors, HttpStatus.NOT_FOUND);
            }
            return buildResponse.success(result, "Product term found", null, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to retrieve product term", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/terms")
    @Override
    public ResponseEntity<Object> filterProductTerms(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) LocalDateTime createdAtStartDate,
            @RequestParam(required = false) LocalDateTime createdAtEndDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) throws Exception {
        try {
            PaginationDto<ProductTermDto> result = productService.filterProductTerms(productId, createdAtStartDate, createdAtEndDate, page, pageSize);
            
            Map<String, Object> meta = new HashMap<>();
            meta.put("page", result.getPageNumber());
            meta.put("size", result.getPageSize());
            meta.put("total", result.getTotalElements());
            
            return buildResponse.success(result.getContent(), "Product terms retrieved successfully", meta, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to retrieve product terms", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/policies")
    @Override
    public ResponseEntity<Object> createProductPolicy(@RequestBody ProductPolicyCreationDto productPolicyCreationDto) throws Exception {
        try {
            ProductPolicyDto result = productService.createProductPolicy(productPolicyCreationDto);
            return buildResponse.success(result, "Product policy created successfully", null, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to create product policy", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/policies")
    @Override
    public ResponseEntity<Object> updateProductPolicy(@RequestBody ProductPolicyUpdateDto productPolicyUpdateDto) throws Exception {
        try {
            ProductPolicyDto result = productService.updateProductPolicy(productPolicyUpdateDto);
            return buildResponse.success(result, "Product policy updated successfully", null, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to update product policy", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/policies/{id}")
    @Override
    public ResponseEntity<Object> getProductPolicyById(@PathVariable Long id) throws Exception {
        try {
            ProductPolicyDto result = productService.getProductPolicyById(id);
            if (result == null) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("general", "Product policy not found");
                return buildResponse.error("Product policy not found", errors, HttpStatus.NOT_FOUND);
            }
            return buildResponse.success(result, "Product policy found", null, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to retrieve product policy", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/policies")
    @Override
    public ResponseEntity<Object> filterProductPolicies(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Long loanId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) LocalDateTime createdAtStartDate,
            @RequestParam(required = false) LocalDateTime createdAtEndDate,
            @RequestParam(required = false) LocalDateTime policyStartDate,
            @RequestParam(required = false) LocalDateTime policyEndDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) throws Exception {
        try {
            PaginationDto<ProductPolicyDto> result = productService.filterProductPolicies(productId, loanId, customerId, customerName, createdAtStartDate, createdAtEndDate, policyStartDate, policyEndDate, page, pageSize);
            
            Map<String, Object> meta = new HashMap<>();
            meta.put("page", result.getPageNumber());
            meta.put("size", result.getPageSize());
            meta.put("total", result.getTotalElements());
            
            return buildResponse.success(result.getContent(), "Product policies retrieved successfully", meta, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to retrieve product policies", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
