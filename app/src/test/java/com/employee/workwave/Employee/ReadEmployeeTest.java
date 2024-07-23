package com.employee.workwave.Employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.employee.workwave.config.EndToEndTest;
import com.employee.workwave.config.TestDataLoader;
import com.employee.workwave.config.Auth.TokenProvider;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ReadEmployeeTest extends EndToEndTest {

    @Autowired
    public ReadEmployeeTest(TestDataLoader dataLoader, TokenProvider tokenProvider) {
        super(dataLoader, tokenProvider);

    }

    // ---------------------- Read all ------------------------------
    @Test
    public void adminUserCanRequestAllEmployees() {
        Response response = givenAdminUserToken().contentType(ContentType.JSON).get("/api/v1/user-management/employees")
                .andReturn();

        assertEquals(200, response.getStatusCode());
        List<Map<String, Object>> allEmployees = response.jsonPath().getList("$");
        assertNotNull(allEmployees, "The list of employees should not be null");
        assertFalse(allEmployees.isEmpty(), "The list of employees should not be empty");

    }

    // ---------------------- Read by id ------------------------------
    @Test
    public void adminUserCanRequestEmployeesById() {        
        Response response = givenAdminUserToken().contentType(ContentType.JSON)
                .get("/api/v1/user-management/employees/" + getTestUserId())
                .andReturn();

        assertEquals(200, response.getStatusCode());
        Map<String, Object> employee = response.jsonPath().getMap("$");
        assertNotNull(employee, "The employee data should not be null");
        assertFalse(employee.isEmpty(), "The employee data should not be empty");
    }

    @Test
    public void plainUserCanNotRequestEmployeesById() {
        Response plainResponse = givenUserToken().contentType(ContentType.JSON)
                .get("/api/v1/user-management/employees/" + getTestUserId())
                .andReturn();

        assertEquals(403, plainResponse.getStatusCode());
    }

}
