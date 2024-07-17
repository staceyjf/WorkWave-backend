package com.employee.workwave.Employee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByUsername(String username);
    Employee findByPublicId(String publicId);
}
