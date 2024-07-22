package com.employee.workwave.config;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        dept1.setId(Long.valueOf(1));
        dept1.setDepartmentName(dept1Name);
        departments.put(dept1Name, this.departmentRepository.save(dept1));

        Department dept2 = new Department();
        String dept2Name = "Finance";
        dept2.setId(Long.valueOf(2));
        dept2.setDepartmentName(dept2Name);
        dept2.setDepartmentName(dept2Name);
        departments.put(dept2Name, this.departmentRepository.save(dept2));
    }

    private void createEmployees() {
        createDepartment();
        Employee adminTestUser = new Employee();
        adminTestUser.setId(Long.valueOf(1));
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
        employees.put("adminTestUser", this.employeeRepository.save(adminTestUser));

        Employee testUser = new Employee();
        testUser.setId(Long.valueOf(2));
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
        employees.put("testUser", this.employeeRepository.save(testUser));
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
