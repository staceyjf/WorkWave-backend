package com.employee.workwave.Contract;

import java.time.LocalDate;
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

    private Long employeeId;
}
