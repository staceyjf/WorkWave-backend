package com.employee.workwave.Contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findById(Long id);

    @Query("SELECT COUNT(c) > 0 FROM Contract c WHERE (c.associatedEmployee.id = :employeeId) AND (c.contractType = 'CONTRACT') AND (c.employmentType = 'FULL_TIME') AND (c.endDate >= :startDate) ")
    Boolean hasCurrentContract(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate);
    // find the number of contracts that match
    // an employeeId AND are `full-time` contractor AND
    // where the new startdate is before or on the same day as the enddate of the
    // found contract

    @Query("SELECT COUNT(c) > 0 FROM Contract c WHERE (c.associatedEmployee.id = :employeeId) AND (c.contractType = 'PERMANENT') AND (c.endDate = null or c.endDate < :startDate)")
    Boolean isCurrentPermanentEmployee(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate);
}
