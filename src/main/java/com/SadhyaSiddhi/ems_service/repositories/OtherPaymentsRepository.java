package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.Advances;
import com.SadhyaSiddhi.ems_service.models.OtherPayments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OtherPaymentsRepository extends JpaRepository<OtherPayments, Long> {
    List<OtherPayments> findByConfigId(Long configId);
}

