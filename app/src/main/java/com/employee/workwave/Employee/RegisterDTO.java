package com.employee.workwave.Employee;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
        @NotBlank
        private String username;

        @NotBlank
        private String password;

        @NotBlank
        private String role;

        @NotBlank
        private String firstName;

        private String middleName;

        @NotBlank
        private String lastName;

        @NotBlank
        private String workEmail;

        @NotBlank
        private String mobile;

        @NotBlank
        private String address;

        @NotBlank
        private String postcode;

        @NotBlank
        private String state;

        private Long departmentId;
}