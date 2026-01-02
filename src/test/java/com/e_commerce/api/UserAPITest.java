package com.e_commerce.api;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * API Tests for User operations
 * C6 Project: User registration, login, and account management
 */
public class UserAPITest extends BaseAPITest {
    
    private String testEmail;
    private String testPassword = "Test@123";
    
    @Test(priority = 1, description = "Create user account - POST")
    public void testCreateUserAccount() {
        testEmail = generateRandomEmail();
        
        Response response = given()
            .spec(requestSpec)
            .formParam("name", "Test User")
            .formParam("email", testEmail)
            .formParam("password", testPassword)
            .formParam("title", "Mr")
            .formParam("birth_date", "15")
            .formParam("birth_month", "5")
            .formParam("birth_year", "1990")
            .formParam("firstname", "Test")
            .formParam("lastname", "User")
            .formParam("company", "Test Company")
            .formParam("address1", "123 Test Street")
            .formParam("address2", "Apt 4B")
            .formParam("country", "India")
            .formParam("zipcode", "12345")
            .formParam("state", "Test State")
            .formParam("city", "Test City")
            .formParam("mobile_number", "1234567890")
        .when()
            .post("/createAccount")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(201))
            .body("message", containsString("User created"))
            .extract().response();
        
        System.out.println("User created: " + testEmail);
    }
    
    @Test(priority = 2, description = "Create duplicate user account - Negative")
    public void testCreateDuplicateAccount() {
        String email = "duplicate@test.com";
        
        // Create first account
        given()
            .spec(requestSpec)
            .formParam("name", "Duplicate User")
            .formParam("email", email)
            .formParam("password", testPassword)
            .formParam("title", "Mr")
            .formParam("birth_date", "15")
            .formParam("birth_month", "5")
            .formParam("birth_year", "1990")
            .formParam("firstname", "Duplicate")
            .formParam("lastname", "User")
            .formParam("company", "Test Company")
            .formParam("address1", "123 Test Street")
            .formParam("country", "India")
            .formParam("zipcode", "12345")
            .formParam("state", "Test State")
            .formParam("city", "Test City")
            .formParam("mobile_number", "1234567890")
        .when()
            .post("/createAccount");
        
        // Try to create duplicate
        given()
            .spec(requestSpec)
            .formParam("name", "Duplicate User")
            .formParam("email", email)
            .formParam("password", testPassword)
            .formParam("title", "Mr")
            .formParam("birth_date", "15")
            .formParam("birth_month", "5")
            .formParam("birth_year", "1990")
            .formParam("firstname", "Duplicate")
            .formParam("lastname", "User")
            .formParam("company", "Test Company")
            .formParam("address1", "123 Test Street")
            .formParam("country", "India")
            .formParam("zipcode", "12345")
            .formParam("state", "Test State")
            .formParam("city", "Test City")
            .formParam("mobile_number", "1234567890")
        .when()
            .post("/createAccount")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400))
            .body("message", containsString("already exist"));
    }
    
    @Test(priority = 3, description = "Verify user login with valid credentials")
    public void testUserLoginValid() {
        given()
            .spec(requestSpec)
            .formParam("email", "testuser@test.com")
            .formParam("password", "Test@123")
        .when()
            .post("/verifyLogin")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(200))
            .body("message", containsString("User exists"));
    }
    
    @Test(priority = 4, description = "Verify user login without email - Negative")
    public void testUserLoginWithoutEmail() {
        given()
            .spec(requestSpec)
            .formParam("password", testPassword)
        .when()
            .post("/verifyLogin")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400))
            .body("message", containsString("Bad request"));
    }
    
    @Test(priority = 5, description = "Verify user login with invalid credentials - Negative")
    public void testUserLoginInvalid() {
        given()
            .spec(requestSpec)
            .formParam("email", "invalid@test.com")
            .formParam("password", "WrongPass123")
        .when()
            .post("/verifyLogin")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(404))
            .body("message", containsString("User not found"));
    }
    
    @Test(priority = 6, description = "Delete user account")
    public void testDeleteUserAccount() {
        String email = generateRandomEmail();
        
        // Create account first
        given()
            .spec(requestSpec)
            .formParam("name", "Delete User")
            .formParam("email", email)
            .formParam("password", testPassword)
            .formParam("title", "Mr")
            .formParam("birth_date", "15")
            .formParam("birth_month", "5")
            .formParam("birth_year", "1990")
            .formParam("firstname", "Delete")
            .formParam("lastname", "User")
            .formParam("company", "Test Company")
            .formParam("address1", "123 Test Street")
            .formParam("country", "India")
            .formParam("zipcode", "12345")
            .formParam("state", "Test State")
            .formParam("city", "Test City")
            .formParam("mobile_number", "1234567890")
        .when()
            .post("/createAccount");
        
        // Delete account
        given()
            .spec(requestSpec)
            .formParam("email", email)
            .formParam("password", testPassword)
        .when()
            .delete("/deleteAccount")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(200))
            .body("message", containsString("Account deleted"));
    }
    
    @Test(priority = 7, description = "Update user account")
    public void testUpdateUserAccount() {
        String email = generateRandomEmail();
        
        // Create account first
        given()
            .spec(requestSpec)
            .formParam("name", "Update User")
            .formParam("email", email)
            .formParam("password", testPassword)
            .formParam("title", "Mr")
            .formParam("birth_date", "15")
            .formParam("birth_month", "5")
            .formParam("birth_year", "1990")
            .formParam("firstname", "Update")
            .formParam("lastname", "User")
            .formParam("company", "Test Company")
            .formParam("address1", "123 Test Street")
            .formParam("country", "India")
            .formParam("zipcode", "12345")
            .formParam("state", "Test State")
            .formParam("city", "Test City")
            .formParam("mobile_number", "1234567890")
        .when()
            .post("/createAccount");
        
        // Update account
        given()
            .spec(requestSpec)
            .formParam("name", "Updated User")
            .formParam("email", email)
            .formParam("password", testPassword)
            .formParam("title", "Mr")
            .formParam("birth_date", "20")
            .formParam("birth_month", "6")
            .formParam("birth_year", "1991")
            .formParam("firstname", "Updated")
            .formParam("lastname", "Name")
            .formParam("company", "Updated Company")
            .formParam("address1", "456 Updated Street")
            .formParam("country", "India")
            .formParam("zipcode", "54321")
            .formParam("state", "Updated State")
            .formParam("city", "Updated City")
            .formParam("mobile_number", "9876543210")
        .when()
            .put("/updateAccount")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(200))
            .body("message", containsString("User updated"));
    }
    
    @Test(priority = 8, description = "Get user account details by email")
    public void testGetUserDetailsByEmail() {
        given()
            .spec(requestSpec)
            .queryParam("email", "testuser@test.com")
        .when()
            .get("/getUserDetailByEmail")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(200))
            .body("user", notNullValue());
    }
    
    @Test(priority = 9, description = "Create account without mandatory fields - Negative")
    public void testCreateAccountWithoutMandatoryFields() {
        given()
            .spec(requestSpec)
            .formParam("name", "Incomplete User")
        .when()
            .post("/createAccount")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400));
    }
    
    @Test(priority = 10, description = "Login with GET method - Negative")
    public void testLoginWithInvalidMethod() {
        given()
            .spec(requestSpec)
            .queryParam("email", "testuser@test.com")
            .queryParam("password", testPassword)
        .when()
            .get("/verifyLogin")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(405))
            .body("message", containsString("not supported"));
    }
}
