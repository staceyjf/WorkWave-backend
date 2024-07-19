package com.employee.workwave.Contract;

public enum EMPLOYMENTTYPE {
    PART_TIME("PART_TIME"),
    FULL_TIME("FULL_TIME");

    private final String value;

    EMPLOYMENTTYPE(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
