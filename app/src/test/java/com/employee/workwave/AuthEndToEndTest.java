package com.employee.workwave;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.employee.workwave.Employee.Employee;
import com.employee.workwave.Employee.RegisterDTO;
import com.employee.workwave.config.EndToEndTest;
import com.employee.workwave.config.TestDataLoader;
import com.employee.workwave.config.Auth.TokenProvider;

import io.restassured.http.ContentType;

public class AuthEndToEndTest extends EndToEndTest {

    
    @Autowired
    public AuthEndToEndTest(TestDataLoader dataLoader, TokenProvider tokenProvider) {
        super(dataLoader, tokenProvider);

    }

    @Test
    public void existingUserCanLoginWithCorrectPassword() {
        Employee plainUser = getDataLoader().getEmployee("testUser");
        String password = getDataLoader().getRawPassword("testUser");
        RegisterDTO body = new RegisterDTO();
        body.setUsername(plainUser.getUsername());
        body.setPassword(password);

        givenUserToken().contentType(ContentType.JSON).body(body).post("/api/v1/user-management/employees/signin").then().statusCode(200);
    }

}
