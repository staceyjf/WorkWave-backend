package com.employee.workwave.Employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.employee.workwave.config.EndToEndTest;
import com.employee.workwave.config.TestDataLoader;
import com.employee.workwave.config.Auth.TokenProvider;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CreateEmployeeTest extends EndToEndTest {

    @Autowired
    public CreateEmployeeTest(TestDataLoader dataLoader, TokenProvider tokenProvider) {
        super(dataLoader, tokenProvider);

    }

    // ---------------------- Create ------------------------------
    @Test
    public void newUserCanRegisterWithCorrectDetails() {
        RegisterDTO body = new RegisterDTO();
        body.setUsername("testAdminNewUser");
        body.setPassword("tester1234");
        body.setRole("admin");
        body.setFirstName("Tester");
        body.setMiddleName("test");
        body.setLastName("Testing");
        body.setWorkEmail("admin@gmail.com");
        body.setMobile("0471498325");
        body.setAddress("10 Pittwater Road");
        body.setPostcode("2097");
        body.setState("NSW");
        // body.setDepartmentId(1L);
        givenNoJwtCookie().contentType(ContentType.JSON).body(body).post("/api/v1/user-management/employees/register")
                .then().statusCode(201);
    }

    @Test
    public void newUserStateCanBeLongFormat() {
        RegisterDTO body = new RegisterDTO();
        body.setUsername("testAdminNewUserWithENUM");
        body.setPassword("tester1234");
        body.setRole("admin");
        body.setFirstName("Tester");
        body.setMiddleName("test");
        body.setLastName("Testing");
        body.setWorkEmail("enumtesting@gmail.com");
        body.setMobile("0471498125");
        body.setAddress("10 Pittwater Road");
        body.setPostcode("2097");
        body.setState("new south wales");
        // body.setDepartmentId(1L);

        Response response = givenNoJwtCookie().contentType(ContentType.JSON).body(body)
                .post("/api/v1/user-management/employees/register")
                .andReturn();

        assertEquals(201, response.getStatusCode());
    }

    @Test
    public void newUserNeedsUniqueUsername() {
        RegisterDTO body = new RegisterDTO();
        body.setUsername("ADMIN");
        body.setPassword("tester1234");
        body.setRole("admin");
        body.setFirstName("Tester");
        body.setMiddleName("test");
        body.setLastName("Testing");
        body.setWorkEmail("testing@gmail.com");
        body.setMobile("0471498325");
        body.setAddress("10 Pittwater Road");
        body.setPostcode("2097");
        body.setState("NSW");
        body.setDepartmentId(1L);

        Response response = givenNoJwtCookie().contentType(ContentType.JSON).body(body)
                .post("/api/v1/user-management/employees/register")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains("Username already exists"));

    }

    @Test
    public void newUserNeedsAUniqueEmail() {
        RegisterDTO body = new RegisterDTO();
        body.setUsername("testerUser");
        body.setPassword("tester1234");
        body.setRole("admin");
        body.setFirstName("Tester");
        body.setMiddleName("test");
        body.setLastName("Testing");
        body.setWorkEmail("tommy@gmail.com");
        body.setMobile("0471498325");
        body.setAddress("10 Pittwater Road");
        body.setPostcode("2097");
        body.setState("NSW");
        body.setDepartmentId(1L);

        Response response = givenNoJwtCookie().contentType(ContentType.JSON).body(body)
                .post("/api/v1/user-management/employees/register")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains("Work email needs to be unique"));

    }

    @Test
    public void newUserEmailNeedsToBeInCorrectFormat() {
        RegisterDTO body = new RegisterDTO();
        body.setUsername("testAdmin");
        body.setPassword("tester1234");
        body.setRole("admin");
        body.setFirstName("Tester");
        body.setMiddleName("test");
        body.setLastName("Testing");
        body.setWorkEmail("testinggmail.com");
        body.setMobile("0471498325");
        body.setAddress("10 Pittwater Road");
        body.setPostcode("2097");
        body.setState("NSW");
        body.setDepartmentId(1L);

        Response response = givenNoJwtCookie().contentType(ContentType.JSON).body(body)
                .post("/api/v1/user-management/employees/register")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains("Work email is not in a valid format"));
    }

    @Test
    public void newUserMobileNeedsToBeInCorrectFormat() {
        RegisterDTO body = new RegisterDTO();
        body.setUsername("testAdmin");
        body.setPassword("tester1234");
        body.setRole("admin");
        body.setFirstName("Tester");
        body.setMiddleName("test");
        body.setLastName("Testing");
        body.setWorkEmail("testing@gmail.com");
        body.setMobile("047149825");
        body.setAddress("10 Pittwater Road");
        body.setPostcode("2097");
        body.setState("NSW");
        body.setDepartmentId(1L);

        Response response = givenNoJwtCookie().contentType(ContentType.JSON).body(body)
                .post("/api/v1/user-management/employees/register")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains("Mobile number needs to be in a 10 digit format"));
    }

    @Test
    public void newUserRoleNeedsToBeAValidENUMValue() {
        RegisterDTO body = new RegisterDTO();
        body.setUsername("testAdmin");
        body.setPassword("tester1234");
        body.setRole("adminstration");
        body.setFirstName("Tester");
        body.setMiddleName("test");
        body.setLastName("Testing");
        body.setWorkEmail("testing@gmail.com");
        body.setMobile("0471498125");
        body.setAddress("10 Pittwater Road");
        body.setPostcode("2097");
        body.setState("NSW");
        body.setDepartmentId(1L);

        Response response = givenNoJwtCookie().contentType(ContentType.JSON).body(body)
                .post("/api/v1/user-management/employees/register")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains(
                "A role match could not be found. Please consult the documentation for accepted values for roles"));
    }

    @Test
    public void newUserStateNeedsToBeAValidENUMValue() {
        RegisterDTO body = new RegisterDTO();
        body.setUsername("testAdmin");
        body.setPassword("tester1234");
        body.setRole("admin");
        body.setFirstName("Tester");
        body.setMiddleName("test");
        body.setLastName("Testing");
        body.setWorkEmail("testing@gmail.com");
        body.setMobile("0471498125");
        body.setAddress("10 Pittwater Road");
        body.setPostcode("2097");
        body.setState("no state");
        body.setDepartmentId(1L);

        Response response = givenNoJwtCookie().contentType(ContentType.JSON).body(body)
                .post("/api/v1/user-management/employees/register")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains(
                "A state match could not be found. Please consult the documentation for accepted values for Australian states"));
    }

    @Test
    public void newUserPostNeedsToBeInAValidFormat() {
        RegisterDTO body = new RegisterDTO();
        body.setUsername("testAdmin");
        body.setPassword("tester1234");
        body.setRole("admin");
        body.setFirstName("Tester");
        body.setMiddleName("test");
        body.setLastName("Testing");
        body.setWorkEmail("testing@gmail.com");
        body.setMobile("0471498125");
        body.setAddress("10 Pittwater Road");
        body.setPostcode("209");
        body.setState("nsw");
        body.setDepartmentId(1L);

        Response response = givenNoJwtCookie().contentType(ContentType.JSON).body(body)
                .post("/api/v1/user-management/employees/register")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains(
                "Postcode field should only contain numbers and be 4 numbers in length"));
    }

}
