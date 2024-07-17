package com.employee.workwave.Employee;

public enum STATE {
    NSW("NEW SOUTH WALES"),
    QLD("QUEENSLAND"),
    SA("SOUTH AUSTRALIA"),
    TAS("TASMANIA"),
    VIC("VICTORIA"),
    WA("WESTERN AUSTRALIA"),
    ACT("AUSTRALIAN CAPITAL TERRITORY"),
    NT("NORTHERN TERRITORY");

    private final String fullName;

    STATE(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public static STATE from(String state) {
        // loop through to check if request state matches either the abbreviation or
        // full state name
        for (STATE STATE : STATE.values()) {
            String upperCaseState = state.toUpperCase(); // to ensure case insensitivity
            if (STATE.name().equals(upperCaseState) || STATE.getFullName().equals(upperCaseState)) {
                return STATE;
            }
        }
        return null;
    }
}
