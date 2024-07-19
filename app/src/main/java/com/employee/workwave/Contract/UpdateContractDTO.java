package com.employee.workwave.Contract;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateContractDTO {
    private String position;

    private String contractType;

    private String employmentType;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<Long> associatedEmployeeIds;
}
