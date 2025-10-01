package com.SadhyaSiddhi.ems_service.repositories;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryConfig;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface EmployeeSalaryLogRepository extends JpaRepository<EmployeeSalaryLog, Long> {
    @Query("""
    SELECT l FROM EmployeeSalaryLog l 
    WHERE l.config.id = :configId 
      AND l.salaryMonth = :salaryMonth 
      AND l.status = 'DONE'
""")
    List<EmployeeSalaryLog> findLogsByConfigAndMonthAndDone(@Param("configId") Long configId,
                                                            @Param("salaryMonth") String salaryMonth);


}
