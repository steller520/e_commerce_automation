package com.e_commerce.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object for Shipping Method Selection during checkout
 */
public class ShippingPage extends BasePage {
    
    // Shipping method radio buttons
    @FindBy(css = "input[name='shipping_method']")
    private List<WebElement> shippingMethodRadios;
    
    @FindBy(id = "standard_shipping")
    private WebElement standardShippingRadio;
    
    @FindBy(id = "express_shipping")
    private WebElement expressShippingRadio;
    
    @FindBy(id = "next_day_shipping")
    private WebElement nextDayShippingRadio;
    
    @FindBy(id = "free_shipping")
    private WebElement freeShippingRadio;
    
    // Shipping cost display
    @FindBy(css = ".shipping-cost, #shipping_cost, [data-test='shipping-cost']")
    private WebElement shippingCostElement;
    
    @FindBy(css = ".standard-shipping-cost")
    private WebElement standardShippingCost;
    
    @FindBy(css = ".express-shipping-cost")
    private WebElement expressShippingCost;
    
    @FindBy(css = ".next-day-shipping-cost")
    private WebElement nextDayShippingCost;
    
    // Delivery time display
    @FindBy(css = ".delivery-time, .estimated-delivery")
    private WebElement deliveryTimeElement;
    
    @FindBy(css = ".standard-delivery-time")
    private WebElement standardDeliveryTime;
    
    @FindBy(css = ".express-delivery-time")
    private WebElement expressDeliveryTime;
    
    // Free shipping eligibility
    @FindBy(css = ".free-shipping-message, .free-shipping-threshold")
    private WebElement freeShippingMessage;
    
    @FindBy(css = ".free-shipping-eligible")
    private WebElement freeShippingEligible;
    
    // Total with shipping
    @FindBy(css = ".order-total, #total_amount, [data-test='total-amount']")
    private WebElement orderTotalElement;
    
    @FindBy(css = ".subtotal")
    private WebElement subtotalElement;
    
    // Buttons
    @FindBy(css = "button[type='submit'], .continue-button, [data-test='continue']")
    private WebElement continueButton;
    
    @FindBy(linkText = "Continue to Payment")
    private WebElement continueToPaymentButton;
    
    @FindBy(linkText = "Back")
    private WebElement backButton;
    
    // Error messages
    @FindBy(css = ".error-message, .alert-danger, .text-danger")
    private WebElement errorMessage;
    
    // Shipping address summary
    @FindBy(css = ".shipping-address-summary")
    private WebElement shippingAddressSummary;
    
    public ShippingPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Select standard shipping method
     */
    public void selectStandardShipping() {
        try {
            WebElement radio = waitClickable(standardShippingRadio);
            radio.click();
        } catch (Exception e) {
            // Fallback: find first shipping option
            if (!shippingMethodRadios.isEmpty()) {
                shippingMethodRadios.get(0).click();
            }
        }
    }
    
    /**
     * Select express shipping method
     */
    public void selectExpressShipping() {
        try {
            WebElement radio = waitClickable(expressShippingRadio);
            radio.click();
        } catch (Exception e) {
            // Fallback: find second shipping option if available
            if (shippingMethodRadios.size() > 1) {
                shippingMethodRadios.get(1).click();
            }
        }
    }
    
    /**
     * Select next day shipping method
     */
    public void selectNextDayShipping() {
        try {
            WebElement radio = waitClickable(nextDayShippingRadio);
            radio.click();
        } catch (Exception e) {
            // Fallback: find third shipping option if available
            if (shippingMethodRadios.size() > 2) {
                shippingMethodRadios.get(2).click();
            }
        }
    }
    
    /**
     * Select free shipping if available
     */
    public void selectFreeShipping() {
        try {
            WebElement radio = waitClickable(freeShippingRadio);
            radio.click();
        } catch (Exception e) {
            System.out.println("Free shipping not available");
        }
    }
    
