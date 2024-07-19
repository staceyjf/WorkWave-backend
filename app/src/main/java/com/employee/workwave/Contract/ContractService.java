package com.employee.workwave.Contract;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee.workwave.exceptions.ServiceValidationException;
import com.employee.workwave.exceptions.ValidationErrors;
import com.employee.workwave.utils.StringUtils;

import com.employee.workwave.Employee.Employee;
import com.employee.workwave.Employee.EmployeeService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Transactional
public class ContractService {

    private static final Logger fullLogsLogger = LogManager.getLogger("fullLogs");

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ContractRepository repo;

    public Contract createContract(@Valid CreateContractDTO data) throws ServiceValidationException {
        ValidationErrors errors = new ValidationErrors();

        CONTRACTTYPE contracttype = null;
        try {
            contracttype = CONTRACTTYPE.valueOf(data.getContractType().toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.addError("Contract",
                    "A contract type match could not be found. Please consult the documentation for accepted values for contract type.");
        }

        EMPLOYMENTTYPE employmentType = null;
        try {
            employmentType = EMPLOYMENTTYPE.valueOf(data.getEmploymentType().toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.addError("Contract",
                    "A employment type match could not be found. Please consult the documentation for accepted values for employment type.");
        }

        if (data.getEndDate() != null && data.getStartDate().compareTo(data.getEndDate()) > 0) {
            errors.addError("Contract",
                    "End date needs to be post start date");
        }

        if (!errors.isEmpty()) {
            throw new ServiceValidationException(errors);
        }

        Contract newContract = new Contract(
                StringUtils.capitalizeStringFields(data.getPosition()), contracttype, employmentType,
                data.getStartDate());

        if (data.getEndDate() != null) {
            newContract.setEndDate(data.getEndDate());
        }

        if (data.getEmployeeId() != null) {
            Optional<Employee> maybeEmployee = this.employeeService.findById(data.getEmployeeId());
            newContract.setAssociatedEmployee(maybeEmployee.get());
        }

        try {
            Contract savedContract = repo.save(newContract);
            fullLogsLogger.info("New contract saved to the db");
            return savedContract;
        } catch (Exception ex) {
            fullLogsLogger.error("An error occurred when trying to sign up and create a new contract in the db: ",
                    ex.getLocalizedMessage());
            errors.addError("Contract", "An error occured when trying to create a new contract.");
            throw new ServiceValidationException(errors);
        }
    }

}
