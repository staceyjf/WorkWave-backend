package com.employee.workwave.Employee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.employee.workwave.Contract.Contract;
import com.employee.workwave.Department.Department;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = { "associatedDepartment", "associatedContracts" })
public class Employee implements UserDetails, Comparable<Employee> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, unique = true)
    private String publicId;

    @Column(unique = true)
    private String username;

    @Column
    @Enumerated(EnumType.STRING)
    private ROLE role;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = true)
    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String workEmail;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String postcode;

    @Column
    @Enumerated(EnumType.STRING)
    private STATE state;

    @ManyToOne()
    @JoinColumn(name = "department_id", nullable = true)
    @JsonIgnoreProperties("associatedEmployees")
    private Department associatedDepartment;

    @OneToMany(mappedBy = "associatedEmployee", cascade = CascadeType.REMOVE) // as contract only exist in relation to an
                                                                           // employee eg if an employee is deleted, the
                                                                           // contract is deleted eg no orphan contracts
    @JsonIgnoreProperties("associatedEmployee")
    private List<Contract> associatedContracts = new ArrayList<>();

    // to create a user with the random publicId
    public Employee(String username, String password, ROLE role, String firstName, String middleName, String lastName,
            String workEmail, String mobile, String address, String postcode, STATE state) {
        this.username = username;
        this.role = role;
        this.password = password;
        this.publicId = UUID.randomUUID().toString(); // 36 characters long including 4 hyphens
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.workEmail = workEmail;
        this.mobile = mobile;
        this.address = address;
        this.postcode = postcode;
        this.state = state;
    }

    // without the optional middle name
    public Employee(String username, String password, ROLE role, String firstName, String lastName,
            String workEmail, String mobile, String address, String postcode, STATE state) {
        this.username = username;
        this.role = role;
        this.password = password;
        this.publicId = UUID.randomUUID().toString(); // 36 characters long including 4 hyphens
        this.firstName = firstName;
        this.lastName = lastName;
        this.workEmail = workEmail;
        this.mobile = mobile;
        this.address = address;
        this.postcode = postcode;
        this.state = state;
    }

    // methods need to be provided for UserDetail
    // TODO: look at refining these methods with custom business logic
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == ROLE.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // helper function to manage the bi-relationship between employee to departments
    public void addDepartment(Department department) {
        this.associatedDepartment = department;
        department.getAssociatedEmployees().add(this); // add the employee to the list of existing employees
    }

    // helper function to manage the bi-relationship between Department to
    // departments
    public void removeDepartment(Department department) {
        this.associatedDepartment = null;
        department.getAssociatedEmployees().remove(this); // add the employee to the list of existing employees
    }

    // using the comparable interface to enable sorting by the postcode field
    @Override
    public int compareTo(Employee other) {
        return this.firstName.compareTo(other.firstName);
    }

}
