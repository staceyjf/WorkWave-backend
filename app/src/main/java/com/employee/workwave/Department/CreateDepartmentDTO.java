package com.employee.workwave.Department;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepartmentDTO {

    private String departmentName;

    private ArrayList<Long> employeeIds = new ArrayList<>();

}
