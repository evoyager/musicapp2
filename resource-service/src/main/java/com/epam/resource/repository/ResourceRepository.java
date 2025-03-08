package com.epam.resource.repository;

import com.epam.resource.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>, CustomResourceRepository {
}
