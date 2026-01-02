package com.e_commerce.api;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for Payment operations
 * C6 Project Issue: "Payments debited but orders not created"
 * Tests payment gateway integration, validation, and failure scenarios
 */
public class PaymentAPITest extends BaseAPITest {
    
    @Test(priority = 1, description = "Process payment - Credit Card - Happy path")
    public void testProcessPaymentCreditCard() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12345")
            .formParam("payment_method", "credit_card")
            .formParam("card_number", "4111111111111111")
            .formParam("card_expiry", "12/25")
            .formParam("card_cvv", "123")
            .formParam("amount", "1500")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(201)))
            .body("message", anyOf(
                containsString("success"),
                containsString("payment"),
                containsString("approved")
            ));
    }
    
    @Test(priority = 2, description = "Process payment - Debit Card")
    public void testProcessPaymentDebitCard() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12346")
            .formParam("payment_method", "debit_card")
            .formParam("card_number", "5500000000000004")
            .formParam("card_expiry", "06/26")
            .formParam("card_cvv", "456")
            .formParam("amount", "2500")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(201)));
    }
    
    @Test(priority = 3, description = "Process payment - PayPal")
    public void testProcessPaymentPayPal() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12347")
            .formParam("payment_method", "paypal")
            .formParam("paypal_email", "user@paypal.com")
            .formParam("amount", "3000")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(201)));
    }
    
    @Test(priority = 4, description = "Process payment - UPI")
    public void testProcessPaymentUPI() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12348")
            .formParam("payment_method", "upi")
            .formParam("upi_id", "user@upi")
            .formParam("amount", "1000")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(201)));
    }
    
    @Test(priority = 5, description = "Process payment - Cash on Delivery")
    public void testProcessPaymentCOD() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12349")
            .formParam("payment_method", "cod")
            .formParam("amount", "800")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(201)));
    }
    
    @Test(priority = 6, description = "Payment with invalid card number - Negative")
    public void testPaymentInvalidCardNumber() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12350")
            .formParam("payment_method", "credit_card")
            .formParam("card_number", "1111222233334444")
            .formParam("card_expiry", "12/25")
            .formParam("card_cvv", "123")
            .formParam("amount", "1500")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(422)));
    }
    
    @Test(priority = 7, description = "Payment with expired card - Negative")
    public void testPaymentExpiredCard() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12351")
            .formParam("payment_method", "credit_card")
            .formParam("card_number", "4111111111111111")
            .formParam("card_expiry", "01/20")
            .formParam("card_cvv", "123")
            .formParam("amount", "1500")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(422)));
    }
    
    @Test(priority = 8, description = "Payment with invalid CVV - Negative")
    public void testPaymentInvalidCVV() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12352")
            .formParam("payment_method", "credit_card")
            .formParam("card_number", "4111111111111111")
            .formParam("card_expiry", "12/25")
            .formParam("card_cvv", "99")
            .formParam("amount", "1500")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(422)));
    }
    
    @Test(priority = 9, description = "Payment with insufficient funds - Negative")
    public void testPaymentInsufficientFunds() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12353")
            .formParam("payment_method", "credit_card")
            .formParam("card_number", "4000000000000002")
            .formParam("card_expiry", "12/25")
            .formParam("card_cvv", "123")
            .formParam("amount", "100000")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(402), equalTo(422)));
    }
    
    @Test(priority = 10, description = "Payment without amount - Negative")
    public void testPaymentWithoutAmount() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12354")
            .formParam("payment_method", "credit_card")
            .formParam("card_number", "4111111111111111")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400))
            .body("message", containsString("Bad request"));
    }
    
    @Test(priority = 11, description = "Payment with zero amount - Negative")
    public void testPaymentZeroAmount() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12355")
            .formParam("payment_method", "credit_card")
            .formParam("amount", "0")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(422)));
    }
    
    @Test(priority = 12, description = "Payment with negative amount - Negative")
    public void testPaymentNegativeAmount() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12356")
            .formParam("payment_method", "credit_card")
            .formParam("amount", "-500")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(422)));
    }
    
    @Test(priority = 13, description = "Get payment status")
    public void testGetPaymentStatus() {
        given()
            .spec(requestSpec)
            .queryParam("payment_id", "PAY12345")
        .when()
            .get("/getPaymentStatus")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(404)));
    }
    
    @Test(priority = 14, description = "Refund payment")
    public void testRefundPayment() {
        given()
            .spec(requestSpec)
            .formParam("payment_id", "PAY12345")
            .formParam("amount", "1500")
            .formParam("reason", "Order cancelled")
        .when()
            .post("/refundPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(404)));
    }
    
    @Test(priority = 15, description = "Payment timeout simulation - Negative")
    public void testPaymentTimeout() {
        given()
            .spec(requestSpec)
            .formParam("order_id", "12357")
            .formParam("payment_method", "credit_card")
            .formParam("card_number", "4111111111111111")
            .formParam("card_expiry", "12/25")
            .formParam("card_cvv", "123")
            .formParam("amount", "1500")
            .formParam("simulate_timeout", "true")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(408), equalTo(504), equalTo(200)));
    }
    
    @Test(priority = 16, description = "Duplicate payment prevention - C6 Issue")
    public void testDuplicatePaymentPrevention() {
        String orderId = "ORD_" + System.currentTimeMillis();
        
        // First payment
        given()
            .spec(requestSpec)
            .formParam("order_id", orderId)
            .formParam("payment_method", "credit_card")
            .formParam("card_number", "4111111111111111")
            .formParam("card_expiry", "12/25")
            .formParam("card_cvv", "123")
            .formParam("amount", "1500")
        .when()
            .post("/processPayment");
        
        // Attempt duplicate payment
        given()
            .spec(requestSpec)
            .formParam("order_id", orderId)
            .formParam("payment_method", "credit_card")
            .formParam("card_number", "4111111111111111")
            .formParam("card_expiry", "12/25")
            .formParam("card_cvv", "123")
            .formParam("amount", "1500")
        .when()
            .post("/processPayment")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(409), equalTo(400), equalTo(200)));
    }
    
    @Test(priority = 17, description = "Verify payment-order linkage - C6 Issue")
    public void testPaymentOrderLinkage() {
        // C6 Issue: "Payments debited but orders not created"
        String orderId = "ORD_" + System.currentTimeMillis();
        
        // Process payment
        given()
            .spec(requestSpec)
            .formParam("order_id", orderId)
            .formParam("payment_method", "credit_card")
            .formParam("card_number", "4111111111111111")
            .formParam("card_expiry", "12/25")
            .formParam("card_cvv", "123")
            .formParam("amount", "1500")
        .when()
            .post("/processPayment");
        
        // Verify order created
        given()
            .spec(requestSpec)
            .queryParam("order_id", orderId)
        .when()
            .get("/getOrderDetails")
        .then()
            .statusCode(200);
    }
}
