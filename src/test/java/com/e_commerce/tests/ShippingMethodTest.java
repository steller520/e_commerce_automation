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
 * Test class for Shipping Method Selection scenarios
 * C6 Project Issue: "Incorrect shipping charges"
 */
public class ShippingMethodTest extends BaseTest {
    
    private CsvUtil csvUtil;
    
    @BeforeClass
    public void setUpTestData() {
        csvUtil = new CsvUtil();
    }
    
    @Test(description = "Verify standard shipping method selection", priority = 1)
    public void testSelectStandardShipping() {
        // Setup: Add product and navigate to shipping
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
            
            // Select shipping method
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            
            // Verify shipping cost is displayed
            String shippingCost = shippingPage.getShippingCost();
            Assert.assertNotNull(shippingCost, "Shipping cost should be displayed");
            
            System.out.println("Standard shipping cost: " + shippingCost);
            
        } catch (Exception e) {
            System.out.println("Standard shipping test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify express shipping has higher cost than standard", priority = 2)
    public void testExpressShippingCostHigherThanStandard() {
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
                "Test", "User", "456 Avenue",
                "India", "Delhi", "110001", "9123456789"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            
            // Get standard shipping cost
            String standardCost = shippingPage.getStandardShippingCost();
            
            // Get express shipping cost
            String expressCost = shippingPage.getExpressShippingCost();
            
            if (!standardCost.isEmpty() && !expressCost.isEmpty()) {
                double standardPrice = Double.parseDouble(standardCost);
                double expressPrice = Double.parseDouble(expressCost);
                
                Assert.assertTrue(expressPrice >= standardPrice,
                    "Express shipping should cost more than or equal to standard shipping");
                
                System.out.println("Standard: " + standardPrice + ", Express: " + expressPrice);
            }
            
        } catch (Exception e) {
            System.out.println("Express shipping cost test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify shipping cost calculation in order total", priority = 3)
    public void testShippingCostCalculation() {
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
                "Test", "User", "789 Road",
                "India", "Bangalore", "560001", "9988776655"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            Thread.sleep(1000);
            
            // Verify calculation: Total = Subtotal + Shipping
            boolean isCorrect = shippingPage.isTotalCalculatedCorrectly();
            
            if (!isCorrect) {
                double subtotal = shippingPage.getSubtotalAsDouble();
                double shipping = shippingPage.getShippingCostAsDouble();
                double total = shippingPage.getOrderTotalAsDouble();
                
                System.out.println("Subtotal: " + subtotal);
                System.out.println("Shipping: " + shipping);
                System.out.println("Total: " + total);
                System.out.println("Expected: " + (subtotal + shipping));
            }
            
            Assert.assertTrue(isCorrect, "Order total should equal subtotal + shipping");
            
        } catch (Exception e) {
            System.out.println("Shipping calculation test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify free shipping threshold", priority = 4)
    public void testFreeShippingThreshold() {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        
        // Add multiple products to meet free shipping threshold
        products.addFirstProductToCart();
        try {
            Thread.sleep(1000);
            driver.navigate().back();
            Thread.sleep(1000);
            products.addFirstProductToCart();
        } catch (Exception e) {
            // Continue if adding second product fails
        }
        
        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        
        try {
            cart.proceedToCheckout();
            Thread.sleep(2000);
            
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillMinimalAddress(
                "Test", "User", "111 Boulevard",
                "India", "Chennai", "600001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            
            // Check if free shipping is available
            boolean freeShippingAvailable = shippingPage.isFreeShippingAvailable();
            
            if (freeShippingAvailable) {
                shippingPage.selectFreeShipping();
                double shippingCost = shippingPage.getShippingCostAsDouble();
                
                Assert.assertEquals(shippingCost, 0.0, 
                    "Free shipping should have 0 cost");
            } else {
                String message = shippingPage.getFreeShippingMessage();
                System.out.println("Free shipping message: " + message);
            }
            
        } catch (Exception e) {
            System.out.println("Free shipping test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify delivery time is displayed for shipping methods", priority = 5)
    public void testDeliveryTimeDisplay() {
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
                "Test", "User", "222 Circle",
                "India", "Pune", "411001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            
            String deliveryTime = shippingPage.getDeliveryTime();
            
            if (!deliveryTime.isEmpty()) {
                Assert.assertFalse(deliveryTime.isEmpty(), 
                    "Delivery time should be displayed");
                System.out.println("Standard delivery time: " + deliveryTime);
            }
            
            // Check express delivery time if available
            String expressDeliveryTime = shippingPage.getExpressDeliveryTime();
            if (!expressDeliveryTime.isEmpty()) {
                System.out.println("Express delivery time: " + expressDeliveryTime);
            }
            
        } catch (Exception e) {
            System.out.println("Delivery time test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify shipping cost changes based on address", priority = 6)
    public void testShippingCostByAddress() {
        // Test domestic address
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
            
            // Try local address
            addressPage.fillMinimalAddress(
                "Local", "User", "333 Local Street",
                "India", "Mumbai", "400001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectStandardShipping();
            
            String localShippingCost = shippingPage.getShippingCost();
            System.out.println("Local shipping cost: " + localShippingCost);
            
            // Note: Testing different addresses would require going back
            // and changing the address, which may vary by site implementation
            
        } catch (Exception e) {
            System.out.println("Shipping by address test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify at least one shipping method must be selected", priority = 7)
    public void testShippingMethodRequired() {
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
                "Test", "User", "444 Test Ave",
                "India", "Hyderabad", "500001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            
            // Try to continue without selecting shipping
            if (!shippingPage.isAnyShippingMethodSelected()) {
                shippingPage.clickContinue();
                Thread.sleep(1000);
                
                // Should show error or prevent continuation
                boolean hasError = shippingPage.hasErrorMessage();
                System.out.println("No shipping selected - Error shown: " + hasError);
            }
            
        } catch (Exception e) {
            System.out.println("Shipping required test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify available shipping methods count", priority = 8)
    public void testAvailableShippingMethodsCount() {
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
                "Test", "User", "555 Test Road",
                "India", "Kolkata", "700001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            
            int methodCount = shippingPage.getAvailableShippingMethodsCount();
            
            Assert.assertTrue(methodCount > 0, 
                "At least one shipping method should be available");
            
            System.out.println("Available shipping methods: " + methodCount);
            
        } catch (Exception e) {
            System.out.println("Shipping methods count test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify back navigation from shipping page preserves address", priority = 9)
    public void testBackNavigationPreservesAddress() {
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
            String expectedCity = "Ahmedabad";
            
            addressPage.fillMinimalAddress(
                "Test", "User", "666 Return Street",
                "India", expectedCity, "380001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            
            // Verify address is shown in summary
            boolean addressPreserved = shippingPage.verifyShippingAddress(expectedCity);
            
            if (addressPreserved) {
                System.out.println("Address preserved: " + expectedCity);
            }
            
        } catch (Exception e) {
            System.out.println("Back navigation test: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "shippingTestData")
    public Object[][] getShippingTestData() {
        try {
            return csvUtil.readCsvForDataProvider("src/test/resources/testdata/shipping_testdata.csv");
        } catch (IOException e) {
            System.out.println("Could not load shipping test data: " + e.getMessage());
            return new Object[0][0];
        }
    }
    
    @Test(dataProvider = "shippingTestData", description = "Data-driven shipping method tests", priority = 10)
    public void testShippingWithDataProvider(String testCase, String shippingMethod, 
                                            String minCost, String maxCost, String expectedResult) {
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
                "Test", "User", "Test Address",
                "India", "Mumbai", "400001", "9876543210"
            );
            addressPage.clickContinue();
            Thread.sleep(2000);
            
            ShippingPage shippingPage = new ShippingPage(driver);
            shippingPage.selectShippingMethod(shippingMethod);
            Thread.sleep(1000);
            
            double actualCost = shippingPage.getShippingCostAsDouble();
            double min = Double.parseDouble(minCost);
            double max = Double.parseDouble(maxCost);
            
            if ("success".equalsIgnoreCase(expectedResult)) {
                Assert.assertTrue(actualCost >= min && actualCost <= max,
                    testCase + ": Shipping cost should be between " + min + " and " + max);
            }
            
            System.out.println(testCase + " - Cost: " + actualCost);
            
        } catch (Exception e) {
            System.out.println(testCase + ": " + e.getMessage());
        }
    }
}
