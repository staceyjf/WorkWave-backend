package com.employee.workwave.Employee;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.employee.workwave.config.Auth.TokenProvider;
import com.employee.workwave.exceptions.ServiceValidationException;
import com.employee.workwave.exceptions.ValidationErrors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication", description = "Endpoints for employee authentication")
@RestController
@RequestMapping("/api/v1/user-management/employees")
public class EmployeeController {

        @Autowired
        private AuthenticationManager authManager;

        @Autowired
        private EmployeeService employeeService;

        @Autowired
        private TokenProvider tokenProvider;

        private static final Logger fullLogsLogger = LogManager.getLogger("fullLogs");

        // create a new user via request body
        @Operation(summary = "Register a new employee", description = "Create a new account and return the user details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Employee created"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        @PostMapping("/register")
        public ResponseEntity<UserDetails> register(@Valid @RequestBody RegisterDTO data)
                        throws ServiceValidationException {
                UserDetails createdUser = employeeService.register(data);
                fullLogsLogger.info("sign up controller responded with a new user: " + createdUser);
                return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        }

        // receive user details, authenticate via AuthManager and then generate a token
        @Operation(summary = "Employee sign in", description = "Authenticate a user and return a cookie based JWT token")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful login"),
                        @ApiResponse(responseCode = "401", description = "Invalid login credentials")
        })
        @PostMapping("/signin")
        public ResponseEntity<JwtDTO> signIn(@Valid @RequestBody SignInDTO data,
                        @NonNull HttpServletResponse response)
                        throws ServiceValidationException {

                try {
                        Authentication usernamePassword = new UsernamePasswordAuthenticationToken(data.getUsername(),
                                        data.getPassword());
                        Authentication authUser = authManager.authenticate(usernamePassword);
                        String accessToken = tokenProvider.generateAccessToken((Employee) authUser.getPrincipal());

                        // create and configure the cookie
                        Cookie cookie = new Cookie("accessToken", accessToken);
                        cookie.setHttpOnly(true); // the HTTPOnly flag which prevents cross site scripting
                        cookie.setSecure(true); // Secure flag which prevents man in the middle attacks
                        cookie.setPath("/"); // Makes the cookie available on all dirs & subdirs
                        cookie.setMaxAge(60 * 60); // Expires in 1 hour
                        response.addCookie(cookie);

                        fullLogsLogger.info("JWT token provided to cookie in sign in controller");
                        return new ResponseEntity<>(HttpStatus.OK);
                } catch (BadCredentialsException err) {
                        ValidationErrors errors = new ValidationErrors();
                        fullLogsLogger.error("Invalid credentials provided ",
                                        err.getLocalizedMessage());
                        errors.addError("User", "Invalid credentials provided. Please update and try again.");
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                } catch (AuthenticationException err) {
                        ValidationErrors errors = new ValidationErrors();
                        fullLogsLogger.error("An error occurred when trying to sign in: ",
                                        err.getLocalizedMessage());
                        errors.addError("User", "An error occurred during sign in. Please try again");
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
        }

        @Operation(summary = "Get all employees", description = "Return a list of all employees and their roles")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation")
        })
        @GetMapping
        public ResponseEntity<List<Employee>> findAllUsers() {
                List<Employee> allUsers = employeeService.findAllUsers();
                fullLogsLogger.info("findAllUsers Controller responded with all Users");
                return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }

        @Operation(summary = "Get a employee by ID", description = "Return the details of an employee with the specified ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Employee> findUserById(@PathVariable Long id) {
                fullLogsLogger.info(id);
                Optional<Employee> maybeUser = employeeService.findById(id);
                Employee foundUser = maybeUser
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
                fullLogsLogger.info("findUserById responses with the found postcode:" + foundUser);
                return new ResponseEntity<>(foundUser, HttpStatus.OK);
        }

        @Operation(summary = "Update an employee by ID", description = "Update an employee by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Operation successful"),
                        @ApiResponse(responseCode = "404", description = "Employee not found"),
        })
        @PatchMapping("/{id}")
        public ResponseEntity<Employee> updateEmployeeById(@PathVariable Long id,
                        @Valid @RequestBody UpdateEmployeeDTO data)
                        throws ServiceValidationException {
                Optional<Employee> maybeEmployee = this.employeeService.updateById(id, data);
                Employee updatedEmployee = maybeEmployee.orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Employee with id: " + id + " not found"));
                fullLogsLogger.info("updateEmployeeById responses with updated Employee:" + updatedEmployee);
                return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
        }

        @Operation(summary = "Delete an employee by ID", description = "Delete an Employee by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Employee deleted"),
                        @ApiResponse(responseCode = "404", description = "Employee not found"),
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteById(@PathVariable Long id)
                        throws ServiceValidationException {
                boolean isDeleted = this.employeeService.deleteById(id);
                if (!isDeleted) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "Employee with id: " + id + " not found");
                }
                fullLogsLogger.info(String.format("Employee with id: %d has been deleted ", id));
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

}
