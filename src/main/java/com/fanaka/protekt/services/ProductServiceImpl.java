package com.fanaka.protekt.services;

import com.fanaka.protekt.dao.CustomerDao;
import com.fanaka.protekt.dao.LoanContractDao;
import com.fanaka.protekt.dao.ProductDao;
import com.fanaka.protekt.dto.*;
import com.fanaka.protekt.dto.mapper.ProductMapper;
import com.fanaka.protekt.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductDao productDao;
    private final CustomerDao customerDao;
    private final LoanContractDao loanContractDao;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(
            ProductDao productDao,
            CustomerDao customerDao,
            LoanContractDao loanContractDao,
            ProductMapper productMapper
    ) {
        this.productDao = productDao;
        this.customerDao = customerDao;
        this.loanContractDao = loanContractDao;
        this.productMapper = productMapper;
    }

    @Transactional
    @Override
    public ProductDto createProduct(ProductCreationDto productCreationDto) throws Exception {

        try {
            LocalDateTime now = LocalDateTime.now();

            Product product = Product.builder()
                    .provider(productCreationDto.getProvider())
                    .providerProductId(productCreationDto.getProviderProductId())
                    .name(productCreationDto.getName())
                    .description(productCreationDto.getDescription())
                    .productBeneficiaryType(productCreationDto.getProductBeneficiaryType())
                    .policyDuration(productCreationDto.getPolicyDuration())
                    .policyDurationType(productCreationDto.getPolicyDurationType())
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

            product.setProductTerms(productTerms);
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
            Customer customer = customerDao.getCustomerById(productPolicyCreationDto.getCustomerId());
            Product product = productDao.getProductById(productPolicyCreationDto.getProductId());

            Double loanAmount = Double.parseDouble(loanContract.getTotalDisbursed().toString());
            Double premiumPercentage = Double.parseDouble(productPolicyCreationDto.getPremiumPercentage());
            Double premiumValue = (premiumPercentage / 100) * loanAmount;

            ProductPolicy productPolicy = ProductPolicy.builder()
                    .product(product)
                    .customer(customer)
                    .loanAmount(String.valueOf(loanContract.getTotalDisbursed()))
                    .premiumPercentage(productPolicyCreationDto.getPremiumPercentage())
                    .premiumValue(String.valueOf(premiumValue))
                    .createdAt(Timestamp.valueOf(now))
                    .updatedAt(Timestamp.valueOf(now))
                    .policyStartDate(loanContract.getDisbursedAt().toLocalDateTime())
                    .policyEndDate(loanContract.getMaturityDate().toLocalDateTime())
                    .loanContract(loanContract)
                    .build();

            productDao.createProductPolicy(productPolicy);

            return productMapper.toProductPolicyDto(productPolicy);
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
}
