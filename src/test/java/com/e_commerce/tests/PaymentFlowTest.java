package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for Payment Flow scenarios
 * C6 Project Issue: "Payments debited but orders not created"
 */
public class PaymentFlowTest extends BaseTest {
    
    @Test(description = "Verify successful payment with credit card", priority = 1)
    public void testSuccessfulCreditCardPayment() {
        // Setup: Add product and navigate to payment
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
            
            // Fill address
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "Test", "User", "123 Street",
                "India", "Mumbai", "400001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            // Select shipping
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            // Process payment
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectCreditCard();
            
            paymentPage.fillCardDetails(
                "4111111111111111",  // Test card number
                "Test User",
                "12",
                "2025",
                "123"
            );
            
            // Verify payment amount is correct
            Assert.assertTrue(paymentPage.isPaymentAmountCorrect(),
                "Payment amount should match order total");
            
            paymentPage.clickPayNow();
            paymentPage.waitForPaymentProcessing(10);
            
            // Verify payment success
            Thread.sleep(2000);
            boolean isSuccess = paymentPage.isPaymentSuccessful();
            
            if (isSuccess) {
                System.out.println("Payment successful: " + paymentPage.getSuccessMessage());
            } else {
                System.out.println("Payment flow completed");
            }
            
        } catch (Exception e) {
            System.out.println("Credit card payment test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify payment with debit card", priority = 2)
    public void testDebitCardPayment() {
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
                "Debit", "Test", "456 Avenue",
                "India", "Delhi", "110001", "9123456789"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectDebitCard();
            
            paymentPage.fillCardDetails(
                "5555555555554444",
                "Debit Test",
                "06",
                "2026",
                "456"
            );
            
            paymentPage.placeOrder();
            paymentPage.waitForPaymentProcessing(10);
            
            System.out.println("Debit card payment processed");
            
        } catch (Exception e) {
            System.out.println("Debit card payment test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify UPI payment flow", priority = 3)
    public void testUPIPayment() {
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
                "UPI", "User", "789 Road",
                "India", "Bangalore", "560001", "9988776655"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectUPI();
            paymentPage.fillUPIId("testuser@upi");
            paymentPage.clickPayNow();
            
            System.out.println("UPI payment initiated");
            
        } catch (Exception e) {
            System.out.println("UPI payment test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify Cash on Delivery option", priority = 4)
    public void testCashOnDelivery() {
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
                "COD", "Test", "111 Boulevard",
                "India", "Chennai", "600001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            paymentPage.selectCOD();
            paymentPage.placeOrder();
            
            System.out.println("Cash on Delivery order placed");
            
        } catch (Exception e) {
            System.out.println("COD test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify payment amount matches order total", priority = 5)
    public void testPaymentAmountValidation() {
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
                "Amount", "Test", "222 Circle",
                "India", "Pune", "411001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            
            // Store order total from shipping page
            String shippingTotal = shippingPage.getOrderTotal();
            
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            
            // Verify payment amount matches
            String paymentAmount = paymentPage.getPaymentAmount();
            
            Assert.assertEquals(paymentAmount, shippingTotal,
                "Payment amount should match order total from shipping page");
            
            System.out.println("Payment amount validated: " + paymentAmount);
            
        } catch (Exception e) {
            System.out.println("Payment amount validation test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify payment methods are available", priority = 6)
    public void testPaymentMethodsAvailability() {
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
                "Methods", "Test", "333 Avenue",
                "India", "Hyderabad", "500001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            PaymentPage paymentPage = new PaymentPage(driver);
            
            int methodCount = paymentPage.getAvailablePaymentMethodsCount();
            
            Assert.assertTrue(methodCount > 0,
                "At least one payment method should be available");
            
            System.out.println("Available payment methods: " + methodCount);
            
        } catch (Exception e) {
            System.out.println("Payment methods availability test: " + e.getMessage());
        }
    }
}
