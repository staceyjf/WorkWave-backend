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

public class UpdateEndToEndTest extends EndToEndTest {

    @Autowired
    public UpdateEndToEndTest(TestDataLoader dataLoader, TokenProvider tokenProvider) {
        super(dataLoader, tokenProvider);

    }

    // ---------------------- Update by id ------------------------------
    @Test
    public void existingAdminUserCanUpdateWithCorrectDetails() {
        UpdateEmployeeDTO body = new UpdateEmployeeDTO();
        body.setUsername("testAdmin");
        body.setPassword("tester1234");
        body.setRole("user");
        body.setFirstName("Tester");
        body.setMiddleName("test");
        body.setLastName("Testing");
        body.setWorkEmail("testing@gmail.com");
        body.setMobile("0471498325");
        body.setAddress("10 Pittwater Road");
        body.setPostcode("2097");
        body.setState("NSW");
        body.setDepartmentId(1L);
        givenAdminUserToken().contentType(ContentType.JSON).body(body).patch("/api/v1/user-management/employees/1")
                .then().statusCode(200);
    }

    @Test
    public void existingUserStateCanBeLongFormat() {
        UpdateEmployeeDTO body = new UpdateEmployeeDTO();
        body.setState("new south wales");
        Response response = givenAdminUserToken().contentType(ContentType.JSON).body(body)
                .patch("/api/v1/user-management/employees/1")
                .andReturn();

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void plainUserCanUpdateWithCorrectDetails() {
        UpdateEmployeeDTO body = new UpdateEmployeeDTO();
        body.setUsername("testAdmin");
        body.setPassword("tester1234");
        body.setRole("user");
        body.setFirstName("Tester");
        body.setMiddleName("test");
        body.setLastName("Testing");
        body.setWorkEmail("testing@gmail.com");
        body.setMobile("0471498325");
        body.setAddress("10 Pittwater Road");
        body.setPostcode("2097");
        body.setState("NSW");
        body.setDepartmentId(1L);
        givenUserToken().contentType(ContentType.JSON).body(body).patch("/api/v1/user-management/employees/1")
                .then().statusCode(403);
    }

    @Test
    public void existingUserNeedsUniqueUsernameWhenUpdating() {
        UpdateEmployeeDTO body = new UpdateEmployeeDTO();
        body.setUsername("ADMIN");

        Response response = givenAdminUserToken().contentType(ContentType.JSON).body(body)
                .patch("/api/v1/user-management/employees/1")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains("Username already exists"));

    }

    @Test
    public void existingUserNeedsAUniqueEmailWhenUpdating() {
        UpdateEmployeeDTO body = new UpdateEmployeeDTO();
        body.setWorkEmail("tommy@gmail.com");
    
        Response response = givenAdminUserToken().contentType(ContentType.JSON).body(body)
                .patch("/api/v1/user-management/employees/1")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains("Work email needs to be unique"));

    }

    @Test
    public void existingUserEmailNeedsToBeInCorrectFormatWhenUpdating() {
        UpdateEmployeeDTO body = new UpdateEmployeeDTO();
        body.setWorkEmail("testinggmail.com");
  
        Response response = givenAdminUserToken().contentType(ContentType.JSON).body(body)
                .patch("/api/v1/user-management/employees/1")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains("Work email is not in a valid format"));
    }

    @Test
    public void existingUserMobileNeedsToBeInCorrectFormatWhenUpdating() {
        UpdateEmployeeDTO body = new UpdateEmployeeDTO();
        body.setMobile("047149825");
    
        Response response = givenAdminUserToken().contentType(ContentType.JSON).body(body)
                .patch("/api/v1/user-management/employees/1")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains("Mobile number needs to be in a 10 digit format"));
    }

    @Test
    public void existingUserRoleNeedsToBeAValidENUMValueWhenUpdating() {
        UpdateEmployeeDTO body = new UpdateEmployeeDTO();
        body.setRole("adminstration");

        Response response = givenAdminUserToken().contentType(ContentType.JSON).body(body)
                .patch("/api/v1/user-management/employees/1")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains(
                "A role match could not be found. Please consult the documentation for accepted values for roles"));
    }

    @Test
    public void existingUserStateNeedsToBeAValidENUMValueWhenUpdating() {
        UpdateEmployeeDTO body = new UpdateEmployeeDTO();
        body.setState("no state");
     
        Response response = givenAdminUserToken().contentType(ContentType.JSON).body(body)
                .patch("/api/v1/user-management/employees/1")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains(
                "A state match could not be found. Please consult the documentation for accepted values for Australian states"));
    }

    @Test
    public void existingUserPostNeedsToBeInAValidFormatWhenUpdating() {
        UpdateEmployeeDTO body = new UpdateEmployeeDTO();
        body.setPostcode("209");
      
        Response response = givenAdminUserToken().contentType(ContentType.JSON).body(body)
                .patch("/api/v1/user-management/employees/1")
                .andReturn();

        assertEquals(400, response.getStatusCode());
        List<String> userErrorMessages = response.jsonPath().getList("errorMessages.RequestValidationError");
        assertTrue(userErrorMessages.contains(
                "Postcode field should only contain numbers and be 4 numbers in length"));
    }

  

}
