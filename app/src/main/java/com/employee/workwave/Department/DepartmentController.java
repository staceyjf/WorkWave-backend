package com.employee.workwave.Department;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.employee.workwave.exceptions.ServiceValidationException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Department", description = "Endpoints for managing departments.")
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {
        @Autowired
        private DepartmentService departmentService;

        private static final Logger fullLogsLogger = LogManager.getLogger("fullLogs");

        @Operation(summary = "Create a new Department", description = "Create a new Department and return department details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Department created"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        @PostMapping
        public ResponseEntity<Department> createDepartment(@Valid @RequestBody CreateDepartmentDTO data)
                        throws ServiceValidationException {
                Department createdDepartment = this.departmentService.createDepartment(data);
                fullLogsLogger.info("createDepartment responded with new Department: " + createdDepartment);
                return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
        }

        @Operation(summary = "Get all Departments", description = "Return a list of all departments")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Operation successful"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping
        public ResponseEntity<List<Department>> findAllDepartments() {
                List<Department> allDepartments = this.departmentService.findAllDepartments();
                fullLogsLogger.info("findAllDepartments responded with all Departments.");
                return new ResponseEntity<>(allDepartments, HttpStatus.OK);
        }

        @Operation(summary = "Get a Department by ID", description = "Return the details of a department with a given ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Operation successful"),
                        @ApiResponse(responseCode = "404", description = "Department not found"),
        })
        @GetMapping("/{id}")
        public ResponseEntity<Department> findDepartmentsById(@PathVariable Long id) {
                Optional<Department> maybeDepartment = this.departmentService.findById(id);
                Department foundDepartment = maybeDepartment
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Department not found"));
                fullLogsLogger.info("findDepartmentsById responses with the found Department:" + foundDepartment);
                return new ResponseEntity<>(foundDepartment, HttpStatus.OK);
        }

        @Operation(summary = "Update a Department by ID", description = "Update a department by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Operation successful"),
                        @ApiResponse(responseCode = "404", description = "Department not found"),
        })
        @PatchMapping("/{id}")
        public ResponseEntity<Department> updateDepartmentById(@PathVariable Long id,
                        @Valid @RequestBody UpdateDepartmentDTO data)
                        throws ServiceValidationException {
                Optional<Department> maybeDepartment = this.departmentService.updateById(id, data);
                Department updatedDepartment = maybeDepartment.orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Department with id: " + id + " not found"));
                fullLogsLogger.info("updateDepartmentById responses with updated Department:" + updatedDepartment);
                return new ResponseEntity<>(updatedDepartment, HttpStatus.OK);
        }

        @Operation(summary = "Delete a department by ID", description = "Delete a department by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Department deleted"),
                        @ApiResponse(responseCode = "404", description = "Department not found"),
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteDepartmentById(@PathVariable Long id)
                        throws ServiceValidationException {
                boolean isDeleted = this.departmentService.deleteById(id);
                if (!isDeleted) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "Department with id: " + id + " not found");
                }
                fullLogsLogger.info(String.format("Department with id: %d has been deleted ", id));
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

}
