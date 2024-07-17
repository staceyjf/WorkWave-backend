package com.employee.workwave.User;

// using a record to enforce immutability (eg no setters)
// comes with built in methods
public record RegisterDTO(String username,
        String password,
        USERROLE role) {

}
