package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.Product;
import com.fanaka.protekt.entities.ProductPolicy;
import com.fanaka.protekt.entities.ProductTerm;
import com.fanaka.protekt.entities.Customer;
import com.fanaka.protekt.entities.LoanContract;
import com.fanaka.protekt.entities.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
            return entityManager.find(ProductPolicy.class, id);
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
}
