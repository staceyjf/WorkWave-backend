package com.employee.workwave.Department;

import java.util.ArrayList;
import java.util.List;

import com.employee.workwave.Employee.Employee;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Department implements Comparable<Department> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String departmentName;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> associatedEmployees = new ArrayList<>();

    // helper function to manage the bi-relationship between employee to departments
    public void addEmployee(Employee employee) {
        associatedEmployees.add(employee);
        employee.setAssociatedDepartment(this);
    }

    // helper function to manage the bi-relationship between employee to departments
    public void removeEmployee(Employee employee) {
        associatedEmployees.remove(employee);
        employee.setAssociatedDepartment(null);
    }

    // using the comparable interface to enable sorting by the postcode field
    @Override
    public int compareTo(Department other) {
        return this.departmentName.compareTo(other.departmentName);
    }

}