    /**
     * Select shipping method by name
     */
    public void selectShippingMethod(String methodName) {
        methodName = methodName.toLowerCase();
        
        if (methodName.contains("standard")) {
            selectStandardShipping();
        } else if (methodName.contains("express")) {
            selectExpressShipping();
        } else if (methodName.contains("next") || methodName.contains("day")) {
            selectNextDayShipping();
        } else if (methodName.contains("free")) {
            selectFreeShipping();
        }
    }
    
    /**
     * Get shipping cost for current selection
     */
    public String getShippingCost() {
        try {
            return waitVisible(shippingCostElement).getText().replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            return "0";
        }
    }
    
    /**
     * Get shipping cost as double
     */
    public double getShippingCostAsDouble() {
        try {
            String cost = getShippingCost();
            return Double.parseDouble(cost);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * Get standard shipping cost
     */
    public String getStandardShippingCost() {
        try {
            return standardShippingCost.getText().replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            return "0";
        }
    }
    
    /**
     * Get express shipping cost
     */
    public String getExpressShippingCost() {
        try {
            return expressShippingCost.getText().replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            return "0";
        }
    }
    
    /**
     * Get delivery time estimate
     */
    public String getDeliveryTime() {
        try {
            return waitVisible(deliveryTimeElement).getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get standard delivery time
     */
    public String getStandardDeliveryTime() {
        try {
            return standardDeliveryTime.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get express delivery time
     */
    public String getExpressDeliveryTime() {
        try {
            return expressDeliveryTime.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Check if free shipping is available
     */
    public boolean isFreeShippingAvailable() {
        try {
            return freeShippingRadio.isDisplayed() && freeShippingRadio.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if free shipping is eligible
     */
    public boolean isFreeShippingEligible() {
        try {
            return freeShippingEligible.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get free shipping message
     */
    public String getFreeShippingMessage() {
        try {
            return freeShippingMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get order total (subtotal + shipping)
     */
    public String getOrderTotal() {
        try {
            return waitVisible(orderTotalElement).getText().replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            return "0";
        }
    }
    
    /**
     * Get order total as double
     */
    public double getOrderTotalAsDouble() {
        try {
            String total = getOrderTotal();
            return Double.parseDouble(total);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * Get subtotal (before shipping)
     */
    public String getSubtotal() {
        try {
            return subtotalElement.getText().replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            return "0";
        }
    }
    
    /**
     * Get subtotal as double
     */
    public double getSubtotalAsDouble() {
        try {
            String subtotal = getSubtotal();
            return Double.parseDouble(subtotal);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * Verify total = subtotal + shipping
     */
    public boolean isTotalCalculatedCorrectly() {
        try {
            double subtotal = getSubtotalAsDouble();
            double shipping = getShippingCostAsDouble();
            double total = getOrderTotalAsDouble();
            double expected = subtotal + shipping;
            
            // Allow small floating point difference
            return Math.abs(total - expected) < 0.01;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get number of available shipping methods
     */
    public int getAvailableShippingMethodsCount() {
        return shippingMethodRadios.size();
    }
    
    /**
     * Click continue to payment button
     */
    public void continueToPayment() {
        try {
            WebElement btn = waitClickable(continueToPaymentButton);
            btn.click();
        } catch (Exception e) {
            // Fallback to generic continue button
            WebElement contBtn = waitClickable(continueButton);
            contBtn.click();
        }
    }
    
    /**
     * Click continue button
     */
    public void clickContinue() {
        WebElement btn = waitClickable(continueButton);
        btn.click();
    }
    
    /**
     * Click back button
     */
    public void goBack() {
        WebElement btn = waitClickable(backButton);
        btn.click();
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
     * Get shipping address summary
     */
    public String getShippingAddressSummary() {
        try {
            return shippingAddressSummary.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Verify shipping address matches expected
     */
    public boolean verifyShippingAddress(String expectedCity) {
        try {
            String summary = getShippingAddressSummary();
            return summary.contains(expectedCity);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if any shipping method is selected
     */
    public boolean isAnyShippingMethodSelected() {
        for (WebElement radio : shippingMethodRadios) {
            if (radio.isSelected()) {
                return true;
            }
        }
        return false;
    }
}
