package com.employee.workwave.Department;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.employee.workwave.Employee.Employee;
import com.employee.workwave.Employee.EmployeeService;
import com.employee.workwave.exceptions.ServiceValidationException;
import com.employee.workwave.exceptions.ValidationErrors;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Service
@Transactional // auto rolls back db if failure
public class DepartmentService {

    private static final Logger fullLogsLogger = LogManager.getLogger("fullLogs");

    @Autowired
    private DepartmentRepository repo;

    @Autowired
    private EmployeeService employeeService;

    public Department createDepartment(@Valid CreateDepartmentDTO data) throws ServiceValidationException {
        ValidationErrors errors = new ValidationErrors();
        Department newDepartment = new Department();

        String trimmedNameField = data.getDepartmentName().trim();
        if (trimmedNameField.isBlank()) {
            errors.addError("Department", "Department field must contain a value.");
        }

        if (!data.getEmployeeIds().isEmpty()) {
            for (Long id : data.getEmployeeIds()) {
                Optional<Employee> maybeEmployee = this.employeeService.findById(id);

                if (maybeEmployee.isEmpty()) {
                    errors.addError("Employee", String.format("Employee with id %s does not exist", id));
                } else {
                    newDepartment.addEmployee(maybeEmployee.get());
                    // using the helper function to update both
                    // department's associatedEmployees and
                    // employee's department
                    // making the relationship bi-directional
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new ServiceValidationException(errors);
        }

        // update with DTO data post validation & data cleaning
        newDepartment.setDepartmentName(trimmedNameField);

        // as the unique constraint is at the bd level
        try {
            Department savedDepartment = this.repo.save(newDepartment);
            fullLogsLogger.info("Created new department in db with ID: " + savedDepartment.getId());
            return savedDepartment;
        } catch (DataIntegrityViolationException err) {
            if (err.getCause() instanceof ConstraintViolationException) {
                errors.addError("Department", "Department already exists. Please review and re-add if needed.");
            } else {
                errors.addError("Department", "There was an issue saving a new department to the database");
            }
            throw new ServiceValidationException(errors);
        }

    }

    public List<Department> findAllDepartments() {
        List<Department> departments = this.repo.findAll();
        Collections.sort(departments);
        fullLogsLogger.info("Sourced all departments from the db. Total count: " + departments.size());
        return departments;
    }

    public Optional<Department> findById(Long id) {
        Optional<Department> foundDepartment = this.repo.findById(id);
        fullLogsLogger.info("Located department in db with ID: " + foundDepartment.get().getId());
        return foundDepartment;
    }

    public Optional<Department> updateById(Long id, @Valid UpdateDepartmentDTO data) throws ServiceValidationException {
        Optional<Department> maybeDepartment = this.findById(id);
        ValidationErrors errors = new ValidationErrors();

        if (maybeDepartment.isEmpty()) {
            errors.addError("Department", String.format("Department with id %s does not exist", id));
        }

        Department foundDepartment = maybeDepartment.get();

        // check to see if Department field has been provided
        String trimmedNameField = data.getDepartmentName() != null ? data.getDepartmentName().trim() : null;

        if (trimmedNameField != null) {
            if (trimmedNameField.isEmpty()) {
                errors.addError("Department", "Department field needs to have a value.");
            }
        }

        if (!data.getEmployeeIds().isEmpty()) {
            for (Long employeeId : data.getEmployeeIds()) {
                Optional<Employee> maybeEmployee = this.employeeService.findById(employeeId);

                if (maybeEmployee.isEmpty()) {
                    errors.addError("Employee", String.format("Employee with id %s does not exist", employeeId));
                } else {
                    foundDepartment.addEmployee(maybeEmployee.get());
                }
            }
        }

        // attempt all validation before throwing an error
        if (errors.hasErrors()) {
            throw new ServiceValidationException(errors);
        }

        // update with DTO fields after validation
        if (trimmedNameField != null) {
            foundDepartment.setDepartmentName(trimmedNameField);
        }

        Department updatedDepartment = this.repo.save(foundDepartment);

        fullLogsLogger.info("Update Department in db with ID: " + updatedDepartment.getId());
        return Optional.of(updatedDepartment);
    }

    public boolean deleteById(Long id) throws ServiceValidationException {
        Optional<Department> maybeDepartment = this.findById(id);
        if (maybeDepartment.isEmpty()) {
            return false;
        }

        Department foundDepartment = maybeDepartment.get();

        if (!foundDepartment.getAssociatedEmployees().isEmpty()) {
            for (Employee employee : foundDepartment.getAssociatedEmployees()) {
                foundDepartment.removeEmployee(employee);
            }
        }

        this.repo.delete(foundDepartment);
        return true;
    }

}
