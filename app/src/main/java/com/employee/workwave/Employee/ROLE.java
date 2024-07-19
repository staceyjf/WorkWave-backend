package com.employee.workwave.Employee;

public enum ROLE {
    ADMIN("ADMIN"),
    USER("USER");

    private final String role;

    ROLE(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
