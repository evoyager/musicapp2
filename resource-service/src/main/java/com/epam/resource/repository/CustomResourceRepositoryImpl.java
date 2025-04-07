package com.epam.resource.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CustomResourceRepositoryImpl implements CustomResourceRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<Long> deleteAllByIdInReturnIds(List<Long> ids) {
        // Check which IDs exist
        List<Long> existingIds = entityManager.createQuery(
                        "SELECT r.id FROM Resource r WHERE r.id IN :ids", Long.class)
                .setParameter("ids", ids)
                .getResultList();

        if (!existingIds.isEmpty()) {
            // Delete entities by existing IDs
            Query deleteQuery = entityManager.createQuery(
                            "DELETE FROM Resource r WHERE r.id IN :ids")
                    .setParameter("ids", existingIds);
            deleteQuery.executeUpdate();
        }

        // Return the IDs of deleted entities
        return existingIds;
    }
}