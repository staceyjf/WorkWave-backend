package com.employee.workwave.Employee;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.employee.workwave.config.EndToEndTest;
import com.employee.workwave.config.TestDataLoader;
import com.employee.workwave.config.Auth.TokenProvider;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class DeleteEmployeeTest extends EndToEndTest {

    @Autowired
    public DeleteEmployeeTest(TestDataLoader dataLoader, TokenProvider tokenProvider) {
        super(dataLoader, tokenProvider);

    }

    // ---------------------- Delete by id ------------------------------
    @Test
    public void adminUserCanDeleteEmployeesById() {
        Response response = givenAdminUserToken().contentType(ContentType.JSON)
                .delete("/api/v1/user-management/employees/1")
                .andReturn();

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void plainUserCanNotDeleteEmployeesById() {
        Response response = givenUserToken().contentType(ContentType.JSON)
                .delete("/api/v1/user-management/employees/2")
                .andReturn();

        assertEquals(403, response.getStatusCode());
    }

}
