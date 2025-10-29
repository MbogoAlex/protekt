package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.ProtektClaim;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProtektClaimDaoImpl implements  ProtektClaimDao {

    private final EntityManager entityManager;

    @Autowired
    public ProtektClaimDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ProtektClaim createClaim(ProtektClaim claim) {
        entityManager.persist(claim);
        return claim;
    }

    @Override
    public ProtektClaim updateClaim(ProtektClaim claim) {
        entityManager.merge(claim);
        return claim;
    }

    @Override
    public ProtektClaim getClaimById(Long id) {
        TypedQuery<ProtektClaim> query = entityManager.createQuery("from ProtektClaim where id = :id", ProtektClaim.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }
}
