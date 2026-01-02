package com.e_commerce.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

/**
 * Base class for API tests
 * C6 Project: API Testing with REST Assured
 */
public class BaseAPITest {
    
    protected static final String BASE_URL = "https://automationexercise.com";
    protected static final String API_BASE_PATH = "/api";
    
    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;
    
    @BeforeClass
    public void setupAPI() {
        // Configure REST Assured
        RestAssured.baseURI = BASE_URL;
        RestAssured.basePath = API_BASE_PATH;
        
        // Request Specification - API accepts form data
        requestSpec = new RequestSpecBuilder()
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            .build();
        
        // Response Specification - API returns text/html with JSON content
        responseSpec = new ResponseSpecBuilder()
            .build();
        
        // Enable logging for debugging
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    /**
     * Get base request specification
     */
    protected RequestSpecification getRequestSpec() {
        return RestAssured.given().spec(requestSpec);
    }
    
    /**
     * Get base response specification
     */
    protected ResponseSpecification getResponseSpec() {
        return responseSpec;
    }
    
    /**
     * Generate random user data
     */
    protected String generateRandomEmail() {
        long timestamp = System.currentTimeMillis();
        return "testuser" + timestamp + "@example.com";
    }
    
    /**
     * Generate random product ID
     */
    protected int generateRandomProductId() {
        return (int) (Math.random() * 50) + 1;
    }
    
    /**
     * Generate random cart ID
     */
    protected String generateRandomCartId() {
        return "CART" + System.currentTimeMillis();
    }
    
    /**
     * Generate random coupon code
     */
    protected String generateRandomCoupon() {
        return "COUPON" + (int) (Math.random() * 1000);
    }
}
