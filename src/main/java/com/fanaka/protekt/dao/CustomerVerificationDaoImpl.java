package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.CustomerVerification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerVerificationDaoImpl implements CustomerVerificationDao{

    private final EntityManager entityManager;

    @Autowired
    public CustomerVerificationDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CustomerVerification createCustomerVerification(CustomerVerification customerVerification) {
        entityManager.persist(customerVerification);
        return customerVerification;
    }

    @Override
    public CustomerVerification updateCustomer(CustomerVerification customerVerification) {
        entityManager.merge(customerVerification);
        return customerVerification;
    }

    @Override
    public CustomerVerification getCustomerVerificationById(Long id) {
        TypedQuery<CustomerVerification> query = entityManager.createQuery("from CustomerVerification where id = :id", CustomerVerification.class);
        query.setParameter("id", id);

        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CustomerVerification getCustomerVerificationByCustomerId(Long id) {
        TypedQuery<CustomerVerification> query = entityManager.createQuery("from CustomerVerification where customer.id = :id", CustomerVerification.class);
        query.setParameter("id", id);

        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Page<CustomerVerification> filterCustomerVerifications(String name, String nrc, String email, String gender, String verificationStatus, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, LocalDateTime statusChangedAtStartDate, LocalDateTime statusChangedAtEndDate, Integer page, Integer pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerVerification> query = cb.createQuery(CustomerVerification.class);
        Root<CustomerVerification> root = query.from(CustomerVerification.class);
        
        Join<CustomerVerification, com.fanaka.protekt.entities.Customer> customerJoin = root.join("customer", JoinType.LEFT);
        Join<com.fanaka.protekt.entities.Customer, com.fanaka.protekt.entities.Member> memberJoin = customerJoin.join("member", JoinType.LEFT);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Filter by name (search in firstName, lastName, middleName, otherName)
        if (name != null && !name.trim().isEmpty()) {
            String namePattern = "%" + name.trim().toLowerCase() + "%";
            Predicate namePredicate = cb.or(
                cb.like(cb.lower(memberJoin.get("firstName")), namePattern),
                cb.like(cb.lower(memberJoin.get("lastName")), namePattern),
                cb.like(cb.lower(memberJoin.get("middleName")), namePattern),
                cb.like(cb.lower(memberJoin.get("otherName")), namePattern)
            );
            predicates.add(namePredicate);
        }
        
        // Filter by NRC (ID number)
        if (nrc != null && !nrc.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(memberJoin.get("idNumber")), "%" + nrc.trim().toLowerCase() + "%"));
        }
        
        // Filter by email (assuming mobile field serves as contact)
        if (email != null && !email.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(memberJoin.get("mobile")), "%" + email.trim().toLowerCase() + "%"));
        }
        
        // Filter by gender
        if (gender != null && !gender.trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(memberJoin.get("gender")), gender.trim().toLowerCase()));
        }
        
        // Filter by verification status
        if (verificationStatus != null && !verificationStatus.trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("verificationStatus")), verificationStatus.trim().toLowerCase()));
        }
        
        // Filter by created date range
        if (createdAtStartDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), Timestamp.valueOf(createdAtStartDate)));
        }
        
        if (createdAtEndDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), Timestamp.valueOf(createdAtEndDate)));
        }
        
        // Filter by status changed date range
        if (statusChangedAtStartDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("statusChangedAt"), Timestamp.valueOf(statusChangedAtStartDate)));
        }
        
        if (statusChangedAtEndDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("statusChangedAt"), Timestamp.valueOf(statusChangedAtEndDate)));
        }
        
        // Apply all predicates
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        
        // Order by created date descending
        query.orderBy(cb.desc(root.get("createdAt")));
        
        // Create typed query
        TypedQuery<CustomerVerification> typedQuery = entityManager.createQuery(query);
        
        // Set pagination
        int pageNumber = page != null ? page : 0;
        int size = pageSize != null ? pageSize : 10;
        
        // Get total count for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CustomerVerification> countRoot = countQuery.from(CustomerVerification.class);
        Join<CustomerVerification, com.fanaka.protekt.entities.Customer> countCustomerJoin = countRoot.join("customer", JoinType.LEFT);
        Join<com.fanaka.protekt.entities.Customer, com.fanaka.protekt.entities.Member> countMemberJoin = countCustomerJoin.join("member", JoinType.LEFT);
        
        // Apply same filters to count query
        List<Predicate> countPredicates = new ArrayList<>();
        
        if (name != null && !name.trim().isEmpty()) {
            String namePattern = "%" + name.trim().toLowerCase() + "%";
            Predicate namePredicate = cb.or(
                cb.like(cb.lower(countMemberJoin.get("firstName")), namePattern),
                cb.like(cb.lower(countMemberJoin.get("lastName")), namePattern),
                cb.like(cb.lower(countMemberJoin.get("middleName")), namePattern),
                cb.like(cb.lower(countMemberJoin.get("otherName")), namePattern)
            );
            countPredicates.add(namePredicate);
        }
        
        if (nrc != null && !nrc.trim().isEmpty()) {
            countPredicates.add(cb.like(cb.lower(countMemberJoin.get("idNumber")), "%" + nrc.trim().toLowerCase() + "%"));
        }
        
        if (email != null && !email.trim().isEmpty()) {
            countPredicates.add(cb.like(cb.lower(countMemberJoin.get("mobile")), "%" + email.trim().toLowerCase() + "%"));
        }
        
        if (gender != null && !gender.trim().isEmpty()) {
            countPredicates.add(cb.equal(cb.lower(countMemberJoin.get("gender")), gender.trim().toLowerCase()));
        }
        
        if (verificationStatus != null && !verificationStatus.trim().isEmpty()) {
            countPredicates.add(cb.equal(cb.lower(countRoot.get("verificationStatus")), verificationStatus.trim().toLowerCase()));
        }
        
        if (createdAtStartDate != null) {
            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("createdAt"), Timestamp.valueOf(createdAtStartDate)));
        }
        
        if (createdAtEndDate != null) {
            countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("createdAt"), Timestamp.valueOf(createdAtEndDate)));
        }
        
        if (statusChangedAtStartDate != null) {
            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("statusChangedAt"), Timestamp.valueOf(statusChangedAtStartDate)));
        }
        
        if (statusChangedAtEndDate != null) {
            countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("statusChangedAt"), Timestamp.valueOf(statusChangedAtEndDate)));
        }
        
        countQuery.select(cb.count(countRoot));
        if (!countPredicates.isEmpty()) {
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }
        
        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        
        // Apply pagination to main query
        typedQuery.setFirstResult(pageNumber * size);
        typedQuery.setMaxResults(size);
        
        List<CustomerVerification> verifications = typedQuery.getResultList();
        
        return new PageImpl<>(verifications, PageRequest.of(pageNumber, size), totalElements);
    }
}
