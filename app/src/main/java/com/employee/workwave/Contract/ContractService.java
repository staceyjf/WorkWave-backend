package com.employee.workwave.Contract;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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

        if (data.getEndDate() != null && data.getEndDate().isBefore(data.getStartDate())) {
            errors.addError("Contract",
                    "End date needs to be post start date");
        }

        if (this.repo.hasCurrentContract(data.getEmployeeId(), data.getStartDate())) {
            errors.addError("Contract",
                    String.format(
                            "Employee with id: %d is already assigned a contract at the proposed start date. Please provide a new start date.",
                            data.getEmployeeId()));
        }

        // checks if the employee is a full time permanent which with ongoing or end
        // date that is after the proposed start date
        if (this.repo.isCurrentPermanentEmployee(data.getEmployeeId(), data.getStartDate())) {
            errors.addError("Contract",
                    String.format(
                            "Employee with id: %d is already a permanent employee and can only hold one contract at a time",
                            data.getEmployeeId()));
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
            if (maybeEmployee.isPresent())
                newContract.setAssociatedEmployee(maybeEmployee.get());
        }

        try {
            Contract savedContract = repo.save(newContract);
            fullLogsLogger.info("New contract saved to the db");
            return savedContract;
        } catch (Exception ex) {
            fullLogsLogger.error("An error occurred when trying to sign up and create a new contract in the db: ",
                    ex.getLocalizedMessage());
            errors.addError("Contract", "An error occurred when trying to create a new contract.");
            throw new ServiceValidationException(errors);
        }
    }

    public List<Contract> findAllContracts() {
        List<Contract> contracts = this.repo.findAll();
        Collections.sort(contracts);
        fullLogsLogger.info("Sourced all contracts from the db. Total count: " + contracts.size());
        return contracts;
    }

    public Optional<Contract> findById(Long id) {
        Optional<Contract> foundContract = this.repo.findById(id);
        fullLogsLogger.info("Located Contract in db with ID: " + foundContract.get().getId());
        return foundContract;
    }

    public Optional<Contract> updateById(Long id, @Valid UpdateContractDTO data) throws ServiceValidationException {
        Optional<Contract> maybeContract = this.findById(id);

        if (maybeContract.isPresent()) {
            ValidationErrors errors = new ValidationErrors();
            Contract contract = maybeContract.get();
            LocalDate endDate = data.getEndDate();
            LocalDate startDate = data.getStartDate();
            Long employeeId = data.getEmployeeId();
            String position = data.getPosition();

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

            if (endDate != null && endDate.isBefore(startDate)) {
                errors.addError("Contract",
                        "End date needs to be post start date");
            }

            if (startDate != null && this.repo.hasCurrentContract(employeeId, startDate)) {
                errors.addError("Contract",
                        String.format(
                                "Employee with id: %d is already assigned a contract at the proposed start date. Please provide a new start date.",
                                employeeId));
            }

            // checks if the employee is a full time permanent which with ongoing or end
            // date that is after the proposed start date
            if (startDate != null && this.repo.isCurrentPermanentEmployee(employeeId, startDate)) {
                errors.addError("Contract",
                        String.format(
                                "Employee with id: %d is already a permanent employee and can only hold one contract at a time",
                                employeeId));
            }

            if (!errors.isEmpty()) {
                throw new ServiceValidationException(errors);
            }

            if (position != null)
                contract.setPosition(position);

            if (contracttype != null)
                contract.setContractType(contracttype);

            if (employmentType != null)
                contract.setEmploymentType(employmentType);

            if (startDate != null)
                contract.setStartDate(startDate);

            if (endDate != null)
                contract.setEndDate(endDate);

            if (data.getEmployeeId() != null) {
                Optional<Employee> maybeEmployee = this.employeeService
                        .findById(data.getEmployeeId());
                if (maybeEmployee.isPresent())
                    contract.setAssociatedEmployee(maybeEmployee.get());
                ;
            }

            // Save the updated Contract
            repo.save(contract);

            return Optional.of(contract);
        } else {
            return Optional.empty();
        }
    }

    public boolean deleteById(Long id) throws ServiceValidationException {
        Optional<Contract> maybeContract = this.findById(id);
        if (maybeContract.isEmpty()) {
            return false;
        }

        Contract foundContract = maybeContract.get();

        this.repo.delete(foundContract);
        return true;
    }

}
