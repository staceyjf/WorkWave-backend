package com.employee.workwave.Contract;

public enum CONTRACTTYPE {
    PERMANENT("PERMANENT"),
    CONTRACT("CONTRACT");

    private final String value;

    CONTRACTTYPE(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
