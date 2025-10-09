package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.ProtektFile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProtektFileDaoImpl implements ProtektFileDao {

    private final EntityManager entityManager;

    @Autowired
    public ProtektFileDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ProtektFile createProtektFile(ProtektFile protektFile) {
        entityManager.persist(protektFile);
        return protektFile;
    }

    @Override
    public ProtektFile updateProtektFile(ProtektFile protektFile) {
        return entityManager.merge(protektFile);
    }

    @Override
    public ProtektFile findProtektFileById(Long id) {
        TypedQuery<ProtektFile> query = entityManager.createQuery("from ProtektFile where id = :id", ProtektFile.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void deleteProtektFile(Long id) {
        ProtektFile protektFile = findProtektFileById(id);
        if (protektFile != null) {
            entityManager.remove(protektFile);
        }
    }
}