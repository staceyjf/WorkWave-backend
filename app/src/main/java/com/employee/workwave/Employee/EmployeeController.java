package com.employee.workwave.Employee;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.employee.workwave.config.Auth.TokenProvider;
import com.employee.workwave.exceptions.ServiceValidationException;
import com.employee.workwave.exceptions.ValidationErrors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Authentication", description = "Endpoints for employee authentication")
@RestController
@RequestMapping("/api/v1/admin")
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
    public ResponseEntity<JwtDTO> signIn(@Valid @RequestBody SignInDTO data)
            throws ServiceValidationException {

        try {
            Authentication usernamePassword = new UsernamePasswordAuthenticationToken(data.getUsername(),
                    data.getPassword());
            Authentication authUser = authManager.authenticate(usernamePassword);
            String accessToken = tokenProvider.generateAccessToken((Employee) authUser.getPrincipal());
            JwtDTO JwtToken = new JwtDTO(accessToken); // wrap the token in a JWTDTO
            fullLogsLogger.info("JWT token provided in sign in controller");
            return new ResponseEntity<>(JwtToken, HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            ValidationErrors errors = new ValidationErrors();
            fullLogsLogger.error("Invalid credentials provided ",
                    ex.getLocalizedMessage());
            errors.addError("User", "Invalid credentials provided. Please update and try again.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException ex) {
            ValidationErrors errors = new ValidationErrors();
            fullLogsLogger.error("An error occurred when trying to sign in: ",
                    ex.getLocalizedMessage());
            errors.addError("User", "An error occurred during sign in. Please try again");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Get all employees", description = "Return a list of all employees and their roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping("/allemployees")
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
        Optional<Employee> maybeUser = employeeService.findById(id);
        Employee foundUser = maybeUser
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        fullLogsLogger.info("findUserById responses with the found postcode:" + foundUser);
        return new ResponseEntity<>(foundUser, HttpStatus.OK);
    }

}
