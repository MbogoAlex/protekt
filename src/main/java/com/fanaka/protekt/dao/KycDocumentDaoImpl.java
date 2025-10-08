package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.KycDocument;
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
public class KycDocumentDaoImpl implements KycDocumentDao{

    private final EntityManager entityManager;

    @Autowired
    public KycDocumentDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public KycDocument createKycDocument(KycDocument kycDocument) {
        entityManager.persist(kycDocument);
        return kycDocument;
    }

    @Override
    public KycDocument updateKycDocument(KycDocument kycDocument) {
        entityManager.merge(kycDocument);
        return kycDocument;
    }

    @Override
    public KycDocument findKycDocumentById(Long id) {
        try {
            TypedQuery<KycDocument> query = entityManager.createQuery("from KycDocument where id = :id", KycDocument.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public KycDocument findKycDocumentByCustomerId(Long id) {
        try {
            TypedQuery<KycDocument> query = entityManager.createQuery("from KycDocument where customer.id = :id", KycDocument.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public KycDocument findKycDocumentByVerificationId(Long id) {
        try {
            TypedQuery<KycDocument> query = entityManager.createQuery("from KycDocument where customerVerification.id = :id", KycDocument.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Page<KycDocument> findAllKycDocuments(Long customerId, Long verificationId, Boolean verified, String documentType, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<KycDocument> query = cb.createQuery(KycDocument.class);
        Root<KycDocument> root = query.from(KycDocument.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Filter by customer ID
        if (customerId != null) {
            predicates.add(cb.equal(root.get("customer").get("id"), customerId));
        }
        
        // Filter by verification ID
        if (verificationId != null) {
            predicates.add(cb.equal(root.get("customerVerification").get("id"), verificationId));
        }
        
        // Filter by verified status
        if (verified != null) {
            predicates.add(cb.equal(root.get("verified"), verified));
        }
        
        // Filter by document type
        if (documentType != null && !documentType.trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("documentType")), documentType.trim().toLowerCase()));
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
        TypedQuery<KycDocument> typedQuery = entityManager.createQuery(query);
        
        // Set pagination
        int pageNumber = page != null ? page : 0;
        int size = pageSize != null ? pageSize : 10;
        
        // Get total count for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<KycDocument> countRoot = countQuery.from(KycDocument.class);
        
        // Apply same filters to count query
        List<Predicate> countPredicates = new ArrayList<>();
        
        if (customerId != null) {
            countPredicates.add(cb.equal(countRoot.get("customer").get("id"), customerId));
        }
        
        if (verificationId != null) {
            countPredicates.add(cb.equal(countRoot.get("customerVerification").get("id"), verificationId));
        }
        
        if (verified != null) {
            countPredicates.add(cb.equal(countRoot.get("verified"), verified));
        }
        
        if (documentType != null && !documentType.trim().isEmpty()) {
            countPredicates.add(cb.equal(cb.lower(countRoot.get("documentType")), documentType.trim().toLowerCase()));
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
        
        List<KycDocument> documents = typedQuery.getResultList();
        
        return new PageImpl<>(documents, PageRequest.of(pageNumber, size), totalElements);
    }
}
