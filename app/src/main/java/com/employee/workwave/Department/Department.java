package com.employee.workwave.Department;

import java.util.ArrayList;
import java.util.List;

import com.employee.workwave.Employee.Employee;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@ToString(exclude = "associatedEmployees")
public class Department implements Comparable<Department> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String departmentName;

    @OneToMany(mappedBy = "associatedDepartment") // TODO: review if merge or persist is needed
                                                  // eg would i save/update new employees
                                                  // nested within departments
    @JsonIgnoreProperties("associatedDepartment")
    private List<Employee> associatedEmployees = new ArrayList<>();

    // using the comparable interface to enable sorting by the postcode field
    @Override
    public int compareTo(Department other) {
        return this.departmentName.compareTo(other.departmentName);
    }

}
