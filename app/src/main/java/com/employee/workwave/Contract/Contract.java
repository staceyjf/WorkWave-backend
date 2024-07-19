package com.employee.workwave.Contract;

import java.time.LocalDate;

import com.employee.workwave.Employee.Employee;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "associatedEmployee")
public class Contract implements Comparable<Contract> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CONTRACTTYPE contractType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EMPLOYMENTTYPE employmentType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = true)
    private LocalDate endDate;

    @ManyToOne()
    @JoinColumn(name = "employee_id", nullable = true)
    @JsonIgnoreProperties("associatedContracts")
    private Employee associatedEmployee;

    public Contract(String position, CONTRACTTYPE contractType, EMPLOYMENTTYPE employmentType, LocalDate startDate) {
        this.position = position;
        this.contractType = contractType;
        this.employmentType = employmentType;
        this.startDate = startDate;
    }

    @Override
    public int compareTo(Contract other) {
        return this.startDate.compareTo(other.startDate);
    }
}
