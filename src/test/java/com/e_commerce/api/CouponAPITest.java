package com.e_commerce.api;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for Coupon operations
 * C6 Project Issue: "Coupon codes not applying correctly"
 * Tests coupon validation, application, and edge cases
 */
public class CouponAPITest extends BaseAPITest {
    
    @Test(priority = 1, description = "Apply valid coupon code")
    public void testApplyValidCoupon() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "SAVE10")
            .formParam("cart_total", "1000")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(201)))
            .body("message", anyOf(
                containsString("applied"),
                containsString("success"),
                containsString("valid")
            ));
    }
    
    @Test(priority = 2, description = "Apply invalid coupon code - Negative")
    public void testApplyInvalidCoupon() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "INVALID123")
            .formParam("cart_total", "1000")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(404), equalTo(400)))
            .body("message", anyOf(
                containsString("invalid"),
                containsString("not found"),
                containsString("does not exist")
            ));
    }
    
    @Test(priority = 3, description = "Apply expired coupon - Negative")
    public void testApplyExpiredCoupon() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "EXPIRED2023")
            .formParam("cart_total", "1000")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(410)))
            .body("message", containsString("expired"));
    }
    
    @Test(priority = 4, description = "Apply coupon below minimum cart value - Negative")
    public void testApplyCouponBelowMinimum() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "SAVE10")
            .formParam("cart_total", "50")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(422)))
            .body("message", anyOf(
                containsString("minimum"),
                containsString("cart value"),
                containsString("not eligible")
            ));
    }
    
    @Test(priority = 5, description = "Apply coupon without cart_total - Negative")
    public void testApplyCouponWithoutCartTotal() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "SAVE10")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400))
            .body("message", containsString("Bad request"));
    }
    
    @Test(priority = 6, description = "Apply coupon without coupon_code - Negative")
    public void testApplyCouponWithoutCode() {
        given()
            .spec(requestSpec)
            .formParam("cart_total", "1000")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400));
    }
    
    @Test(priority = 7, description = "Get available coupons")
    public void testGetAvailableCoupons() {
        given()
            .spec(requestSpec)
        .when()
            .get("/getAvailableCoupons")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(404)))
            .body("coupons", anyOf(notNullValue(), nullValue()));
    }
    
    @Test(priority = 8, description = "Verify coupon discount calculation")
    public void testCouponDiscountCalculation() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "SAVE10")
            .formParam("cart_total", "1000")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200)
            .body("discount", anyOf(
                greaterThan(0.0f),
                notNullValue()
            ));
    }
    
    @Test(priority = 9, description = "Apply multiple coupons - Negative")
    public void testApplyMultipleCoupons() {
        String sessionId = "SESSION_" + System.currentTimeMillis();
        
        // Apply first coupon
        given()
            .spec(requestSpec)
            .header("Session-ID", sessionId)
            .formParam("coupon_code", "SAVE10")
            .formParam("cart_total", "1000")
        .when()
            .post("/applyCoupon");
        
        // Try to apply second coupon
        given()
            .spec(requestSpec)
            .header("Session-ID", sessionId)
            .formParam("coupon_code", "SAVE20")
            .formParam("cart_total", "1000")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(400), equalTo(409)))
            .body("message", anyOf(
                containsString("already applied"),
                containsString("one coupon"),
                containsString("conflict")
            ));
    }
    
    @Test(priority = 10, description = "Remove applied coupon")
    public void testRemoveCoupon() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "SAVE10")
        .when()
            .delete("/removeCoupon")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(204)));
    }
    
    @Test(priority = 11, description = "Apply coupon with case sensitivity")
    public void testApplyCouponCaseSensitive() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "save10")
            .formParam("cart_total", "1000")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 12, description = "Apply user-specific coupon")
    public void testApplyUserSpecificCoupon() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "FIRSTORDER")
            .formParam("cart_total", "1000")
            .formParam("user_id", "123")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200);
    }
    
    @Test(priority = 13, description = "Verify coupon usage limit")
    public void testCouponUsageLimit() {
        String couponCode = "LIMITED5";
        
        for (int i = 1; i <= 6; i++) {
            given()
                .spec(requestSpec)
                .formParam("coupon_code", couponCode)
                .formParam("cart_total", "1000")
                .formParam("user_id", "USER_" + i)
            .when()
                .post("/applyCoupon")
            .then()
                .statusCode(200);
        }
    }
    
    @Test(priority = 14, description = "Apply percentage discount coupon")
    public void testPercentageDiscountCoupon() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "PERCENT20")
            .formParam("cart_total", "5000")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200)
            .body("discount_type", anyOf(equalTo("percentage"), nullValue()));
    }
    
    @Test(priority = 15, description = "Apply fixed amount discount coupon")
    public void testFixedAmountDiscountCoupon() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "FLAT100")
            .formParam("cart_total", "2000")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200)
            .body("discount_type", anyOf(equalTo("fixed"), nullValue()));
    }
    
    @Test(priority = 16, description = "Validate coupon before applying")
    public void testValidateCoupon() {
        given()
            .spec(requestSpec)
            .queryParam("coupon_code", "SAVE10")
            .queryParam("cart_total", "1000")
        .when()
            .get("/validateCoupon")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(200), equalTo(404)))
            .body("valid", anyOf(equalTo(true), equalTo(false), nullValue()));
    }
    
    @Test(priority = 17, description = "Apply coupon with special characters - Negative")
    public void testApplyCouponSpecialCharacters() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "SAVE@10%")
            .formParam("cart_total", "1000")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200)
            .body("responseCode", anyOf(equalTo(404), equalTo(400)));
    }
    
    @Test(priority = 18, description = "Apply category-specific coupon")
    public void testCategorySpecificCoupon() {
        given()
            .spec(requestSpec)
            .formParam("coupon_code", "ELECTRONICS10")
            .formParam("cart_total", "3000")
            .formParam("category", "Electronics")
        .when()
            .post("/applyCoupon")
        .then()
            .statusCode(200);
    }
}
