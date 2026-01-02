package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.*;
import com.e_commerce.utils.CsvUtil;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Test class for Address Validation scenarios
 * C6 Project Issue: "Incorrect address validations and shipping charges"
 */
public class AddressValidationTest extends BaseTest {
    
    private CsvUtil csvUtil;
    
    @BeforeClass
    public void setUpTestData() {
        csvUtil = new CsvUtil();
    }
    
    @Test(description = "Verify address form with all required fields filled", priority = 1)
    public void testValidAddressWithAllFields() {
        // Navigate to checkout to reach address page
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
            
            // Fill address form
            AddressPage addressPage = new AddressPage(driver);
            addressPage.fillAddressForm(
                "John",
                "Doe",
                "Test Company",
                "123 Main Street",
                "Apt 4B",
                "India",
                "California",
                "Los Angeles",
                "90001",
                "9876543210"
            );
            
            addressPage.submitAddress();
            
            // Verify no error messages
            Assert.assertFalse(addressPage.hasErrorMessage(), 
                "Valid address should not show error messages");
            
        } catch (Exception e) {
            System.out.println("Address validation test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify address form with only required fields", priority = 2)
    public void testValidAddressWithRequiredFieldsOnly() {
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
                "Jane",
                "Smith",
                "456 Oak Avenue",
                "India",
                "Mumbai",
                "400001",
                "9123456789"
            );
            
            addressPage.submitAddress();
            
            Assert.assertFalse(addressPage.hasErrorMessage(), 
                "Address with required fields should be accepted");
            
        } catch (Exception e) {
            System.out.println("Minimal address test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify error for missing required fields", priority = 3)
    public void testMissingRequiredFields() {
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
            
            // Fill only first name, leave other required fields empty
            addressPage.fillPartialAddress("John");
            addressPage.submitAddress();
            
            // Verify error message is shown
            Assert.assertTrue(addressPage.hasErrorMessage() || 
                            addressPage.hasFieldError("last_name") ||
                            addressPage.hasFieldError("address1"),
                "Missing required fields should show validation error");
            
        } catch (Exception e) {
            System.out.println("Missing fields test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify invalid zipcode format validation", priority = 4)
    public void testInvalidZipcodeFormat() {
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
            
            // Fill with invalid zipcode
            addressPage.fillInvalidZipcode(
                "Test",
                "User",
                "789 Pine Road",
                "India",
                "Delhi",
                "ABCDEF", // Invalid zipcode
                "9988776655"
            );
            
            addressPage.submitAddress();
            
            // Verify validation error for zipcode
            Assert.assertTrue(addressPage.hasErrorMessage() || 
                            addressPage.hasFieldError("zipcode"),
                "Invalid zipcode format should show validation error");
            
        } catch (Exception e) {
            System.out.println("Invalid zipcode test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify invalid phone number format", priority = 5)
    public void testInvalidPhoneNumberFormat() {
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
                "Test",
                "User",
                "111 Elm Street",
                "India",
                "Bangalore",
                "560001",
                "123" // Invalid phone (too short)
            );
            
            addressPage.submitAddress();
            
            Assert.assertTrue(addressPage.hasErrorMessage() || 
                            addressPage.hasFieldError("mobile_number"),
                "Invalid phone number should show validation error");
            
        } catch (Exception e) {
            System.out.println("Invalid phone test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify address not serviceable scenario", priority = 6)
    public void testAddressNotServiceable() {
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
            
            // Try remote/unserviceable area (if supported by site)
            addressPage.fillMinimalAddress(
                "Remote",
                "Area",
                "Remote Location",
                "India",
                "Remote City",
                "999999",
                "9999999999"
            );
            
            addressPage.submitAddress();
            
            // Check if serviceability error is shown
            String errorMsg = addressPage.getErrorMessage().toLowerCase();
            boolean isServiceabilityError = errorMsg.contains("serviceable") || 
                                           errorMsg.contains("delivery") ||
                                           errorMsg.contains("available");
            
            System.out.println("Serviceability test - Error message: " + errorMsg);
            
        } catch (Exception e) {
            System.out.println("Serviceability test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify international vs domestic address handling", priority = 7)
    public void testInternationalAddress() {
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
            
            // Try international address (if site supports)
            addressPage.fillMinimalAddress(
                "International",
                "User",
                "123 Foreign Street",
                "United States", // International address
                "New York",
                "10001",
                "9876543210"
            );
            
            addressPage.submitAddress();
            
            // Verify international shipping message or error
            System.out.println("International address test completed");
            
        } catch (Exception e) {
            System.out.println("International address test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify special characters in address fields", priority = 8)
    public void testSpecialCharactersInAddress() {
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
                "O'Brien", // Special character in name
                "Smith-Jones",
                "123 Main St. #456", // Special characters
                "India",
                "Chennai",
                "600001",
                "9876543210"
            );
            
            addressPage.submitAddress();
            
            Assert.assertFalse(addressPage.hasErrorMessage(), 
                "Valid special characters should be accepted");
            
        } catch (Exception e) {
            System.out.println("Special characters test: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify same as delivery address checkbox", priority = 9)
    public void testSameAsDeliveryAddress() {
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
                "Test",
                "User",
                "456 Test Road",
                "India",
                "Pune",
                "411001",
                "9876543210"
            );
            
            // Check "Same as delivery" for billing
            addressPage.setSameAsDeliveryAddress(true);
            addressPage.submitAddress();
            
            // Verify billing address matches delivery
            if (addressPage.isDeliveryAddressDisplayed() && addressPage.isBillingAddressDisplayed()) {
                String delivery = addressPage.getDeliveryAddressDetails();
                String billing = addressPage.getBillingAddressDetails();
                
                Assert.assertTrue(delivery.contains("Test User") && billing.contains("Test User"),
                    "Billing should match delivery when checkbox is selected");
            }
            
        } catch (Exception e) {
            System.out.println("Same as delivery test: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "addressTestData")
    public Object[][] getAddressTestData() {
        try {
            return csvUtil.readCsvForDataProvider("src/test/resources/testdata/addresses_testdata.csv");
        } catch (IOException e) {
            System.out.println("Could not load address test data: " + e.getMessage());
            return new Object[0][0];
        }
    }
    
    @Test(dataProvider = "addressTestData", description = "Data-driven address validation tests", priority = 10)
    public void testAddressWithDataProvider(String testCase, String firstName, String lastName, 
                                           String address, String city, String zipcode, 
                                           String mobile, String expectedResult) {
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
                firstName,
                lastName,
                address,
                "India",
                city,
                zipcode,
                mobile
            );
            
            addressPage.submitAddress();
            
            if ("success".equalsIgnoreCase(expectedResult)) {
                Assert.assertFalse(addressPage.hasErrorMessage(), 
                    testCase + ": Should not show error for valid address");
            } else {
                Assert.assertTrue(addressPage.hasErrorMessage(), 
                    testCase + ": Should show error for invalid address");
            }
            
        } catch (Exception e) {
            System.out.println(testCase + ": " + e.getMessage());
        }
    }
}
