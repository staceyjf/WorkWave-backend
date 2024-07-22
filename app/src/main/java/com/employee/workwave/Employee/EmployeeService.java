package com.employee.workwave.Employee;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.employee.workwave.Department.Department;
import com.employee.workwave.Department.DepartmentService;
import com.employee.workwave.exceptions.ServiceValidationException;
import com.employee.workwave.exceptions.ValidationErrors;
import com.employee.workwave.utils.StringUtils;

import jakarta.validation.Valid;

@Service
public class EmployeeService implements UserDetailsService {
    @Autowired
    private EmployeeRepository repo;

    @Autowired
    private DepartmentService departmentService;

    private static final Logger fullLogsLogger = LogManager.getLogger("fullLogs");

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails employee = repo.findByUsername(username); // custom lookup
        return employee;
    }

    public UserDetails register(@Valid RegisterDTO data) throws ServiceValidationException {
        ValidationErrors errors = new ValidationErrors();

        // check to see if the username already exists
        if (data.getUsername() != null && repo.findByUsername(data.getUsername()) != null) {
            errors.addError("RequestValidationError", "Username already exists");
        }

        // check to see if the email has already been used
        if (data.getWorkEmail() != null && repo.findByWorkEmail(data.getWorkEmail()) != null) {
            errors.addError("RequestValidationError", "Work email needs to be unique");
        }

        // email needs to be in a valid email format
        if (!data.getWorkEmail().matches("^(\\w+@\\w+\\.\\w{2,3})$")) {
            errors.addError("RequestValidationError", "Work email is not in a valid format");
        }

        // mobile needs to be 10 digital numerical format
        if (!data.getMobile().matches("^(?:\\d{10})$")) {
            errors.addError("RequestValidationError", "Mobile number needs to be in a 10 digit format");
        }

        ROLE role = null;
        try {
            role = ROLE.valueOf(data.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.addError("RequestValidationError",
                    "A role match could not be found. Please consult the documentation for accepted values for roles");
        }

        STATE state = STATE.from(data.getState());

        if (state == null)
            errors.addError("RequestValidationError",
                    "A state match could not be found. Please consult the documentation for accepted values for Australian states");

        if (!data.getPostcode().matches("[\\d]{4}")) {
            errors.addError("RequestValidationError",
                    "Postcode field should only contain numbers and be 4 numbers in length");
        }

        if (!errors.isEmpty()) {
            throw new ServiceValidationException(errors);
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.getPassword());

        Employee newEmployee = new Employee(
                data.getUsername(), encryptedPassword,
                role,
                StringUtils.capitalizeStringFields(
                        data.getFirstName()),
                StringUtils.capitalizeStringFields(data.getLastName()), data.getWorkEmail(), data.getMobile(),
                data.getAddress(), data.getPostcode(),
                state);

        if (data.getMiddleName() != null) {
            newEmployee.setMiddleName(StringUtils.capitalizeStringFields(data.getMiddleName()));
        }

        if (data.getDepartmentId() != null) {
            Optional<Department> maybeDepartment = this.departmentService.findById(data.getDepartmentId());
            newEmployee.addDepartment(maybeDepartment.get());
        }

        fullLogsLogger.info(newEmployee);

        try {
            Employee savedEmployee = repo.save(newEmployee);
            fullLogsLogger.info("New employee saved to the db");
            return savedEmployee;
        } catch (Exception ex) {
            fullLogsLogger.error("An error occurred when trying to sign up and create a new user in the db: ",
                    ex.getLocalizedMessage());
            errors.addError("UserRegistrationException", "An error occurred during sign up. Please try again");
            throw new ServiceValidationException(errors);
        }
    }

    public List<Employee> findAllUsers() {
        List<Employee> employees = this.repo.findAll();
        Collections.sort(employees);
        fullLogsLogger.info("Sourced all employees from the db. Total count: " + employees.size());
        return employees;
    }

    public Optional<Employee> findById(Long id) {
        Optional<Employee> foundUser = this.repo.findById(id);
        fullLogsLogger.info("Located User in db with ID: " + foundUser.get().getId());
        return foundUser;
    }

    public Optional<Employee> updateById(Long id, @Valid UpdateEmployeeDTO data) throws ServiceValidationException {
        Optional<Employee> maybeEmployee = this.findById(id);

        if (maybeEmployee.isPresent()) {
            Employee employee = maybeEmployee.get();
            ValidationErrors errors = new ValidationErrors();

            // check to see if the username already exists
            if (data.getUsername() != null && repo.findByUsername(data.getUsername()) != null) {
                errors.addError("RequestValidationError", "Username already exists");
            }

            // email needs to be in a valid email format
            if (data.getWorkEmail() != null && !data.getWorkEmail().matches("^(\\w+@\\w+\\.\\w{2,3})$")) {
                errors.addError("RequestValidationError", "Work email is not in a valid format");
            }

            // check to see if the email has already been used
            if (data.getWorkEmail() != null && repo.findByWorkEmail(data.getWorkEmail()) != null) {
                errors.addError("RequestValidationError", "Work email needs to be unique");
            }

            // mobile needs to be 10 digital numerical format
            if (data.getMobile() != null && !data.getMobile().matches("^(?:\\d{10})$")) {
                errors.addError("RequestValidationError", "Mobile number needs to be in a 10 digit format");
            }

            ROLE role = null;

            if (data.getRole() != null) {
                try {
                    role = ROLE.valueOf(data.getRole().toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.addError("RequestValidationError",
                            "A role match could not be found. Please consult the documentation for accepted values for roles");
                }
            }

            if (data.getState() != null) {
                STATE state = STATE.from(data.getState());

                if (state == null)
                    errors.addError("RequestValidationError",
                            "A state match could not be found. Please consult the documentation for accepted values for Australian states");
            }

            if (data.getPostcode() != null && !data.getPostcode().matches("[\\d]{4}")) {
                errors.addError("RequestValidationError",
                        "Postcode field should only contain numbers and be 4 numbers in length");
            }

            if (!errors.isEmpty()) {
                throw new ServiceValidationException(errors);
            }

            if (data.getUsername() != null)
                employee.setUsername(data.getUsername());
            if (data.getRole() != null)
                employee.setRole(role);
            if (data.getPassword() != null) {
                String encryptedPassword = new BCryptPasswordEncoder().encode(data.getPassword());
                employee.setPassword(encryptedPassword);
            }
            if (data.getFirstName() != null)
                employee.setFirstName(StringUtils.capitalizeStringFields(data.getFirstName()));
            if (data.getMiddleName() != null)
                employee.setMiddleName(StringUtils.capitalizeStringFields(data.getMiddleName()));
            if (data.getLastName() != null)
                employee.setLastName(StringUtils.capitalizeStringFields(data.getLastName()));
            if (data.getWorkEmail() != null)
                employee.setWorkEmail(data.getWorkEmail());
            if (data.getMobile() != null)
                employee.setMobile(data.getMobile());
            if (data.getDepartmentId() != null) {
                Optional<Department> maybeDepartment = this.departmentService
                        .findById(data.getDepartmentId());
                employee.addDepartment(maybeDepartment.get());
            }

            // Save the updated employee
            repo.save(employee);

            return Optional.of(employee);
        } else {
            return Optional.empty();
        }
    }

    public boolean deleteById(Long id) throws ServiceValidationException {
        Optional<Employee> maybeEmployee = this.findById(id);
        if (maybeEmployee.isEmpty()) {
            return false;
        }

        Employee foundEmployee = maybeEmployee.get();

        if (foundEmployee.getAssociatedDepartment() != null) {
            foundEmployee.removeDepartment(foundEmployee.getAssociatedDepartment());
        }

        this.repo.delete(foundEmployee);
        return true;
    }

}
