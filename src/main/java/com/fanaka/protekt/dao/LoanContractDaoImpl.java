package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.LoanContract;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LoanContractDaoImpl implements LoanContractDao {

    private final EntityManager entityManager;

    @Autowired
    public LoanContractDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public LoanContract getLoanContractById(Long id) {
        try {
            TypedQuery<LoanContract> query = entityManager.createQuery("from LoanContract where application = :id", LoanContract.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
