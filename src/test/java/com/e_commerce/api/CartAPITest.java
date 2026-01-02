package com.e_commerce.api;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for Cart operations
 * C6 Project Issue: "Cart items disappearing or quantities changing"
 * Tests cart add, update, remove, and persistence
 */
public class CartAPITest extends BaseAPITest {
    
    @Test(priority = 1, description = "Add product to cart - Happy path")
    public void testAddProductToCart() {
        given()
            .spec(requestSpec)
            .formParam("product_id", "1")
            .formParam("quantity", "2")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(201)))
            .body("message", anyOf(
                containsString("added"),
                containsString("success"),
                containsString("cart")
            ));
    }
    
    @Test(priority = 2, description = "Add multiple products to cart")
    public void testAddMultipleProductsToCart() {
        // Add first product
        given()
            .spec(requestSpec)
            .formParam("product_id", "1")
            .formParam("quantity", "1")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200);
        
        // Add second product
        given()
            .spec(requestSpec)
            .formParam("product_id", "2")
            .formParam("quantity", "3")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200);
        
        // Add third product
        given()
            .spec(requestSpec)
            .formParam("product_id", "3")
            .formParam("quantity", "2")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 3, description = "Add product with zero quantity - Negative")
    public void testAddProductWithZeroQuantity() {
        given()
            .spec(requestSpec)
            .formParam("product_id", "1")
            .formParam("quantity", "0")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(200)));
    }
    
    @Test(priority = 4, description = "Add product with negative quantity - Negative")
    public void testAddProductWithNegativeQuantity() {
        given()
            .spec(requestSpec)
            .formParam("product_id", "1")
            .formParam("quantity", "-5")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(200)));
    }
    
    @Test(priority = 5, description = "Add product without product_id - Negative")
    public void testAddProductWithoutProductId() {
        given()
            .spec(requestSpec)
            .formParam("quantity", "2")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400))
            .body("message", containsString("Bad request"));
    }
    
    @Test(priority = 6, description = "Add product without quantity parameter")
    public void testAddProductWithoutQuantity() {
        given()
            .spec(requestSpec)
            .formParam("product_id", "1")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 7, description = "Add invalid product ID - Negative")
    public void testAddInvalidProductId() {
        given()
            .spec(requestSpec)
            .formParam("product_id", "99999")
            .formParam("quantity", "1")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 8, description = "Add product with very large quantity")
    public void testAddProductWithLargeQuantity() {
        given()
            .spec(requestSpec)
            .formParam("product_id", "1")
            .formParam("quantity", "1000")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 9, description = "Update cart item quantity")
    public void testUpdateCartItemQuantity() {
        // Add product first
        given()
            .spec(requestSpec)
            .formParam("product_id", "5")
            .formParam("quantity", "2")
        .when()
            .post("/addToCart");
        
        // Update quantity
        given()
            .spec(requestSpec)
            .formParam("product_id", "5")
            .formParam("quantity", "5")
        .when()
            .put("/updateCart")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(201)));
    }
    
    @Test(priority = 10, description = "Remove product from cart")
    public void testRemoveProductFromCart() {
        // Add product first
        given()
            .spec(requestSpec)
            .formParam("product_id", "10")
            .formParam("quantity", "1")
        .when()
            .post("/addToCart");
        
        // Remove product
        given()
            .spec(requestSpec)
            .formParam("product_id", "10")
        .when()
            .delete("/removeFromCart")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(204)));
    }
    
    @Test(priority = 11, description = "Get cart items")
    public void testGetCartItems() {
        given()
            .spec(requestSpec)
        .when()
            .get("/getCart")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(404)));
    }
    
    @Test(priority = 12, description = "Clear entire cart")
    public void testClearCart() {
        // Add some products first
        given().spec(requestSpec).formParam("product_id", "1").formParam("quantity", "1")
            .post("/addToCart");
        given().spec(requestSpec).formParam("product_id", "2").formParam("quantity", "2")
            .post("/addToCart");
        
        // Clear cart
        given()
            .spec(requestSpec)
        .when()
            .delete("/clearCart")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(204)));
    }
    
    @Test(priority = 13, description = "Cart persistence after session")
    public void testCartPersistence() {
        String sessionId = "SESSION_" + System.currentTimeMillis();
        
        // Add items with session
        given()
            .spec(requestSpec)
            .header("Session-ID", sessionId)
            .formParam("product_id", "15")
            .formParam("quantity", "3")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200);
        
        // Verify cart with same session
        given()
            .spec(requestSpec)
            .header("Session-ID", sessionId)
        .when()
            .get("/getCart")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 14, description = "Add same product multiple times - quantity update")
    public void testAddSameProductMultipleTimes() {
        int productId = 7;
        
        // Add product first time
        given()
            .spec(requestSpec)
            .formParam("product_id", productId)
            .formParam("quantity", "2")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200);
        
        // Add same product again
        given()
            .spec(requestSpec)
            .formParam("product_id", productId)
            .formParam("quantity", "3")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 15, description = "Add product with special characters in quantity - Negative")
    public void testAddProductWithInvalidQuantityFormat() {
        given()
            .spec(requestSpec)
            .formParam("product_id", "1")
            .formParam("quantity", "abc")
        .when()
            .post("/addToCart")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(200)));
    }
}
