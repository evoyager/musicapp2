package com.epam.song.repository;

import com.epam.song.exceptions.SongIdExistsException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CustomSongRepositoryImpl implements CustomSongRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void checkSongIdExists(Long id) {
        String query = "SELECT COUNT(s) FROM Song s WHERE s.id = :id";
        Long count = entityManager.createQuery(query, Long.class)
                .setParameter("id", id)
                .getSingleResult();
        if (count > 0) {
            throw new SongIdExistsException("Song with ID " + id + " already exists.");
        }
    }

    @Override
    @Transactional
    public List<Long> deleteAllByIdInReturnIds(List<Long> ids) {
        // Check which IDs exist
        List<Long> existingIds = entityManager.createQuery(
                        "SELECT r.id FROM Song r WHERE r.id IN :ids", Long.class)
                .setParameter("ids", ids)
                .getResultList();

        if (!existingIds.isEmpty()) {
            // Delete entities by existing IDs
            Query deleteQuery = entityManager.createQuery(
                            "DELETE FROM Song r WHERE r.id IN :ids")
                    .setParameter("ids", existingIds);
            deleteQuery.executeUpdate();
        }

        // Return the IDs of deleted entities
        return existingIds;
    }
}