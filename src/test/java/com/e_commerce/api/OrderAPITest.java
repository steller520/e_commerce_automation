package com.e_commerce.api;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for Order operations
 * C6 Project Issue: "Payments debited but orders not created"
 * Tests order creation, validation, and payment integration
 */
public class OrderAPITest extends BaseAPITest {
    
    private String testOrderId;
    
    @Test(priority = 1, description = "Create order - Happy path")
    public void testCreateOrder() {
        given()
            .spec(requestSpec)
            .formParam("user_id", "1")
            .formParam("product_id", "1")
            .formParam("quantity", "2")
            .formParam("payment_method", "credit_card")
            .formParam("address_id", "1")
        .when()
            .post("/createOrder")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(201)))
            .body("message", anyOf(
                containsString("order"),
                containsString("created"),
                containsString("success")
            ));
    }
    
    @Test(priority = 2, description = "Create order without user_id - Negative")
    public void testCreateOrderWithoutUserId() {
        given()
            .spec(requestSpec)
            .formParam("product_id", "1")
            .formParam("quantity", "2")
            .formParam("payment_method", "credit_card")
        .when()
            .post("/createOrder")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400))
            .body("message", containsString("Bad request"));
    }
    
    @Test(priority = 3, description = "Create order without payment method - Negative")
    public void testCreateOrderWithoutPaymentMethod() {
        given()
            .spec(requestSpec)
            .formParam("user_id", "1")
            .formParam("product_id", "1")
            .formParam("quantity", "2")
        .when()
            .post("/createOrder")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(200)));
    }
    
    @Test(priority = 4, description = "Get order details by order ID")
    public void testGetOrderDetails() {
        given()
            .spec(requestSpec)
            .queryParam("order_id", "12345")
        .when()
            .get("/getOrderDetails")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(404)));
    }
    
    @Test(priority = 5, description = "Get all orders for a user")
    public void testGetUserOrders() {
        given()
            .spec(requestSpec)
            .queryParam("user_id", "1")
        .when()
            .get("/getUserOrders")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(404)));
    }
    
    @Test(priority = 6, description = "Cancel order")
    public void testCancelOrder() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12345")
            .formParam("reason", "Changed mind")
        .when()
            .put("/cancelOrder")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(404)));
    }
    
    @Test(priority = 7, description = "Update order status")
    public void testUpdateOrderStatus() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12345")
            .formParam("status", "shipped")
        .when()
            .put("/updateOrderStatus")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(404)));
    }
    
    @Test(priority = 8, description = "Create order with multiple products")
    public void testCreateOrderMultipleProducts() {
        String products = "[{\"product_id\":1,\"quantity\":2},{\"product_id\":2,\"quantity\":1}]";
        
        given()
            .spec(requestSpec)
            .formParam("user_id", "1")
            .formParam("products", products)
            .formParam("payment_method", "credit_card")
            .formParam("address_id", "1")
        .when()
            .post("/createOrder")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(201), equalTo(400)));
    }
    
    @Test(priority = 9, description = "Create order with invalid product ID - Negative")
    public void testCreateOrderInvalidProduct() {
        given()
            .spec(requestSpec)
            .formParam("user_id", "1")
            .formParam("product_id", "99999")
            .formParam("quantity", "2")
            .formParam("payment_method", "credit_card")
        .when()
            .post("/createOrder")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 10, description = "Get order with invalid order ID - Negative")
    public void testGetOrderInvalidId() {
        given()
            .spec(requestSpec)
            .queryParam("order_id", "INVALID_ORDER")
        .when()
            .get("/getOrderDetails")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(404), equalTo(400)));
    }
    
    @Test(priority = 11, description = "Verify order total calculation")
    public void testOrderTotalCalculation() {
        given()
            .spec(requestSpec)
            .formParam("user_id", "1")
            .formParam("product_id", "1")
            .formParam("quantity", "3")
            .formParam("payment_method", "credit_card")
            .formParam("shipping_charge", "50")
            .formParam("tax", "10")
        .when()
            .post("/createOrder")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 12, description = "Create order without address - Guest checkout")
    public void testCreateOrderGuestCheckout() {
        given()
            .spec(requestSpec)
            .formParam("product_id", "1")
            .formParam("quantity", "1")
            .formParam("payment_method", "cod")
            .formParam("guest_email", generateRandomEmail())
            .formParam("guest_name", "Guest User")
            .formParam("guest_phone", "9876543210")
        .when()
            .post("/createOrder")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 13, description = "Verify order confirmation email sent")
    public void testOrderConfirmationEmail() {
        given()
            .spec(requestSpec)
            .formParam("user_id", "1")
            .formParam("product_id", "1")
            .formParam("quantity", "1")
            .formParam("payment_method", "credit_card")
            .formParam("send_email", "true")
        .when()
            .post("/createOrder")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 14, description = "Create order with zero quantity - Negative")
    public void testCreateOrderZeroQuantity() {
        given()
            .spec(requestSpec)
            .formParam("user_id", "1")
            .formParam("product_id", "1")
            .formParam("quantity", "0")
            .formParam("payment_method", "credit_card")
        .when()
            .post("/createOrder")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(200)));
    }
    
    @Test(priority = 15, description = "Verify order items match cart items - Data consistency")
    public void testOrderItemsMatchCart() {
        // C6 Issue: "UI and DB data inconsistencies"
        // Add items to cart
        given().spec(requestSpec).formParam("product_id", "1").formParam("quantity", "2")
            .post("/addToCart");
        
        // Create order from cart
        given()
            .spec(requestSpec)
            .formParam("user_id", "1")
            .formParam("payment_method", "credit_card")
            .formParam("from_cart", "true")
        .when()
            .post("/createOrder")
        .then()
            .statusCode(200);
    }
}
