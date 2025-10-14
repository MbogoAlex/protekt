package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.Product;
import com.fanaka.protekt.entities.ProductPolicy;
import com.fanaka.protekt.entities.ProductTerm;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface ProductDao {
    Product createProduct(Product product);
    Product updateProduct(Product product);
    Product getProductById(Long id);
    Page<Product> filterProducts(String provider, String name, String productBeneficiaryType, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize);

    ProductTerm createProductTerm(ProductTerm productTerm);
    ProductTerm updateProductTerm(ProductTerm productTerm);
    ProductTerm getProductTermById(Long id);
    Page<ProductTerm> filterProductTerms(Integer productId, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize);

    ProductPolicy createProductPolicy(ProductPolicy productPolicy);
    ProductPolicy updateProductPolicy(ProductPolicy productPolicy);
    ProductPolicy getProductPolicyById(Long id);
    Page<ProductPolicy> filterProductPolicies(Integer productId, Long loanId, Long customerId, String customerName, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, LocalDateTime policyStartDate, LocalDateTime policyEndDate, Integer page, Integer pageSize);
}
