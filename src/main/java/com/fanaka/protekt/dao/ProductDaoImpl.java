package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

    private final EntityManager entityManager;

    @Autowired
    public ProductDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Product createProduct(Product product) {
        entityManager.persist(product);
        entityManager.flush();
        return product;
    }

    @Override
    public Product updateProduct(Product product) {
        return entityManager.merge(product);
    }

    @Override
    public Product getProductById(Long id) {
        try {
            return entityManager.find(Product.class, id.intValue());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Page<Product> filterProducts(String provider, String name, String productBeneficiaryType, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        // Query for data
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Add filters
        if (provider != null && !provider.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("provider")), "%" + provider.toLowerCase() + "%"));
        }
        
        if (name != null && !name.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        
        if (productBeneficiaryType != null && !productBeneficiaryType.trim().isEmpty()) {
            predicates.add(cb.equal(cb.upper(root.get("productBeneficiaryType")), productBeneficiaryType.toUpperCase()));
        }
        
        if (createdAtStartDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), Timestamp.valueOf(createdAtStartDate)));
        }
        
        if (createdAtEndDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), Timestamp.valueOf(createdAtEndDate)));
        }
        
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        
        query.orderBy(cb.desc(root.get("createdAt")));
        
        // Set pagination
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;
        
        TypedQuery<Product> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageNumber * size);
        typedQuery.setMaxResults(size);
        
        List<Product> products = typedQuery.getResultList();
        
        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.count(countRoot));
        
        if (!predicates.isEmpty()) {
            // Rebuild predicates for count query
            List<Predicate> countPredicates = new ArrayList<>();
            
            if (provider != null && !provider.trim().isEmpty()) {
                countPredicates.add(cb.like(cb.lower(countRoot.get("provider")), "%" + provider.toLowerCase() + "%"));
            }
            
            if (name != null && !name.trim().isEmpty()) {
                countPredicates.add(cb.like(cb.lower(countRoot.get("name")), "%" + name.toLowerCase() + "%"));
            }
            
            if (productBeneficiaryType != null && !productBeneficiaryType.trim().isEmpty()) {
                countPredicates.add(cb.equal(cb.upper(countRoot.get("productBeneficiaryType")), productBeneficiaryType.toUpperCase()));
            }
            
            if (createdAtStartDate != null) {
                countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("createdAt"), Timestamp.valueOf(createdAtStartDate)));
            }
            
            if (createdAtEndDate != null) {
                countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("createdAt"), Timestamp.valueOf(createdAtEndDate)));
            }
            
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }
        
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();
        
        Pageable pageable = PageRequest.of(pageNumber, size);
        return new PageImpl<>(products, pageable, totalCount);
    }

    @Override
    public ProductTerm createProductTerm(ProductTerm productTerm) {
        entityManager.persist(productTerm);
        entityManager.flush();
        return productTerm;
    }

    @Override
    public ProductTerm updateProductTerm(ProductTerm productTerm) {
        return entityManager.merge(productTerm);
    }

    @Override
    public ProductTerm getProductTermById(Long id) {
        try {
            return entityManager.find(ProductTerm.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Page<ProductTerm> filterProductTerms(Integer productId, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        // Query for data
        CriteriaQuery<ProductTerm> query = cb.createQuery(ProductTerm.class);
        Root<ProductTerm> root = query.from(ProductTerm.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Add filters
        if (productId != null) {
            predicates.add(cb.equal(root.get("product").get("id"), productId));
        }
        
        if (createdAtStartDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), Timestamp.valueOf(createdAtStartDate)));
        }
        
        if (createdAtEndDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), Timestamp.valueOf(createdAtEndDate)));
        }
        
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        
        query.orderBy(cb.desc(root.get("createdAt")));
        
        // Set pagination
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;
        
        TypedQuery<ProductTerm> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageNumber * size);
        typedQuery.setMaxResults(size);
        
        List<ProductTerm> productTerms = typedQuery.getResultList();
        
        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ProductTerm> countRoot = countQuery.from(ProductTerm.class);
        countQuery.select(cb.count(countRoot));
        
        if (!predicates.isEmpty()) {
            // Rebuild predicates for count query
            List<Predicate> countPredicates = new ArrayList<>();
            
            if (productId != null) {
                countPredicates.add(cb.equal(countRoot.get("product").get("id"), productId));
            }
            
            if (createdAtStartDate != null) {
                countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("createdAt"), Timestamp.valueOf(createdAtStartDate)));
            }
            
            if (createdAtEndDate != null) {
                countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("createdAt"), Timestamp.valueOf(createdAtEndDate)));
            }
            
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }
        
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();
        
        Pageable pageable = PageRequest.of(pageNumber, size);
        return new PageImpl<>(productTerms, pageable, totalCount);
    }

    @Override
    public ProductPolicy createProductPolicy(ProductPolicy productPolicy) {
        entityManager.persist(productPolicy);
        entityManager.flush();
        return productPolicy;
    }

    @Override
    public ProductPolicy updateProductPolicy(ProductPolicy productPolicy) {
        return entityManager.merge(productPolicy);
    }

    @Override
    public ProductPolicy getProductPolicyById(Long id) {
        try {
            TypedQuery<ProductPolicy> query = entityManager.createQuery(
                "SELECT p FROM ProductPolicy p LEFT JOIN FETCH p.premiumCalculations WHERE p.id = :id",
                ProductPolicy.class
            );
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ProductPolicy getProductPolicyByLoanId(Long loanId) {
        try {
           TypedQuery<ProductPolicy> query = entityManager.createQuery("from ProductPolicy where loanContract.application = :loanId", ProductPolicy.class);
           query.setParameter("loanId", loanId);
           return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Page<ProductPolicy> filterProductPolicies(Integer productId, Long loanId, Long customerId, String customerName, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, LocalDateTime policyStartDate, LocalDateTime policyEndDate, Integer page, Integer pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        // Query for data
        CriteriaQuery<ProductPolicy> query = cb.createQuery(ProductPolicy.class);
        Root<ProductPolicy> root = query.from(ProductPolicy.class);
        
        // Joins for related entities
        Join<ProductPolicy, Customer> customerJoin = root.join("customer", JoinType.LEFT);
        Join<Customer, Member> memberJoin = customerJoin.join("member", JoinType.LEFT);
        Join<ProductPolicy, LoanContract> loanJoin = root.join("loanContract", JoinType.LEFT);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Add filters
        if (productId != null) {
            predicates.add(cb.equal(root.get("product").get("id"), productId));
        }
        
        // loanId refers to LoanContract.application
        if (loanId != null) {
            predicates.add(cb.equal(loanJoin.get("application"), loanId));
        }
        
        if (customerId != null) {
            predicates.add(cb.equal(customerJoin.get("id"), customerId));
        }
        
        if (customerName != null && !customerName.trim().isEmpty()) {
            String searchName = "%" + customerName.toLowerCase() + "%";
            Predicate firstNamePredicate = cb.like(cb.lower(memberJoin.get("firstName")), searchName);
            Predicate lastNamePredicate = cb.like(cb.lower(memberJoin.get("lastName")), searchName);
            Predicate middleNamePredicate = cb.like(cb.lower(memberJoin.get("middleName")), searchName);
            Predicate otherNamePredicate = cb.like(cb.lower(memberJoin.get("otherName")), searchName);
            
            predicates.add(cb.or(firstNamePredicate, lastNamePredicate, middleNamePredicate, otherNamePredicate));
        }
        
        if (createdAtStartDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), Timestamp.valueOf(createdAtStartDate)));
        }
        
        if (createdAtEndDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), Timestamp.valueOf(createdAtEndDate)));
        }
        
        if (policyStartDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("policyStartDate"), policyStartDate));
        }
        
        if (policyEndDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("policyEndDate"), policyEndDate));
        }
        
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        
        query.orderBy(cb.desc(root.get("createdAt")));
        
        // Set pagination
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;
        
        TypedQuery<ProductPolicy> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageNumber * size);
        typedQuery.setMaxResults(size);
        
        List<ProductPolicy> productPolicies = typedQuery.getResultList();
        
        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ProductPolicy> countRoot = countQuery.from(ProductPolicy.class);
        countQuery.select(cb.count(countRoot));
        
        // Joins for count query
        Join<ProductPolicy, Customer> countCustomerJoin = countRoot.join("customer", JoinType.LEFT);
        Join<Customer, Member> countMemberJoin = countCustomerJoin.join("member", JoinType.LEFT);
        Join<ProductPolicy, LoanContract> countLoanJoin = countRoot.join("loanContract", JoinType.LEFT);
        
        if (!predicates.isEmpty()) {
            // Rebuild predicates for count query
            List<Predicate> countPredicates = new ArrayList<>();
            
            if (productId != null) {
                countPredicates.add(cb.equal(countRoot.get("product").get("id"), productId));
            }
            
            if (loanId != null) {
                countPredicates.add(cb.equal(countLoanJoin.get("application"), loanId));
            }
            
            if (customerId != null) {
                countPredicates.add(cb.equal(countCustomerJoin.get("id"), customerId));
            }
            
            if (customerName != null && !customerName.trim().isEmpty()) {
                String searchName = "%" + customerName.toLowerCase() + "%";
                Predicate firstNamePredicate = cb.like(cb.lower(countMemberJoin.get("firstName")), searchName);
                Predicate lastNamePredicate = cb.like(cb.lower(countMemberJoin.get("lastName")), searchName);
                Predicate middleNamePredicate = cb.like(cb.lower(countMemberJoin.get("middleName")), searchName);
                Predicate otherNamePredicate = cb.like(cb.lower(countMemberJoin.get("otherName")), searchName);
                
                countPredicates.add(cb.or(firstNamePredicate, lastNamePredicate, middleNamePredicate, otherNamePredicate));
            }
            
            if (createdAtStartDate != null) {
                countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("createdAt"), Timestamp.valueOf(createdAtStartDate)));
            }
            
            if (createdAtEndDate != null) {
                countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("createdAt"), Timestamp.valueOf(createdAtEndDate)));
            }
            
            if (policyStartDate != null) {
                countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("policyStartDate"), policyStartDate));
            }
            
            if (policyEndDate != null) {
                countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("policyEndDate"), policyEndDate));
            }
            
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }
        
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();
        
        Pageable pageable = PageRequest.of(pageNumber, size);
        return new PageImpl<>(productPolicies, pageable, totalCount);
    }

    @Override
    public ProductProperty createProductProperty(ProductProperty productProperty) {
        entityManager.persist(productProperty);
        return productProperty;
    }

    @Override
    public ProductProperty updateProductProperty(ProductProperty productProperty) {
        entityManager.merge(productProperty);
        return productProperty;
    }

    @Override
    public ProductProperty getProductPropertyById(Long id) {
        TypedQuery<ProductProperty> query = entityManager.createQuery("from ProductProperty where  id = :productPropertyId", ProductProperty.class);
        query.setParameter("productPropertyId", id);

        try {
           return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ProductProperty> getProductProperties(Integer productId) {
        TypedQuery<ProductProperty> query =  entityManager.createQuery("from ProductProperty where product.id = :productId", ProductProperty.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }

    // Provider methods implementation
    @Override
    public Provider createProvider(Provider provider) {
        entityManager.persist(provider);
        entityManager.flush();
        return provider;
    }

    @Override
    public Provider updateProvider(Provider provider) {
        return entityManager.merge(provider);
    }

    @Override
    public Provider getProviderById(Long id) {
        try {
            TypedQuery<Provider> query = entityManager.createQuery("from Provider where id = :providerId", Provider.class);
            query.setParameter("providerId", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Provider getProviderByCode(String code) {
        try {
            TypedQuery<Provider> query = entityManager.createQuery("from Provider where code = :code", Provider.class);
            query.setParameter("code", code);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Page<Provider> filterProviders(String name, String code, Boolean isActive, Integer page, Integer pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Query for data
        CriteriaQuery<Provider> query = cb.createQuery(Provider.class);
        Root<Provider> root = query.from(Provider.class);

        List<Predicate> predicates = new ArrayList<>();

        // Add filters
        if (name != null && !name.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (code != null && !code.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
        }

        if (isActive != null) {
            predicates.add(cb.equal(root.get("isActive"), isActive));
        }

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        query.orderBy(cb.desc(root.get("createdAt")));

        // Set pagination
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

        TypedQuery<Provider> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageNumber * size);
        typedQuery.setMaxResults(size);

        List<Provider> providers = typedQuery.getResultList();

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Provider> countRoot = countQuery.from(Provider.class);
        countQuery.select(cb.count(countRoot));

        if (!predicates.isEmpty()) {
            // Rebuild predicates for count query
            List<Predicate> countPredicates = new ArrayList<>();

            if (name != null && !name.trim().isEmpty()) {
                countPredicates.add(cb.like(cb.lower(countRoot.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (code != null && !code.trim().isEmpty()) {
                countPredicates.add(cb.like(cb.lower(countRoot.get("code")), "%" + code.toLowerCase() + "%"));
            }

            if (isActive != null) {
                countPredicates.add(cb.equal(countRoot.get("isActive"), isActive));
            }

            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        Pageable pageable = PageRequest.of(pageNumber, size);
        return new PageImpl<>(providers, pageable, totalCount);
    }

    // Premium calculation methods implementation
    @Override
    public PremiumCalculation createPremiumCalculation(PremiumCalculation premiumCalculation) {
        entityManager.persist(premiumCalculation);
        entityManager.flush();
        return premiumCalculation;
    }

    @Override
    public PremiumCalculation updatePremiumCalculation(PremiumCalculation premiumCalculation) {
        return entityManager.merge(premiumCalculation);
    }

    @Override
    public PremiumCalculation getPremiumCalculationById(Long id) {
        try {
            return entityManager.find(PremiumCalculation.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<PremiumCalculation> getPremiumCalculationsByPolicyId(Long policyId) {
        try {
            TypedQuery<PremiumCalculation> query = entityManager.createQuery(
                "from PremiumCalculation where productPolicy.id = :policyId order by createdAt desc",
                PremiumCalculation.class
            );
            query.setParameter("policyId", policyId);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
