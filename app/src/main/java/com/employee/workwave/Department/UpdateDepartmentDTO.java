package com.employee.workwave.Department;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartmentDTO {

    String departmentName;

}
