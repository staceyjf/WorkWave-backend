package com.employee.workwave.Contract;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateContractDTO {
    @NotBlank
    private String position;

    @NotBlank
    private String contractType;

    @NotBlank
    private String employmentType;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private Long employeeId;
}
