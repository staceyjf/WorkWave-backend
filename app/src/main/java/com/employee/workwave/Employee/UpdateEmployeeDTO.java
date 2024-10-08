package com.employee.workwave.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeDTO {
    private String username;
    private String password;
    private String role;
    private String firstName;
    private String middleName;
    private String lastName;
    private String workEmail;
    private String mobile;
    private String address;
    private String postcode;
    private String state;
    private Long departmentId;
}
