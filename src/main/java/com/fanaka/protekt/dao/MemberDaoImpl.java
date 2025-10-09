package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDaoImpl implements MemberDao{

    private final EntityManager entityManager;

    @Autowired
    public MemberDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Member findMemberById(Long id) {
        TypedQuery<Member> query = entityManager.createQuery("from Member where id = :id", Member.class);
        query.setParameter("id", id);
        try {
           return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
