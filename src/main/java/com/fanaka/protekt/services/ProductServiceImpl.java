package com.fanaka.protekt.services;

import com.fanaka.protekt.dao.CustomerDao;
import com.fanaka.protekt.dao.LoanContractDao;
import com.fanaka.protekt.dao.ProductDao;
import com.fanaka.protekt.dto.*;
import com.fanaka.protekt.dto.mapper.ProductMapper;
import com.fanaka.protekt.dto.mapper.ProviderMapper;
import com.fanaka.protekt.dto.mapper.PremiumCalculationMapper;
import com.fanaka.protekt.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductDao productDao;
    private final CustomerDao customerDao;
    private final LoanContractDao loanContractDao;
    private final ProductMapper productMapper;
    private final ProviderMapper providerMapper;
    private final PremiumCalculationMapper premiumCalculationMapper;
    private final PremiumCalculationHelper premiumCalculationHelper;

    @Autowired
    public ProductServiceImpl(
            ProductDao productDao,
            CustomerDao customerDao,
            LoanContractDao loanContractDao,
            ProductMapper productMapper,
            ProviderMapper providerMapper,
            PremiumCalculationMapper premiumCalculationMapper,
            PremiumCalculationHelper premiumCalculationHelper
    ) {
        this.productDao = productDao;
        this.customerDao = customerDao;
        this.loanContractDao = loanContractDao;
        this.productMapper = productMapper;
        this.providerMapper = providerMapper;
        this.premiumCalculationMapper = premiumCalculationMapper;
        this.premiumCalculationHelper = premiumCalculationHelper;
    }

    @Transactional
    @Override
    public ProductDto createProduct(ProductCreationDto productCreationDto) throws Exception {

        try {
            LocalDateTime now = LocalDateTime.now();

            // Get provider entity if providerId is provided
            Provider providerEntity = null;
            if (productCreationDto.getProviderId() != null) {
                providerEntity = productDao.getProviderById(Long.valueOf(productCreationDto.getProviderId()));
                if (providerEntity == null) {
                    throw new Exception("Provider not found with ID: " + productCreationDto.getProviderId());
                }
            }

            Product product = Product.builder()
                    .provider(providerEntity == null ? productCreationDto.getProvider() : providerEntity.getName())// Keep for backward compatibility
                    .providerEntity(providerEntity) // Use the entity relationship
                    .providerProductId(productCreationDto.getProviderProductId())
                    .name(productCreationDto.getName())
                    .description(productCreationDto.getDescription())
                    .productBeneficiaryType(productCreationDto.getProductBeneficiaryType())
                    .policyDuration(productCreationDto.getPolicyDuration())
                    .policyDurationType(productCreationDto.getPolicyDurationType())
                    .premiumCalculationMethod(productCreationDto.getPremiumCalculationMethod())
                    .requiresComplexCalculation(productCreationDto.getRequiresComplexCalculation())
                    .productTerms(new ArrayList<>())
                    .createdAt(Timestamp.valueOf(now))
                    .updatedAt(Timestamp.valueOf(now))
                    .build();

            productDao.createProduct(product);

            List<ProductTerm> productTerms = new ArrayList<>();

            if(productCreationDto.getProductTerms() != null) {
                for(String term : productCreationDto.getProductTerms()) {
                    ProductTerm productTerm = ProductTerm.builder()
                            .term(term)
                            .product(product)
                            .createdAt(Timestamp.valueOf(now))
                            .updatedAt(Timestamp.valueOf(now))
                            .build();

                    productTerms.add(productDao.createProductTerm(productTerm));
                }
            }

            List<ProductProperty> productProperties = new ArrayList<>();

            if(productCreationDto.getProductProperties() != null) {
                for(ProductPropertyDto productPropertyDto : productCreationDto.getProductProperties()) {
                    ProductProperty productProperty = ProductProperty.builder()
                            .key(productPropertyDto.getKey())
                            .value(productPropertyDto.getValue())
                            .product(product)
                            .createdAt(Timestamp.valueOf(now))
                            .updatedAt(Timestamp.valueOf(now))
                            .build();
                    productProperties.add(productDao.createProductProperty(productProperty));
                }
            }

            product.setProductTerms(productTerms);
            product.setProductProperties(productProperties);
            productDao.updateProduct(product);

            return productMapper.toProductDto(product);
        } catch (Exception e) {
            throw new Exception("Failed: "+e);
        }
    }

    @Transactional
    @Override
    public ProductDto updateProduct(ProductUpdateDto productUpdateDto) throws Exception {

        try {
            LocalDateTime now = LocalDateTime.now();

            Product product = productDao.getProductById(productUpdateDto.getProductId());

            product.setProvider(productUpdateDto.getProvider());
            product.setProviderProductId(productUpdateDto.getProviderProductId());
            product.setName(productUpdateDto.getName());
            product.setDescription(productUpdateDto.getDescription());
            product.setPolicyDuration(productUpdateDto.getPolicyDuration());
            product.setPolicyDurationType(productUpdateDto.getPolicyDurationType());
            product.setUpdatedAt(Timestamp.valueOf(now));

            return productMapper.toProductDto(productDao.updateProduct(product));
        } catch (Exception e) {

            throw new Exception("Failed: "+e);
        }
    }

    @Override
    public ProductDto getProductById(Long id) throws Exception {
        try {
           return productMapper.toProductDto(productDao.getProductById(id));
        } catch (Exception e) {
            throw new Exception("Failed: "+e);
        }
    }

    @Override
    public PaginationDto<ProductDto> filterProducts(String provider, String name, String productBeneficiaryType, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize) throws Exception {
        try {
            var productsPage = productDao.filterProducts(provider, name, productBeneficiaryType, createdAtStartDate, createdAtEndDate, page, pageSize);
            
            List<ProductDto> productDtos = productsPage.getContent().stream()
                    .map(productMapper::toProductDto)
                    .toList();
            
            return new PaginationDto<>(
                    productDtos,
                    productsPage.getNumber(),
                    productsPage.getSize(),
                    productsPage.getTotalElements()
            );
        } catch (Exception e) {
            throw new Exception("Failed: "+e);
        }
    }

    @Transactional
    @Override
    public ProductTermDto createProductTerm(ProductTermCreationDto productTermCreationDto) throws Exception {
        try {
            LocalDateTime now = LocalDateTime.now();

            Product product = productDao.getProductById(productTermCreationDto.getProductId());

            ProductTerm productTerm = ProductTerm.builder()
                    .term(productTermCreationDto.getTerm())
                    .product(product)
                    .createdAt(Timestamp.valueOf(now))
                    .updatedAt(Timestamp.valueOf(now))
                    .build();

            productDao.createProductTerm(productTerm);

            return productMapper.toProductTermDto(productTerm);
        } catch (Exception e) {
            throw new Exception("Failed: "+e);
        }
    }

    @Transactional
    @Override
    public ProductTermDto updateProductTerm(ProductTermUpdateDto productTermUpdateDto) throws Exception {

        try {
            LocalDateTime now = LocalDateTime.now();

            ProductTerm productTerm = productDao.getProductTermById(productTermUpdateDto.getProductTermId());
            Product product = productDao.getProductById(productTermUpdateDto.getProductId());

            productTerm.setTerm(productTermUpdateDto.getTerm());
            productTerm.setProduct(product);
            productTerm.setUpdatedAt(Timestamp.valueOf(now));

            productDao.updateProductTerm(productTerm);

            return productMapper.toProductTermDto(productTerm);
        } catch (Exception e) {
            throw new Exception("Failed: "+e);
        }
    }

    @Override
    public ProductTermDto getProductTermById(Long id) throws Exception {
        try {
            return productMapper.toProductTermDto(productDao.getProductTermById(id));
        } catch (Exception e) {
            throw new Exception("Failed: "+e);
        }
    }

    @Override
    public PaginationDto<ProductTermDto> filterProductTerms(Integer productId, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize) throws Exception {
        try {
            var productTermsPage = productDao.filterProductTerms(productId, createdAtStartDate, createdAtEndDate, page, pageSize);
            
            List<ProductTermDto> productTermDtos = productTermsPage.getContent().stream()
                    .map(productMapper::toProductTermDto)
                    .toList();
            
            return new PaginationDto<>(
                    productTermDtos,
                    productTermsPage.getNumber(),
                    productTermsPage.getSize(),
                    productTermsPage.getTotalElements()
            );
        } catch (Exception e) {
            throw new Exception("Failed: "+e);
        }
    }

    @Transactional
    @Override
    public ProductPolicyDto createProductPolicy(ProductPolicyCreationDto productPolicyCreationDto) throws Exception {

        try {
            LocalDateTime now = LocalDateTime.now();

            LoanContract loanContract = loanContractDao.getLoanContractById(productPolicyCreationDto.getLoanId());
            ProductPolicy existingPolicy = productDao.getProductPolicyByLoanId(productPolicyCreationDto.getLoanId());

            if(existingPolicy != null) {
                throw new Exception("This loan is already insured");
            }


            Customer customer = customerDao.getCustomerById(productPolicyCreationDto.getCustomerId());

            if(customer == null) {
                throw new Exception("Customer not found");
            }

            Product product = productDao.getProductById(productPolicyCreationDto.getProductId());

            // Create initial policy with basic info
            ProductPolicy productPolicy = ProductPolicy.builder()
                    .product(product)
                    .customer(customer)
                    .loanAmount(String.valueOf(loanContract.getTotalDisbursed()))
                    .createdAt(Timestamp.valueOf(now))
                    .updatedAt(Timestamp.valueOf(now))
                    .policyStartDate(loanContract.getDisbursedAt().toLocalDateTime())
                    .policyEndDate(loanContract.getMaturityDate().toLocalDateTime())
                    .loanContract(loanContract)
                    .build();

            ProductPolicy savedPolicy = productDao.createProductPolicy(productPolicy);

            // Auto-calculate premium based on product calculation requirements
            if (product.getRequiresComplexCalculation() != null && product.getRequiresComplexCalculation()) {
                // Use complex calculation for providers like Hollard
                PremiumCalculation calculation = premiumCalculationHelper.calculatePremium(savedPolicy);

                // Update the saved policy with calculated values
                savedPolicy.setPremiumValue(calculation.getTotalPremium());
                savedPolicy.setPremiumPercentage(calculation.getPremiumRate());
                productDao.updateProductPolicy(savedPolicy);

                // Save the detailed calculation breakdown
                productDao.createPremiumCalculation(calculation);

                // Reload the policy to get the updated relationships including premiumCalculations
                savedPolicy = productDao.getProductPolicyById(savedPolicy.getId());
            } else {
                // Use simple calculation for internal products
                Double loanAmount = Double.parseDouble(loanContract.getTotalDisbursed().toString());
                Double premiumPercentage = Double.parseDouble(productPolicyCreationDto.getPremiumPercentage());
                Double premiumValue = (premiumPercentage / 100) * loanAmount;
                savedPolicy.setPremiumPercentage(productPolicyCreationDto.getPremiumPercentage());
                savedPolicy.setPremiumValue(String.valueOf(premiumValue));
                productDao.updateProductPolicy(savedPolicy);
            }

            return productMapper.toProductPolicyDto(savedPolicy);
        } catch (Exception e) {
            throw new Exception("Failed: "+e);
        }
    }

    @Transactional
    @Override
    public ProductPolicyDto updateProductPolicy(ProductPolicyUpdateDto productPolicyUpdateDto) throws Exception {

        try {
            LocalDateTime now = LocalDateTime.now();

            LoanContract loanContract = loanContractDao.getLoanContractById(productPolicyUpdateDto.getLoanId());
            Customer customer = customerDao.getCustomerById(productPolicyUpdateDto.getCustomerId());
            Product product = productDao.getProductById(productPolicyUpdateDto.getProductId());
            ProductPolicy productPolicy = productDao.getProductPolicyById(productPolicyUpdateDto.getProductPolicyId());

            Double loanAmount = Double.parseDouble(loanContract.getTotalDisbursed().toString());
            Double premiumPercentage = Double.parseDouble(productPolicyUpdateDto.getPremiumPercentage());
            Double premiumValue = (premiumPercentage / 100) * loanAmount;

            productPolicy.setLoanContract(loanContract);
            productPolicy.setCustomer(customer);
            productPolicy.setProduct(product);
            productPolicy.setPremiumPercentage(productPolicyUpdateDto.getPremiumPercentage());
            productPolicy.setPremiumValue(String.valueOf(premiumValue));
            productPolicy.setPolicyStartDate(loanContract.getDisbursedAt().toLocalDateTime());
            productPolicy.setPolicyEndDate(loanContract.getMaturityDate().toLocalDateTime());

            productPolicy.setUpdatedAt(Timestamp.valueOf(now));

            productDao.updateProductPolicy(productPolicy);

            return productMapper.toProductPolicyDto(productPolicy);
        } catch (Exception e) {
            throw new Exception("Failed: "+e);
        }
    }

    @Override
    public ProductPolicyDto getProductPolicyById(Long id) throws Exception {
        try {
            return productMapper.toProductPolicyDto(productDao.getProductPolicyById(id));
        } catch (Exception e) {
            throw new Exception("Failed: "+e);
        }
    }

    @Override
    public PaginationDto<ProductPolicyDto> filterProductPolicies(Integer productId, Long loanId, Long customerId, String customerName, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, LocalDateTime policyStartDate, LocalDateTime policyEndDate, Integer page, Integer pageSize) throws Exception {
        try {
            var productPoliciesPage = productDao.filterProductPolicies(productId, loanId, customerId, customerName, createdAtStartDate, createdAtEndDate, policyStartDate, policyEndDate, page, pageSize);
            
            List<ProductPolicyDto> productPolicyDtos = productPoliciesPage.getContent().stream()
                    .map(productMapper::toProductPolicyDto)
                    .toList();
            
            return new PaginationDto<>(
                    productPolicyDtos,
                    productPoliciesPage.getNumber(),
                    productPoliciesPage.getSize(),
                    productPoliciesPage.getTotalElements()
            );
        } catch (Exception e) {
            throw new Exception("Failed: "+e);
        }
    }

    @Transactional
    @Override
    public ProductPropertyDto createProductProperty(ProductPropertyDto productPropertyDto) {
        LocalDateTime now = LocalDateTime.now();
        Product product = productDao.getProductById(productPropertyDto.getProductId().longValue());
        ProductProperty productProperty = ProductProperty.builder()
                .key(productPropertyDto.getKey())
                .value(productPropertyDto.getValue())
                .valueType(productPropertyDto.getValueType())
                .product(product)
                .createdAt(Timestamp.valueOf(now))
                .updatedAt(Timestamp.valueOf(now))
                .build();
        return productMapper.toProductPropertyDto(productDao.createProductProperty(productProperty));
    }

    @Transactional
    @Override
    public ProductPropertyDto updateProductProperty(ProductPropertyDto productPropertyDto) {
        LocalDateTime now = LocalDateTime.now();
        ProductProperty productProperty = productDao.getProductPropertyById(productPropertyDto.getId());
        productProperty.setKey(productPropertyDto.getKey());
        productProperty.setValue(productPropertyDto.getValue());
        productProperty.setUpdatedAt(Timestamp.valueOf(now));
        return productMapper.toProductPropertyDto(productDao.updateProductProperty(productProperty));
    }

    @Override
    public ProductPropertyDto getProductPropertyById(Long id) {
        return productMapper.toProductPropertyDto(productDao.getProductPropertyById(id));
    }

    @Override
    public List<ProductPropertyDto> getProductProperties(Integer productId) {
        return productDao.getProductProperties(productId).stream().map(productMapper::toProductPropertyDto).collect(Collectors.toList());
    }

    // Provider methods implementation
    @Transactional
    @Override
    public ProviderDto createProvider(ProviderCreationDto providerCreationDto) throws Exception {
        try {
            LocalDateTime now = LocalDateTime.now();

            Provider provider = Provider.builder()
                    .name(providerCreationDto.getName())
                    .code(providerCreationDto.getCode())
                    .description(providerCreationDto.getDescription())
                    .contactEmail(providerCreationDto.getContactEmail())
                    .apiEndpoint(providerCreationDto.getApiEndpoint())
                    .isActive(providerCreationDto.getIsActive() != null ? providerCreationDto.getIsActive() : true)
                    .createdAt(Timestamp.valueOf(now))
                    .updatedAt(Timestamp.valueOf(now))
                    .build();

            Provider savedProvider = productDao.createProvider(provider);
            return providerMapper.toProviderDto(savedProvider);
        } catch (Exception e) {
            throw new Exception("Failed to create provider: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public ProviderDto updateProvider(ProviderDto providerDto) throws Exception {
        try {
            Provider existingProvider = productDao.getProviderById(providerDto.getId().longValue());
            if (existingProvider == null) {
                throw new Exception("Provider not found");
            }

            LocalDateTime now = LocalDateTime.now();

            existingProvider.setName(providerDto.getName());
            existingProvider.setCode(providerDto.getCode());
            existingProvider.setDescription(providerDto.getDescription());
            existingProvider.setContactEmail(providerDto.getContactEmail());
            existingProvider.setApiEndpoint(providerDto.getApiEndpoint());
            existingProvider.setIsActive(providerDto.getIsActive());
            existingProvider.setUpdatedAt(Timestamp.valueOf(now));

            Provider updatedProvider = productDao.updateProvider(existingProvider);
            return providerMapper.toProviderDto(updatedProvider);
        } catch (Exception e) {
            throw new Exception("Failed to update provider: " + e.getMessage());
        }
    }

    @Override
    public ProviderDto getProviderById(Long id) throws Exception {
        try {
            Provider provider = productDao.getProviderById(id);
            if (provider == null) {
                throw new Exception("Provider not found");
            }
            return providerMapper.toProviderDto(provider);
        } catch (Exception e) {
            throw new Exception("Failed to get provider: " + e.getMessage());
        }
    }

    @Override
    public ProviderDto getProviderByCode(String code) throws Exception {
        try {
            Provider provider = productDao.getProviderByCode(code);
            if (provider == null) {
                throw new Exception("Provider not found");
            }
            return providerMapper.toProviderDto(provider);
        } catch (Exception e) {
            throw new Exception("Failed to get provider by code: " + e.getMessage());
        }
    }

    @Override
    public PaginationDto<ProviderDto> filterProviders(String name, String code, Boolean isActive, Integer page, Integer pageSize) throws Exception {
        try {
            var providersPage = productDao.filterProviders(name, code, isActive, page, pageSize);

            List<ProviderDto> providerDtos = providersPage.getContent().stream()
                    .map(providerMapper::toProviderDto)
                    .toList();

            return new PaginationDto<>(
                    providerDtos,
                    providersPage.getNumber(),
                    providersPage.getSize(),
                    providersPage.getTotalElements()
            );
        } catch (Exception e) {
            throw new Exception("Failed to filter providers: " + e.getMessage());
        }
    }

    // Premium calculation methods implementation
    @Transactional
    @Override
    public PremiumCalculationDto recalculatePremiumForPolicy(Long policyId) throws Exception {
        try {
            ProductPolicy policy = productDao.getProductPolicyById(policyId);
            if (policy == null) {
                throw new Exception("Policy not found");
            }

            // Create new calculation (keeping history)
            PremiumCalculation newCalculation = premiumCalculationHelper.calculatePremium(policy);
            PremiumCalculation savedCalculation = productDao.createPremiumCalculation(newCalculation);

            return premiumCalculationMapper.toPremiumCalculationDto(savedCalculation);
        } catch (Exception e) {
            throw new Exception("Failed to recalculate premium: " + e.getMessage());
        }
    }

    @Override
    public List<PremiumCalculationDto> getPremiumCalculationsByPolicyId(Long policyId) throws Exception {
        try {
            List<PremiumCalculation> calculations = productDao.getPremiumCalculationsByPolicyId(policyId);
            return calculations.stream()
                    .map(premiumCalculationMapper::toPremiumCalculationDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Failed to get premium calculations: " + e.getMessage());
        }
    }
}
