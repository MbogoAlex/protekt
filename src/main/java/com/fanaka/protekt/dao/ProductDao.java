package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.Product;
import com.fanaka.protekt.entities.ProductPolicy;
import com.fanaka.protekt.entities.ProductProperty;
import com.fanaka.protekt.entities.ProductTerm;
import com.fanaka.protekt.entities.Provider;
import com.fanaka.protekt.entities.PremiumCalculation;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

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
    ProductPolicy getProductPolicyByLoanId(Long loanId);
    Page<ProductPolicy> filterProductPolicies(Integer productId, Long loanId, Long customerId, String customerName, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, LocalDateTime policyStartDate, LocalDateTime policyEndDate, Integer page, Integer pageSize);

    ProductProperty createProductProperty(ProductProperty productProperty);
    ProductProperty updateProductProperty(ProductProperty productProperty);
    ProductProperty getProductPropertyById(Long id);
    List<ProductProperty> getProductProperties(Integer productId);

    // Provider methods
    Provider createProvider(Provider provider);
    Provider updateProvider(Provider provider);
    Provider getProviderById(Long id);
    Provider getProviderByCode(String code);
    Page<Provider> filterProviders(String name, String code, Boolean isActive, Integer page, Integer pageSize);

    // Premium calculation methods
    PremiumCalculation createPremiumCalculation(PremiumCalculation premiumCalculation);
    PremiumCalculation updatePremiumCalculation(PremiumCalculation premiumCalculation);
    PremiumCalculation getPremiumCalculationById(Long id);
    List<PremiumCalculation> getPremiumCalculationsByPolicyId(Long policyId);
}
