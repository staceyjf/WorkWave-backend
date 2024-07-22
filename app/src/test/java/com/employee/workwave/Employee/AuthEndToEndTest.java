package com.employee.workwave.Employee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.employee.workwave.config.EndToEndTest;
import com.employee.workwave.config.TestDataLoader;
import com.employee.workwave.config.Auth.TokenProvider;

import io.restassured.http.ContentType;

public class AuthEndToEndTest extends EndToEndTest {

    @Autowired
    public AuthEndToEndTest(TestDataLoader dataLoader, TokenProvider tokenProvider) {
        super(dataLoader, tokenProvider);

    }

    // ---------------------- Auth - login ------------------------------
    @Test
    public void existingUserCanLoginWithCorrectPassword() {
        Employee plainUser = getDataLoader().getEmployee("testUser");
        String password = getDataLoader().getRawPassword("testUser");
        SignInDTO body = new SignInDTO();
        body.setUsername(plainUser.getUsername());
        body.setPassword(password);

        givenNoJwtCookie().contentType(ContentType.JSON).body(body).post("/api/v1/user-management/employees/signin")
                .then().statusCode(200);
    }

    @Test
    public void existingAdminUserCanLoginWithCorrectPassword() {
        Employee adminUser = getDataLoader().getEmployee("adminTestUser");
        String password = getDataLoader().getRawPassword("adminTestUser");
        SignInDTO body = new SignInDTO();
        body.setUsername(adminUser.getUsername());
        body.setPassword(password);

        givenNoJwtCookie().contentType(ContentType.JSON).body(body).post("/api/v1/user-management/employees/signin")
                .then().statusCode(200);
    }

    @Test
    public void existingAdminUserCanNotLoginWithIncorrectPassword() {
        Employee adminUser = getDataLoader().getEmployee("adminTestUser");
        SignInDTO body = new SignInDTO();
        body.setUsername(adminUser.getUsername());
        body.setPassword("fakepassword");

        givenNoJwtCookie().contentType(ContentType.JSON).body(body).post("/api/v1/user-management/employees/signin")
                .then().statusCode(401);
    }

    @Test
    public void unknownUserCanNotLogin() {
        SignInDTO body = new SignInDTO();
        body.setUsername("faker");
        body.setPassword("fakepassword");

        givenNoJwtCookie().contentType(ContentType.JSON).body(body).post("/api/v1/user-management/employees/signin")
                .then().statusCode(401);
    }

}
