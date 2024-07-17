package com.employee.workwave.Employee;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.employee.workwave.Department.DepartmentService;
import com.employee.workwave.exceptions.ServiceValidationException;
import com.employee.workwave.exceptions.ValidationErrors;

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
            errors.addError("User", "Username already exists");
            throw new ServiceValidationException(errors);
        }

        // email needs to be in a valid email format
        if (data.getWorkEmail().matches("^(\\w+@\\w+\\.\\w{2,3})$")) {
            errors.addError("User", "Work email is not in a valid format");
            throw new ServiceValidationException(errors);
        }

        // mobile needs to be 10 digital numerical format
        if (data.getMobile().matches("^(?:\\d{10})$")) {
            errors.addError("User", "Mobile number is not in a 10 digit format");
            throw new ServiceValidationException(errors);
        }

        if (!errors.isEmpty()) {
            throw new ServiceValidationException(errors);
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.getPassword());

        Employee newEmployee = new Employee(
                data.getUsername(), encryptedPassword, data.getRole(),
                data.getFirstName(), data.getMiddleName().isEmpty() ? null : data.getMiddleName(),
                data.getLastName(), data.getWorkEmail(), data.getMobile(), data.getAddress());

        if (!data.get) {
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
   
        try {
            Employee savedEmployee = repo.save(newEmployee);
            fullLogsLogger.info("New user saved to the db");
            return savedEmployee;
        } catch (Exception ex) {
            fullLogsLogger.error("An error occurred when trying to sign up and create a new user in the db: ",
                    ex.getLocalizedMessage());
            errors.addError("User", "An error occurred during sign up. Please try again");
            throw new ServiceValidationException(errors);
        }
    }

    public List<Employee> findAllUsers() {
        return this.repo.findAll();
    }

    public Optional<Employee> findById(Long id) {
        Optional<Employee> foundUser = this.repo.findById(id);
        fullLogsLogger.info("Located User in db with ID: " + foundUser.get().getId());
        return foundUser;
    }

}
