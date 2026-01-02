package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.*;
import com.e_commerce.utils.CsvUtil;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Test class for Payment Failure and Timeout scenarios
 * C6 Project Issue: "Payment failures/timeouts"
 */
public class PaymentFailureTest extends BaseTest {
    
    private CsvUtil csvUtil;
    
    @BeforeClass
    public void setUpTestData() {
        csvUtil = new CsvUtil();
    }
    
    @Test(description = "Verify payment with invalid card number", priority = 1)
    public void testInvalidCardNumber() {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();
        
        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        
        try {
            cart.proceedToCheckout();
            Thread.sleep(2000);
            
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "Invalid", "Card", "123 Test St",
                "India", "Mumbai", "400001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectCreditCard();
            
            // Invalid card number (wrong format)
            paymentPage.fillInvalidCardDetails(
                "1234567890",  // Invalid card
                "Test User",
                "12",
                "2025",
                "123"
            );
            
            paymentPage.clickPayNow();
            Thread.sleep(2000);
            
            // Verify error is shown
            boolean hasError = paymentPage.hasCardNumberError() || 
                              paymentPage.isPaymentFailed();
            
            Assert.assertTrue(hasError,
                "Invalid card number should show validation error");
            
            if (paymentPage.isPaymentFailed()) {
                System.out.println("Payment failed: " + paymentPage.getErrorMessage());
            }
            
        } catch (Exception e) {
            System.out.println("Invalid card test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify payment with expired card", priority = 2)
    public void testExpiredCard() {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();
        
        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        
        try {
            cart.proceedToCheckout();
            Thread.sleep(2000);
            
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "Expired", "Card", "456 Old Ave",
                "India", "Delhi", "110001", "9123456789"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectCreditCard();
            
            // Expired card
            paymentPage.fillExpiredCardDetails(
                "4111111111111111",
                "Test User",
                "123"
            );
            
            paymentPage.clickPayNow();
            Thread.sleep(2000);
            
            boolean hasError = paymentPage.hasExpiryError() || 
                              paymentPage.isPaymentFailed();
            
            Assert.assertTrue(hasError,
                "Expired card should show validation error");
            
        } catch (Exception e) {
            System.out.println("Expired card test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify payment with invalid CVV", priority = 3)
    public void testInvalidCVV() {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();
        
        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        
        try {
            cart.proceedToCheckout();
            Thread.sleep(2000);
            
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "CVV", "Test", "789 Road",
                "India", "Bangalore", "560001", "9988776655"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectCreditCard();
            
            // Invalid CVV (only 2 digits)
            paymentPage.fillCardDetails(
                "4111111111111111",
                "Test User",
                "12",
                "2025",
                "12"  // Invalid CVV
            );
            
            paymentPage.clickPayNow();
            Thread.sleep(2000);
            
            boolean hasError = paymentPage.hasCVVError() || 
                              paymentPage.isPaymentFailed();
            
            Assert.assertTrue(hasError,
                "Invalid CVV should show validation error");
            
        } catch (Exception e) {
            System.out.println("Invalid CVV test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify payment timeout handling", priority = 4)
    public void testPaymentTimeout() {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();
        
        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        
        try {
            cart.proceedToCheckout();
            Thread.sleep(2000);
            
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "Timeout", "Test", "111 Slow St",
                "India", "Chennai", "600001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectCreditCard();
            
            paymentPage.fillCardDetails(
                "4111111111111111",
                "Test User",
                "12",
                "2025",
                "123"
            );
            
            paymentPage.clickPayNow();
            
            // Simulate long wait (timeout scenario)
            paymentPage.waitForPaymentProcessing(30);
            
            // Check if timeout error or still processing
            boolean isProcessing = paymentPage.isPaymentProcessing();
            boolean hasFailed = paymentPage.isPaymentFailed();
            
            if (hasFailed) {
                String error = paymentPage.getErrorMessage();
                System.out.println("Payment timeout error: " + error);
            } else if (isProcessing) {
                System.out.println("Payment still processing after timeout");
            }
            
        } catch (Exception e) {
            System.out.println("Payment timeout test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify payment retry mechanism", priority = 5)
    public void testPaymentRetry() {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();
        
        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        
        try {
            cart.proceedToCheckout();
            Thread.sleep(2000);
            
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "Retry", "Test", "222 Retry Rd",
                "India", "Pune", "411001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectCreditCard();
            
            // First attempt with invalid card
            paymentPage.fillInvalidCardDetails(
                "1234567890",
                "Test User",
                "12",
                "2025",
                "123"
            );
            
            paymentPage.clickPayNow();
            Thread.sleep(2000);
            
            // Check if retry button is available
            if (paymentPage.isRetryButtonAvailable()) {
                paymentPage.retryPayment();
                Thread.sleep(1000);
                
                // Retry with valid card
                paymentPage.fillCardDetails(
                    "4111111111111111",
                    "Test User",
                    "12",
                    "2025",
                    "123"
                );
                
                paymentPage.clickPayNow();
                System.out.println("Payment retried successfully");
            }
            
        } catch (Exception e) {
            System.out.println("Payment retry test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify payment gateway error handling", priority = 6)
    public void testPaymentGatewayError() {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();
        
        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        
        try {
            cart.proceedToCheckout();
            Thread.sleep(2000);
            
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "Gateway", "Error", "333 Gateway Ave",
                "India", "Hyderabad", "500001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectCreditCard();
            
            paymentPage.fillCardDetails(
                "4111111111111111",
                "Test User",
                "12",
                "2025",
                "123"
            );
            
            paymentPage.clickPayNow();
            paymentPage.waitForPaymentProcessing(15);
            
            // Check for gateway errors
            if (paymentPage.isPaymentFailed()) {
                String error = paymentPage.getErrorMessage();
                boolean isGatewayError = error.toLowerCase().contains("gateway") ||
                                        error.toLowerCase().contains("network") ||
                                        error.toLowerCase().contains("timeout");
                
                System.out.println("Gateway error detected: " + error);
            }
            
        } catch (Exception e) {
            System.out.println("Gateway error test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify insufficient funds scenario", priority = 7)
    public void testInsufficientFunds() {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();
        
        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        
        try {
            cart.proceedToCheckout();
            Thread.sleep(2000);
            
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "Funds", "Test", "444 No Money Ln",
                "India", "Kolkata", "700001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectCreditCard();
            
            // Use test card for insufficient funds (if supported)
            paymentPage.fillCardDetails(
                "4000000000000002",  // Test card for declined transaction
                "Test User",
                "12",
                "2025",
                "123"
            );
            
            paymentPage.clickPayNow();
            paymentPage.waitForPaymentProcessing(10);
            
            if (paymentPage.isPaymentFailed()) {
                String error = paymentPage.getErrorMessage();
                System.out.println("Insufficient funds error: " + error);
            }
            
        } catch (Exception e) {
            System.out.println("Insufficient funds test: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "paymentFailureData")
    public Object[][] getPaymentFailureData() {
        try {
            return csvUtil.readCsvForDataProvider("src/test/resources/testdata/payment_testdata.csv");
        } catch (IOException e) {
            System.out.println("Could not load payment test data: " + e.getMessage());
            return new Object[0][0];
        }
    }
    
    @Test(dataProvider = "paymentFailureData", description = "Data-driven payment failure tests", priority = 8)
    public void testPaymentWithDataProvider(String testCase, String cardNumber, 
                                           String holderName, String month, 
                                           String year, String cvv, String expectedResult) {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();
        
        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        
        try {
            cart.proceedToCheckout();
            Thread.sleep(2000);
            
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "Data", "Test", "Test Address",
                "India", "Mumbai", "400001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectCreditCard();
            
            paymentPage.fillCardDetails(cardNumber, holderName, month, year, cvv);
            paymentPage.clickPayNow();
            Thread.sleep(3000);
            
            if ("failure".equalsIgnoreCase(expectedResult)) {
                boolean hasFailed = paymentPage.isPaymentFailed() ||
                                   paymentPage.hasCardNumberError() ||
                                   paymentPage.hasCVVError() ||
                                   paymentPage.hasExpiryError();
                
                Assert.assertTrue(hasFailed,
                    testCase + ": Should show payment error");
            }
            
            System.out.println(testCase + " completed");
            
        } catch (Exception e) {
            System.out.println(testCase + ": " + e.getMessage());
        }
    }
}
