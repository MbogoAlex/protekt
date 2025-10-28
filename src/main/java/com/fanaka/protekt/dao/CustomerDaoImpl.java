package com.fanaka.protekt.dao;

import com.fanaka.protekt.dto.CustomerCheckDto;
import com.fanaka.protekt.entities.Customer;
import com.fanaka.protekt.entities.LoanContract;
import com.fanaka.protekt.entities.Member;
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
public class CustomerDaoImpl implements CustomerDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomerDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        entityManager.persist(customer);
        return customer;
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        entityManager.merge(customer);
        return customer;
    }

    @Override
    public Customer getCustomerById(Long id) {
        TypedQuery<Customer> query = entityManager.createQuery("from Customer c where c.id = :id", Customer.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Customer getCustomerByMemberId(Long memberId) {
        try {
            TypedQuery<Customer> query = entityManager.createQuery("from Customer where member.id = :memberId", Customer.class);
            query.setParameter("memberId", memberId);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Page<Customer> filterCustomers(String name, String nrc, String email, String gender, String verificationStatus, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
        Root<Customer> root = query.from(Customer.class);
        
        Join<Customer, com.fanaka.protekt.entities.Member> memberJoin = root.join("member", JoinType.LEFT);
        Join<Customer, com.fanaka.protekt.entities.CustomerVerification> verificationJoin = root.join("customerVerification", JoinType.LEFT);
        
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
            predicates.add(cb.equal(cb.lower(verificationJoin.get("verificationStatus")), verificationStatus.trim().toLowerCase()));
        }
        
        // Filter by created date range
        if (createdAtStartDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), Timestamp.valueOf(createdAtStartDate)));
        }
        
        if (createdAtEndDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), Timestamp.valueOf(createdAtEndDate)));
        }
        
        // Apply all predicates
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        
        // Order by created date descending
        query.orderBy(cb.desc(root.get("createdAt")));
        
        // Create typed query
        TypedQuery<Customer> typedQuery = entityManager.createQuery(query);
        
        // Set pagination
        int pageNumber = page != null ? page : 0;
        int size = pageSize != null ? pageSize : 10;
        
        // Get total count for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Customer> countRoot = countQuery.from(Customer.class);
        Join<Customer, com.fanaka.protekt.entities.Member> countMemberJoin = countRoot.join("member", JoinType.LEFT);
        Join<Customer, com.fanaka.protekt.entities.CustomerVerification> countVerificationJoin = countRoot.join("customerVerification", JoinType.LEFT);
        
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
            countPredicates.add(cb.equal(cb.lower(countVerificationJoin.get("verificationStatus")), verificationStatus.trim().toLowerCase()));
        }
        
        if (createdAtStartDate != null) {
            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("createdAt"), Timestamp.valueOf(createdAtStartDate)));
        }
        
        if (createdAtEndDate != null) {
            countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("createdAt"), Timestamp.valueOf(createdAtEndDate)));
        }
        
        countQuery.select(cb.count(countRoot));
        if (!countPredicates.isEmpty()) {
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }
        
        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        
        // Apply pagination to main query
        typedQuery.setFirstResult(pageNumber * size);
        typedQuery.setMaxResults(size);
        
        List<Customer> customers = typedQuery.getResultList();
        
        return new PageImpl<>(customers, PageRequest.of(pageNumber, size), totalElements);
    }

    @Override
    public CustomerCheckDto getCustomerCheckDetails(String phone, String nrc) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            // Initialize return values
            Boolean isMember = false;
            Boolean isProtektCustomer = false;
            Boolean hasActiveLoan = false;
            Boolean activeLoanIsInsured = false;
            Long memberId = null;
            Long customerId = null;

            // 1. Check if person is a member with type = "CUSTOMER" by phone or NRC
            if (phone != null || nrc != null) {
                CriteriaQuery<Member> memberQuery = cb.createQuery(Member.class);
                Root<Member> memberRoot = memberQuery.from(Member.class);

                List<Predicate> memberPredicates = new ArrayList<>();

                // Filter by type = "CUSTOMER"
                memberPredicates.add(cb.equal(memberRoot.get("type"), "CUSTOMER"));

                // Filter by phone or NRC
                if (phone != null && nrc != null) {
                    memberPredicates.add(cb.or(
                        cb.equal(memberRoot.get("mobile"), phone),
                        cb.equal(memberRoot.get("idNumber"), nrc)
                    ));
                } else if (phone != null) {
                    memberPredicates.add(cb.equal(memberRoot.get("mobile"), phone));
                } else if (nrc != null) {
                    memberPredicates.add(cb.equal(memberRoot.get("idNumber"), nrc));
                }

                memberQuery.where(cb.and(memberPredicates.toArray(new Predicate[0])));

                TypedQuery<Member> memberTypedQuery = entityManager.createQuery(memberQuery);
                List<Member> members = memberTypedQuery.getResultList();

                if (!members.isEmpty()) {
                    isMember = true;
                    Member member = members.get(0); // Take first match
                    memberId = member.getId();

                    // 2. Check if this member is in protekt_customers table
                    CriteriaQuery<Customer> customerQuery = cb.createQuery(Customer.class);
                    Root<Customer> customerRoot = customerQuery.from(Customer.class);

                    customerQuery.where(cb.equal(customerRoot.get("member"), member));

                    TypedQuery<Customer> customerTypedQuery = entityManager.createQuery(customerQuery);
                    List<Customer> customers = customerTypedQuery.getResultList();

                    if (!customers.isEmpty()) {
                        isProtektCustomer = true;
                        customerId = customers.get(0).getId();
                    }

                    // 3. Check for active loans using simple native query
                    try {
                        String activeLoanQuery = """
                            SELECT COUNT(*)
                            FROM lms_loan_contracts lc
                            JOIN lms_loan_applications la ON lc.application = la.id
                            WHERE lc.status = 'ACTIVE'
                            AND (la.customer_id = ? OR la.member_id = ?)
                        """;

                        Long activeLoanCount = ((Number) entityManager.createNativeQuery(activeLoanQuery)
                            .setParameter(1, customerId)
                            .setParameter(2, memberId)
                            .getSingleResult()).longValue();
                        hasActiveLoan = activeLoanCount > 0;

                        // 4. If there's an active loan, check if it has insurance policy
                        if (hasActiveLoan) {
                            String activeLoanWithPolicyQuery = """
                                SELECT COUNT(*)
                                FROM lms_loan_contracts lc
                                JOIN lms_loan_applications la ON lc.application = la.id
                                JOIN protekt_product_policies pp ON pp.loan_contract_id = lc.application
                                WHERE lc.status = 'ACTIVE'
                                AND (la.customer_id = ? OR la.member_id = ?)
                            """;

                            Long activeLoanWithPolicyCount = ((Number) entityManager.createNativeQuery(activeLoanWithPolicyQuery)
                                .setParameter(1, customerId)
                                .setParameter(2, memberId)
                                .getSingleResult()).longValue();
                            activeLoanIsInsured = activeLoanWithPolicyCount > 0;
                        }
                    } catch (Exception loanCheckException) {
                        // If loan tables don't exist or query fails, set safe defaults
                        hasActiveLoan = false;
                        activeLoanIsInsured = false;
                    }
                }
            }

            // Ensure boolean values are never null
            isMember = (isMember != null) ? isMember : false;
            isProtektCustomer = (isProtektCustomer != null) ? isProtektCustomer : false;
            hasActiveLoan = (hasActiveLoan != null) ? hasActiveLoan : false;
            activeLoanIsInsured = (activeLoanIsInsured != null) ? activeLoanIsInsured : false;

            return new CustomerCheckDto(
                isProtektCustomer,
                isMember,
                hasActiveLoan,
                activeLoanIsInsured,
                customerId,
                memberId
            );

        } catch (Exception e) {
            // Log error and return safe defaults
            e.printStackTrace();
            return new CustomerCheckDto(false, false, false, false, null, null);
        }
    }
}
