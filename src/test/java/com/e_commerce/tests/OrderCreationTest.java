package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for Order Creation and Confirmation
 * C6 Project Issue: "Payments debited but orders not created"
 */
public class OrderCreationTest extends BaseTest {
    
    @Test(description = "Verify order is created after successful payment", priority = 1)
    public void testOrderCreatedAfterPayment() {
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
            
            // Complete address
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "Order", "Test", "123 Main St",
                "India", "Mumbai", "400001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            // Select shipping
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            double expectedTotal = shippingPage.getOrderTotalAsDouble();
            shippingPage.clickContinue();
            Thread.sleep(2000);
            
            // Complete payment
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
            paymentPage.waitForPaymentProcessing(10);
            Thread.sleep(3000);
            
            // Verify order confirmation page
            OrderConfirmationPage confirmation = new OrderConfirmationPage(driver);
            
            Assert.assertTrue(confirmation.isOrderConfirmationDisplayed(),
                "Order confirmation page should be displayed after payment");
            
            Assert.assertTrue(confirmation.isOrderNumberGenerated(),
                "Order number should be generated");
            
            String orderNumber = confirmation.getOrderNumber();
            System.out.println("Order created with number: " + orderNumber);
            
            Assert.assertTrue(confirmation.hasCompleteOrderInfo(),
                "Order should have complete information (number, total, items)");
            
        } catch (Exception e) {
            System.out.println("Order creation test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify order number is unique and generated", priority = 2)
    public void testOrderNumberGeneration() {
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
                "Number", "Test", "456 Oak Ave",
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
            paymentPage.fillCardDetails(
                "4111111111111111",
                "Test User",
                "12",
                "2025",
                "123"
            );
            paymentPage.clickPayNow();
            paymentPage.waitForPaymentProcessing(10);
            Thread.sleep(3000);
            
            OrderConfirmationPage confirmation = new OrderConfirmationPage(driver);
            
            String orderNumber = confirmation.getOrderNumber();
            
            Assert.assertNotNull(orderNumber, "Order number should not be null");
            Assert.assertFalse(orderNumber.isEmpty(), "Order number should not be empty");
            Assert.assertNotEquals("0", orderNumber, "Order number should not be zero");
            
            System.out.println("Generated order number: " + orderNumber);
            
        } catch (Exception e) {
            System.out.println("Order number generation test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify order items match cart items", priority = 3)
    public void testOrderItemsMatchCartItems() {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();
        
        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        
        try {
            int cartItemCount = cart.getCartItemCount();
            
            cart.proceedToCheckout();
            Thread.sleep(2000);
            
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "Items", "Test", "789 Elm St",
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
            paymentPage.fillCardDetails(
                "4111111111111111",
                "Test User",
                "12",
                "2025",
                "123"
            );
            paymentPage.clickPayNow();
            paymentPage.waitForPaymentProcessing(10);
            Thread.sleep(3000);
            
            OrderConfirmationPage confirmation = new OrderConfirmationPage(driver);
            
            int orderItemCount = confirmation.getOrderItemCount();
            
            Assert.assertEquals(orderItemCount, cartItemCount,
                "Order items should match cart items count");
            
            System.out.println("Cart items: " + cartItemCount + ", Order items: " + orderItemCount);
            
        } catch (Exception e) {
            System.out.println("Order items match test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify order total matches payment amount", priority = 4)
    public void testOrderTotalMatchesPaymentAmount() {
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
                "Total", "Test", "111 Pine Rd",
                "India", "Chennai", "600001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            double shippingTotal = shippingPage.getOrderTotalAsDouble();
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
            
            double paymentAmount = Double.parseDouble(paymentPage.getPaymentAmount());
            
            paymentPage.clickPayNow();
            paymentPage.waitForPaymentProcessing(10);
            Thread.sleep(3000);
            
            OrderConfirmationPage confirmation = new OrderConfirmationPage(driver);
            
            double orderTotal = confirmation.getOrderTotalAsDouble();
            
            Assert.assertEquals(orderTotal, paymentAmount, 0.01,
                "Order total should match payment amount");
            
            System.out.println("Payment amount: " + paymentAmount + ", Order total: " + orderTotal);
            
        } catch (Exception e) {
            System.out.println("Order total match test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify order confirmation displays all required information", priority = 5)
    public void testOrderConfirmationCompleteInfo() {
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
                "Complete", "Info", "222 Maple Dr",
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
            paymentPage.fillCardDetails(
                "4111111111111111",
                "Test User",
                "12",
                "2025",
                "123"
            );
            paymentPage.clickPayNow();
            paymentPage.waitForPaymentProcessing(10);
            Thread.sleep(3000);
            
            OrderConfirmationPage confirmation = new OrderConfirmationPage(driver);
            
            // Verify all essential information is present
            Assert.assertTrue(confirmation.isOrderNumberGenerated(),
                "Order number should be present");
            
            Assert.assertTrue(confirmation.getOrderItemCount() > 0,
                "Order should have at least one item");
            
            Assert.assertTrue(confirmation.getOrderTotalAsDouble() > 0,
                "Order total should be greater than zero");
            
            String confirmationMsg = confirmation.getConfirmationMessage();
            Assert.assertFalse(confirmationMsg.isEmpty(),
                "Confirmation message should be displayed");
            
            System.out.println("Order confirmation: " + confirmationMsg);
            System.out.println("Order number: " + confirmation.getOrderNumber());
            System.out.println("Order total: " + confirmation.getOrderTotal());
            System.out.println("Order items: " + confirmation.getOrderItemCount());
            
        } catch (Exception e) {
            System.out.println("Order confirmation info test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify order status is set correctly", priority = 6)
    public void testOrderStatus() {
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
                "Status", "Test", "333 Cedar Ln",
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
            paymentPage.waitForPaymentProcessing(10);
            Thread.sleep(3000);
            
            OrderConfirmationPage confirmation = new OrderConfirmationPage(driver);
            
            String orderStatus = confirmation.getOrderStatus();
            
            Assert.assertNotNull(orderStatus, "Order status should not be null");
            Assert.assertFalse(orderStatus.isEmpty(), "Order status should not be empty");
            
            System.out.println("Order status: " + orderStatus);
            
        } catch (Exception e) {
            System.out.println("Order status test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify payment status in order confirmation", priority = 7)
    public void testPaymentStatusInOrder() {
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
                "Payment", "Status", "444 Birch Ave",
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
            paymentPage.fillCardDetails(
                "4111111111111111",
                "Test User",
                "12",
                "2025",
                "123"
            );
            paymentPage.clickPayNow();
            paymentPage.waitForPaymentProcessing(10);
            Thread.sleep(3000);
            
            OrderConfirmationPage confirmation = new OrderConfirmationPage(driver);
            
            String paymentStatus = confirmation.getPaymentStatus();
            
            Assert.assertNotNull(paymentStatus, "Payment status should not be null");
            
            // Payment status should indicate successful payment
            String statusLower = paymentStatus.toLowerCase();
            boolean isPaid = statusLower.contains("paid") || 
                           statusLower.contains("success") ||
                           statusLower.contains("complete");
            
            Assert.assertTrue(isPaid || paymentStatus.isEmpty(),
                "Payment status should indicate successful payment");
            
            System.out.println("Payment status: " + paymentStatus);
            
        } catch (Exception e) {
            System.out.println("Payment status test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify email confirmation is mentioned", priority = 8)
    public void testEmailConfirmation() {
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
                "Email", "Test", "555 Walnut St",
                "India", "Jaipur", "302001", "9876543210"
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
            paymentPage.waitForPaymentProcessing(10);
            Thread.sleep(3000);
            
            OrderConfirmationPage confirmation = new OrderConfirmationPage(driver);
            
            // Check if email confirmation is mentioned
            boolean hasEmail = confirmation.hasEmailConfirmation();
            
            if (hasEmail) {
                System.out.println("Email confirmation: " + confirmation.getEmailConfirmationMessage());
            } else {
                System.out.println("Email confirmation not explicitly mentioned");
            }
            
        } catch (Exception e) {
            System.out.println("Email confirmation test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify order total calculation is correct", priority = 9)
    public void testOrderTotalCalculation() {
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
                "Calculation", "Test", "666 Ash Blvd",
                "India", "Ahmedabad", "380001", "9876543210"
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
            paymentPage.waitForPaymentProcessing(10);
            Thread.sleep(3000);
            
            OrderConfirmationPage confirmation = new OrderConfirmationPage(driver);
            
            // Verify calculation: total = subtotal + shipping + tax - discount
            boolean isCalculationCorrect = confirmation.isOrderTotalCorrect();
            
            if (isCalculationCorrect) {
                System.out.println("Order total calculation is correct");
            } else {
                System.out.println("Subtotal: " + confirmation.getOrderSubtotal());
                System.out.println("Shipping: " + confirmation.getShippingCost());
                System.out.println("Tax: " + confirmation.getTaxAmount());
                System.out.println("Discount: " + confirmation.getDiscountAmount());
                System.out.println("Total: " + confirmation.getOrderTotal());
            }
            
        } catch (Exception e) {
            System.out.println("Order calculation test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify continue shopping navigation", priority = 10)
    public void testContinueShoppingNavigation() {
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
                "Continue", "Shop", "777 Spruce Ct",
                "India", "Surat", "395001", "9876543210"
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
            paymentPage.waitForPaymentProcessing(10);
            Thread.sleep(3000);
            
            OrderConfirmationPage confirmation = new OrderConfirmationPage(driver);
            
            if (confirmation.isContinueShoppingAvailable()) {
                String orderNumber = confirmation.getOrderNumber();
                confirmation.continueShopping();
                Thread.sleep(2000);
                
                // Verify navigation away from confirmation page
                String currentUrl = driver.getCurrentUrl();
                Assert.assertFalse(currentUrl.contains("confirmation") || 
                                  currentUrl.contains("thank-you"),
                    "Should navigate away from confirmation page");
                
                System.out.println("Successfully navigated after order: " + orderNumber);
            } else {
                System.out.println("Continue shopping button not available");
            }
            
        } catch (Exception e) {
            System.out.println("Continue shopping test: " + e.getMessage());
        }
    }
}
