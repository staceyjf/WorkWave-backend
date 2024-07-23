package com.employee.workwave.config;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.employee.workwave.Contract.CONTRACTTYPE;
import com.employee.workwave.Contract.Contract;
import com.employee.workwave.Contract.ContractRepository;
import com.employee.workwave.Contract.EMPLOYMENTTYPE;
import com.employee.workwave.Department.Department;
import com.employee.workwave.Department.DepartmentRepository;
import com.employee.workwave.Employee.Employee;
import com.employee.workwave.Employee.EmployeeRepository;
import com.employee.workwave.Employee.ROLE;
import com.employee.workwave.Employee.STATE;

@Component
public class TestDataLoader {
    private final DepartmentRepository departmentRepository;
    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    private Map<String, Employee> employees = new HashMap<>();
    private Map<String, Department> departments = new HashMap<>();
    private Map<String, Contract> contracts = new HashMap<>();
    private Map<String, String> rawPasswords = new HashMap<>();

    private static final Logger fullLogsLogger = LogManager.getLogger("fullLogs");

    @Autowired
    public TestDataLoader(
            DepartmentRepository departmentRepository,
            ContractRepository contractRepository,
            EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.departmentRepository = departmentRepository;
        this.contractRepository = contractRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Employee getAdminTestUser() {
        return employees.get("adminTestUser");
    }

    public Employee getTestUser() {
        return employees.get("testUser");
    }

    public String getRawPassword(String key) {
        return rawPasswords.get(key);
    }

    public Employee getEmployee(String key) {
        return employees.get(key);
    }

    public Department getDepartment() {
        return departments.get("Human resources");
    }

    public void loadData() {
        createContract();
    }

    public void clearData() {
        this.contractRepository.deleteAll();
        this.employeeRepository.deleteAll();
        this.departmentRepository.deleteAll();
        contracts.clear();
        employees.clear();
        rawPasswords.clear();
        departments.clear();
    }

    private void createDepartment() {
        Department dept1 = new Department();
        String dept1Name = "Human resources";
        dept1.setDepartmentName(dept1Name);
        departments.put(dept1Name, this.departmentRepository.save(dept1));

        Department dept2 = new Department();
        String dept2Name = "Finance";
        dept2.setDepartmentName(dept2Name);
        departments.put(dept2Name, this.departmentRepository.save(dept2));
    }

    private void createEmployees() {
        createDepartment();
        Employee adminTestUser = new Employee();
        adminTestUser.setUsername("ADMIN");
        adminTestUser.setRole(ROLE.ADMIN);
        adminTestUser.setPublicId(UUID.randomUUID().toString());
        // adminTestUser.setPassword("test1234");
        adminTestUser.setFirstName("Tom");
        adminTestUser.setMiddleName("Jerry");
        adminTestUser.setLastName("Jones");
        adminTestUser.setWorkEmail("tommy@gmail.com");
        adminTestUser.setMobile("0781493078");
        adminTestUser.setAddress("12 Darley Road");
        adminTestUser.setPostcode("2095");
        adminTestUser.setState(STATE.NSW);
        adminTestUser.setAssociatedDepartment(departments.get("Human resources"));

        String rawPass1 = "test1234";

        rawPasswords.put("adminTestUser", rawPass1);
        adminTestUser.setPassword(passwordEncoder.encode(rawPass1));
        Employee savedAdminEmployee = this.employeeRepository.save(adminTestUser);
        fullLogsLogger.info("Saved employee: " + savedAdminEmployee);
        employees.put("adminTestUser", savedAdminEmployee);

        Employee testUser = new Employee();
        testUser.setUsername("USER");
        testUser.setRole(ROLE.USER);
        testUser.setPublicId(UUID.randomUUID().toString());
        // testUser.setPassword("test1234");
        testUser.setFirstName("Sally");
        testUser.setLastName("Smith");
        testUser.setWorkEmail("sally@gmail.com");
        testUser.setMobile("0781493078");
        testUser.setAddress("5 Osborne Road");
        testUser.setPostcode("2095");
        testUser.setState(STATE.NSW);
        testUser.setAssociatedDepartment(departments.get("Finance"));

        String rawPass2 = "password1234";

        rawPasswords.put("testUser", rawPass2);
        testUser.setPassword(passwordEncoder.encode(rawPass2));
        Employee savedEmployee = this.employeeRepository.save(testUser);
        fullLogsLogger.info("Saved testUser employee: " + savedEmployee);
        employees.put("testUser", savedEmployee);

        Employee testUser2 = new Employee();
        testUser2.setUsername("TesterUser");
        testUser2.setRole(ROLE.USER);
        testUser2.setPublicId(UUID.randomUUID().toString());
        // testUser2.setPassword("test1234");
        testUser2.setFirstName("Test2");
        testUser2.setLastName("test2");
        testUser2.setWorkEmail("test2@gmail.com");
        testUser2.setMobile("0479148032");
        testUser2.setAddress("25 Osborne Road");
        testUser2.setPostcode("2095");
        testUser2.setState(STATE.NSW);
        testUser2.setAssociatedDepartment(departments.get("Finance"));

        String rawPass3 = "password1234";

        rawPasswords.put("testUser2", rawPass3);
        testUser2.setPassword(passwordEncoder.encode(rawPass3));
        Employee savedEmployee2 = this.employeeRepository.save(testUser2);
        fullLogsLogger.info("Saved testUser2 employee: " + savedEmployee2);
        employees.put("testUser2", savedEmployee2);

    }

    private void createContract() {
        createEmployees();
        Contract testAdminContract = new Contract();
        testAdminContract.setPosition("manager");
        testAdminContract.setContractType(CONTRACTTYPE.PERMANENT);
        testAdminContract.setEmploymentType(EMPLOYMENTTYPE.FULL_TIME);
        LocalDate startDate = LocalDate.of(2024, 8, 1);
        testAdminContract.setStartDate(startDate);
        testAdminContract.setAssociatedEmployee(employees.get("adminTestUser"));
        contracts.put("testAdminContract", this.contractRepository.save(testAdminContract));

        Contract testContract = new Contract();
        testContract.setPosition("assistant");
        testContract.setContractType(CONTRACTTYPE.CONTRACT);
        testContract.setEmploymentType(EMPLOYMENTTYPE.FULL_TIME);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        testContract.setStartDate(startDate);
        testContract.setEndDate(endDate);
        testContract.setAssociatedEmployee(employees.get("testUser"));
        contracts.put("testContract", this.contractRepository.save(testContract));

    }

}
