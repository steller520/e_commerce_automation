package com.e_commerce.api;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

/**
 * API Tests for Product operations
 * C6 Project: Product listing, search, and details API validation
 */
public class ProductAPITest extends BaseAPITest {
    
    @Test(priority = 1, description = "Get all products list - API validation")
    public void testGetAllProductsList() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/productsList")
        .then()
            .statusCode(200)
            .extract().response();
        
        // Parse JSON from response
        int responseCode = response.jsonPath().getInt("responseCode");
        assertEquals(responseCode, 200, "Response code should be 200");
        
        int productCount = response.jsonPath().getList("products").size();
        System.out.println("Total products: " + productCount);
        assertTrue(productCount > 0, "Product list should not be empty");
    }
    
    @Test(priority = 2, description = "Get all brands list - API validation")
    public void testGetAllBrandsList() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/brandsList")
        .then()
            .statusCode(200)
            .extract().response();
        
        // Parse JSON from response
        int responseCode = response.jsonPath().getInt("responseCode");
        assertEquals(responseCode, 200, "Response code should be 200");
        
        int brandCount = response.jsonPath().getList("brands").size();
        System.out.println("Total brands: " + brandCount);
        assertTrue(brandCount > 0, "Brands list should not be empty");
    }
    
    @Test(priority = 3, description = "Search product with valid keyword")
    public void testSearchProductWithValidKeyword() {
        Response response = given()
            .spec(requestSpec)
            .formParam("search_product", "top")
        .when()
            .post("/searchProduct")
        .then()
            .statusCode(200)
            .extract().response();
        
        // Parse JSON from response
        int responseCode = response.jsonPath().getInt("responseCode");
        assertEquals(responseCode, 200, "Response code should be 200");
        
        int productCount = response.jsonPath().getList("products").size();
        System.out.println("Products found with keyword 'top': " + productCount);
        assertTrue(productCount > 0, "Should find products with keyword 'top'");
    }
    
    @Test(priority = 4, description = "Search product without search_product parameter - Negative")
    public void testSearchProductWithoutParameter() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .post("/searchProduct")
        .then()
            .statusCode(200)
            .extract().response();
        
        // Parse JSON from response
        int responseCode = response.jsonPath().getInt("responseCode");
        assertEquals(responseCode, 400, "Response code should be 400 for missing parameter");
        
        String message = response.jsonPath().getString("message");
        assertTrue(message.contains("Bad request"), "Error message should mention bad request");
    }
    
    @Test(priority = 5, description = "Validate product data structure")
    public void testValidateProductDataStructure() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/productsList")
        .then()
            .statusCode(200)
            .extract().response();
        
        // Validate first product has required fields
        String productName = response.jsonPath().getString("products[0].name");
        String productPrice = response.jsonPath().getString("products[0].price");
        String productBrand = response.jsonPath().getString("products[0].brand");
        
        assertNotNull(productName, "Product should have name");
        assertNotNull(productPrice, "Product should have price");
        assertNotNull(productBrand, "Product should have brand");
        
        System.out.println("Sample product: " + productName + " - " + productPrice);
    }
    
    @Test(priority = 6, description = "Search product with empty keyword")
    public void testSearchProductWithEmptyKeyword() {
        Response response = given()
            .spec(requestSpec)
            .formParam("search_product", "")
        .when()
            .post("/searchProduct")
        .then()
            .statusCode(200)
            .extract().response();
        
        int responseCode = response.jsonPath().getInt("responseCode");
        System.out.println("Response code for empty search: " + responseCode);
        // API may return 200 with all products or 400 for empty search
        assertTrue(responseCode == 200 || responseCode == 400, "Should handle empty search");
    }
    
    @Test(priority = 7, description = "Search product with special characters")
    public void testSearchProductWithSpecialCharacters() {
        Response response = given()
            .spec(requestSpec)
            .formParam("search_product", "@#$%")
        .when()
            .post("/searchProduct")
        .then()
            .statusCode(200)
            .extract().response();
        
        int responseCode = response.jsonPath().getInt("responseCode");
        System.out.println("Response code for special chars search: " + responseCode);
        // API should handle special characters gracefully
        assertTrue(responseCode == 200 || responseCode == 400, "Should handle special characters");
    }
    
    @Test(priority = 8, description = "Validate brands data structure")
    public void testValidateBrandsDataStructure() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/brandsList")
        .then()
            .statusCode(200)
            .extract().response();
        
        // Validate first brand has required fields
        int brandId = response.jsonPath().getInt("brands[0].id");
        String brandName = response.jsonPath().getString("brands[0].brand");
        
        assertTrue(brandId > 0, "Brand should have valid ID");
        assertNotNull(brandName, "Brand should have name");
        
        System.out.println("Sample brand: " + brandName);
    }
    
    @Test(priority = 9, description = "Get products with invalid method - Negative")
    public void testGetProductsWithInvalidMethod() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .put("/productsList")  // Using PUT instead of GET
        .then()
            .extract().response();
        
        int statusCode = response.getStatusCode();
        System.out.println("Status code for invalid method: " + statusCode);
        
        // Check if API returned error for wrong HTTP method
        if (statusCode == 200) {
            // If 200, check for error in response body
            int responseCode = response.jsonPath().getInt("responseCode");
            assertEquals(responseCode, 405, "Should return 405 for unsupported method");
        } else {
            // HTTP level error
            assertTrue(statusCode == 405 || statusCode == 404, "Should return 405 or 404 for invalid method");
        }
    }
    
    @Test(priority = 10, description = "Validate API response time")
    public void testValidateAPIResponseTime() {
        long startTime = System.currentTimeMillis();
        
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/productsList")
        .then()
            .statusCode(200)
            .extract().response();
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        System.out.println("API Response Time: " + responseTime + "ms");
        assertTrue(responseTime < 30000, "API should respond within 30 seconds");
    }
}
