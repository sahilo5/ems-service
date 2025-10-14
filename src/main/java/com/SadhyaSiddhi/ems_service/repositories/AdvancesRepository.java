package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.Advances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvancesRepository extends JpaRepository<Advances, Long> {
    List<Advances> findByConfigId(Long configId);
}

