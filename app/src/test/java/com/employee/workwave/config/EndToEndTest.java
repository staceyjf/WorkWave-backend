package com.employee.workwave.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.employee.workwave.Department.Department;
import com.employee.workwave.Employee.Employee;
import com.employee.workwave.config.Auth.TokenProvider;
import com.employee.workwave.exceptions.ServiceValidationException;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Getter
public abstract class EndToEndTest {
    @LocalServerPort
    public int port;

    private final TestDataLoader dataLoader;
    private final TokenProvider TokenProvider;
    private String testUserToken;
    private String adminTestUserToken;
    protected Long departmentId;
    protected Long testUserId;

    @Autowired
    public EndToEndTest(TestDataLoader dataLoader, TokenProvider TokenProvider) {
        this.dataLoader = dataLoader;
        this.TokenProvider = TokenProvider;
    }

    @BeforeEach
    public void setUp() throws ServiceValidationException {
        this.testUserToken = "";
        this.adminTestUserToken = "";

        RestAssured.port = port;
        this.dataLoader.clearData();
        this.dataLoader.loadData();
        Employee testUser = this.dataLoader.getTestUser();
        this.testUserId = testUser.getId();
        this.testUserToken = this.TokenProvider.generateAccessToken(testUser);
        Employee adminTestUser = this.dataLoader.getAdminTestUser();
        this.adminTestUserToken = this.TokenProvider.generateAccessToken(adminTestUser);
        Department testDepartment = this.dataLoader.getDepartment();
        this.departmentId = testDepartment.getId();
    }

    @AfterEach
    public void tearDown() {
        this.dataLoader.clearData();
    }

    public RequestSpecification givenAdminUserToken() {
        return RestAssured.given().cookie("accessToken", this.adminTestUserToken);
    }

    public RequestSpecification givenUserToken() {
        return RestAssured.given().cookie("accessToken", this.testUserToken);
    }

    public RequestSpecification givenNoJwtCookie() {
        return RestAssured.given().cookie("accessToken", "");
    }

}
