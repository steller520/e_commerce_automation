package com.e_commerce.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for Address Selection and Management during checkout
 */
public class AddressPage extends BasePage {
    
    // Address form fields
    @FindBy(id = "first_name")
    private WebElement firstNameField;
    
    @FindBy(id = "last_name")
    private WebElement lastNameField;
    
    @FindBy(id = "company")
    private WebElement companyField;
    
    @FindBy(id = "address1")
    private WebElement address1Field;
    
    @FindBy(id = "address2")
    private WebElement address2Field;
    
    @FindBy(id = "country")
    private WebElement countryDropdown;
    
    @FindBy(id = "state")
    private WebElement stateField;
    
    @FindBy(id = "city")
    private WebElement cityField;
    
    @FindBy(id = "zipcode")
    private WebElement zipcodeField;
    
    @FindBy(id = "mobile_number")
    private WebElement mobileNumberField;
    
    // Address selection (if saved addresses exist)
    @FindBy(css = "input[name='delivery_address']")
    private WebElement deliveryAddressRadio;
    
    @FindBy(css = "input[name='billing_address']")
    private WebElement billingAddressRadio;
    
    @FindBy(css = "input[name='same_as_delivery']")
    private WebElement sameAsDeliveryCheckbox;
    
    // Buttons
    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;
    
    @FindBy(linkText = "Continue")
    private WebElement continueButton;
    
    // Validation messages
    @FindBy(css = ".error-message, .text-danger, .invalid-feedback")
    private WebElement errorMessage;
    
    @FindBy(css = ".success-message, .text-success, .valid-feedback")
    private WebElement successMessage;
    
    // Address summary (after selection)
    @FindBy(css = ".delivery-address-details, #address_delivery")
    private WebElement deliveryAddressDetails;
    
    @FindBy(css = ".billing-address-details, #address_invoice")
    private WebElement billingAddressDetails;
    
    public AddressPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Fill complete address form
     */
    public void fillAddressForm(String firstName, String lastName, String company,
                                String address1, String address2, String country,
                                String state, String city, String zipcode, String mobile) {
        waitVisible(firstNameField).sendKeys(firstName);
        lastNameField.sendKeys(lastName);
        
        if (company != null && !company.isEmpty()) {
            companyField.sendKeys(company);
        }
        
        address1Field.sendKeys(address1);
        
        if (address2 != null && !address2.isEmpty()) {
            address2Field.sendKeys(address2);
        }
        
        new Select(countryDropdown).selectByVisibleText(country);
        stateField.sendKeys(state);
        cityField.sendKeys(city);
        zipcodeField.sendKeys(zipcode);
        mobileNumberField.sendKeys(mobile);
    }
    
    /**
     * Fill address with minimal required fields
     */
    public void fillMinimalAddress(String firstName, String lastName, String address1,
                                   String country, String city, String zipcode, String mobile) {
        waitVisible(firstNameField).sendKeys(firstName);
        lastNameField.sendKeys(lastName);
        address1Field.sendKeys(address1);
        new Select(countryDropdown).selectByVisibleText(country);
        cityField.sendKeys(city);
        zipcodeField.sendKeys(zipcode);
        mobileNumberField.sendKeys(mobile);
    }
    
    /**
     * Fill only first name (to test missing required fields)
     */
    public void fillPartialAddress(String firstName) {
        waitVisible(firstNameField).sendKeys(firstName);
    }
    
    /**
     * Fill invalid zipcode format
     */
    public void fillInvalidZipcode(String firstName, String lastName, String address1,
                                   String country, String city, String invalidZip, String mobile) {
        fillMinimalAddress(firstName, lastName, address1, country, city, invalidZip, mobile);
    }
    
    /**
     * Select saved delivery address (if available)
     */
    public void selectDeliveryAddress() {
        WebElement radio = waitClickable(deliveryAddressRadio);
        radio.click();
    }
    
    /**
     * Select saved billing address (if available)
     */
    public void selectBillingAddress() {
        WebElement radio = waitClickable(billingAddressRadio);
        radio.click();
    }
    
    /**
     * Check "Same as delivery address" for billing
     */
    public void setSameAsDeliveryAddress(boolean checked) {
        if (checked && !sameAsDeliveryCheckbox.isSelected()) {
            sameAsDeliveryCheckbox.click();
        } else if (!checked && sameAsDeliveryCheckbox.isSelected()) {
            sameAsDeliveryCheckbox.click();
        }
    }
    
    /**
     * Submit the address form
     */
    public void submitAddress() {
        WebElement btn = waitClickable(submitButton);
        btn.click();
    }
    
    /**
     * Click continue button
     */
    public void clickContinue() {
        try {
            WebElement btn = waitClickable(continueButton);
            btn.click();
        } catch (Exception e) {
            // Fallback if continue button has different locator
            submitAddress();
        }
    }
    
    /**
     * Check if error message is displayed
     */
    public boolean hasErrorMessage() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get error message text
     */
    public String getErrorMessage() {
        if (hasErrorMessage()) {
            return errorMessage.getText();
        }
        return "";
    }
    
    /**
     * Check if success message is displayed
     */
    public boolean hasSuccessMessage() {
        try {
            return successMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if delivery address is displayed
     */
    public boolean isDeliveryAddressDisplayed() {
        try {
            return waitVisible(deliveryAddressDetails).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if billing address is displayed
     */
    public boolean isBillingAddressDisplayed() {
        try {
            return waitVisible(billingAddressDetails).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get delivery address details text
     */
    public String getDeliveryAddressDetails() {
        if (isDeliveryAddressDisplayed()) {
            return deliveryAddressDetails.getText();
        }
        return "";
    }
    
    /**
     * Get billing address details text
     */
    public String getBillingAddressDetails() {
        if (isBillingAddressDisplayed()) {
            return billingAddressDetails.getText();
        }
        return "";
    }
    
    /**
     * Check if specific field shows validation error
     */
    public boolean hasFieldError(String fieldId) {
        try {
            WebElement field = driver.findElement(org.openqa.selenium.By.id(fieldId));
            String classAttr = field.getAttribute("class");
            return classAttr != null && (classAttr.contains("error") || classAttr.contains("invalid"));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if form is valid (no error messages)
     */
    public boolean isFormValid() {
        return !hasErrorMessage();
    }
    
    /**
     * Clear address form
     */
    public void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        address1Field.clear();
        cityField.clear();
        zipcodeField.clear();
        mobileNumberField.clear();
    }
}
