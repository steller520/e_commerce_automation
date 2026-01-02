package com.e_commerce.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Page Object for Payment Processing during checkout
 */
public class PaymentPage extends BasePage {
    
    // Payment method selection
    @FindBy(css = "input[name='payment_method']")
    private List<WebElement> paymentMethodRadios;
    
    @FindBy(id = "payment_method_credit_card")
    private WebElement creditCardRadio;
    
    @FindBy(id = "payment_method_debit_card")
    private WebElement debitCardRadio;
    
    @FindBy(id = "payment_method_paypal")
    private WebElement paypalRadio;
    
    @FindBy(id = "payment_method_upi")
    private WebElement upiRadio;
    
    @FindBy(id = "payment_method_cod")
    private WebElement codRadio;
    
    // Credit/Debit card fields
    @FindBy(id = "card_number")
    private WebElement cardNumberField;
    
    @FindBy(id = "card_holder_name")
    private WebElement cardHolderNameField;
    
    @FindBy(id = "expiry_month")
    private WebElement expiryMonthDropdown;
    
    @FindBy(id = "expiry_year")
    private WebElement expiryYearDropdown;
    
    @FindBy(id = "cvv")
    private WebElement cvvField;
    
    // UPI fields
    @FindBy(id = "upi_id")
    private WebElement upiIdField;
    
    // Order summary
    @FindBy(css = ".order-total, #total_amount")
    private WebElement orderTotalElement;
    
    @FindBy(css = ".payment-amount")
    private WebElement paymentAmountElement;
    
    // Payment buttons
    @FindBy(css = "button[type='submit'], .pay-now-button, [data-test='place-order']")
    private WebElement payNowButton;
    
    @FindBy(linkText = "Place Order")
    private WebElement placeOrderButton;
    
    @FindBy(css = ".confirm-payment-button")
    private WebElement confirmPaymentButton;
    
    @FindBy(linkText = "Back")
    private WebElement backButton;
    
    // Payment processing indicators
    @FindBy(css = ".payment-processing, .loader, .spinner")
    private WebElement processingIndicator;
    
    @FindBy(css = ".payment-success-message")
    private WebElement successMessage;
    
    @FindBy(css = ".payment-error-message, .error-message, .alert-danger")
    private WebElement errorMessage;
    
    // Validation messages
    @FindBy(css = ".card-number-error")
    private WebElement cardNumberError;
    
    @FindBy(css = ".cvv-error")
    private WebElement cvvError;
    
    @FindBy(css = ".expiry-error")
    private WebElement expiryError;
    
    // Retry/Cancel buttons
    @FindBy(css = ".retry-payment-button")
    private WebElement retryButton;
    
    @FindBy(css = ".cancel-payment-button")
    private WebElement cancelButton;
    
    // Payment gateway redirect indicator
    @FindBy(css = ".gateway-redirect-message")
    private WebElement gatewayRedirectMessage;
    
    public PaymentPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Select credit card payment method
     */
    public void selectCreditCard() {
        try {
            WebElement radio = waitClickable(creditCardRadio);
            radio.click();
        } catch (Exception e) {
            // Fallback to first payment option
            if (!paymentMethodRadios.isEmpty()) {
                paymentMethodRadios.get(0).click();
            }
        }
    }
    
    /**
     * Select debit card payment method
     */
    public void selectDebitCard() {
        try {
            WebElement radio = waitClickable(debitCardRadio);
            radio.click();
        } catch (Exception e) {
            selectCreditCard(); // Fallback
        }
    }
    
    /**
     * Select PayPal payment method
     */
    public void selectPayPal() {
        try {
            WebElement radio = waitClickable(paypalRadio);
            radio.click();
        } catch (Exception e) {
            System.out.println("PayPal not available");
        }
    }
    
    /**
     * Select UPI payment method
     */
    public void selectUPI() {
        try {
            WebElement radio = waitClickable(upiRadio);
            radio.click();
        } catch (Exception e) {
            System.out.println("UPI not available");
        }
    }
    
    /**
     * Select Cash on Delivery
     */
    public void selectCOD() {
        try {
            WebElement radio = waitClickable(codRadio);
            radio.click();
        } catch (Exception e) {
            System.out.println("COD not available");
        }
    }
    
    /**
     * Fill complete credit card details
     */
    public void fillCardDetails(String cardNumber, String holderName, 
                                String expiryMonth, String expiryYear, String cvv) {
        waitVisible(cardNumberField).clear();
        cardNumberField.sendKeys(cardNumber);
        
        cardHolderNameField.clear();
        cardHolderNameField.sendKeys(holderName);
        
        new Select(expiryMonthDropdown).selectByVisibleText(expiryMonth);
        new Select(expiryYearDropdown).selectByVisibleText(expiryYear);
        
        cvvField.clear();
        cvvField.sendKeys(cvv);
    }
    
    /**
     * Fill card details with invalid card number
     */
    public void fillInvalidCardDetails(String invalidCardNumber, String holderName, 
                                       String expiryMonth, String expiryYear, String cvv) {
        fillCardDetails(invalidCardNumber, holderName, expiryMonth, expiryYear, cvv);
    }
    
    /**
     * Fill expired card details
     */
    public void fillExpiredCardDetails(String cardNumber, String holderName, String cvv) {
        waitVisible(cardNumberField).clear();
        cardNumberField.sendKeys(cardNumber);
        
        cardHolderNameField.clear();
        cardHolderNameField.sendKeys(holderName);
        
        // Select past month/year
        new Select(expiryMonthDropdown).selectByValue("01");
        new Select(expiryYearDropdown).selectByValue("2020");
        
        cvvField.clear();
        cvvField.sendKeys(cvv);
    }
    
    /**
     * Fill UPI ID
     */
    public void fillUPIId(String upiId) {
        waitVisible(upiIdField).clear();
        upiIdField.sendKeys(upiId);
    }
    
    /**
     * Click Pay Now button
     */
    public void clickPayNow() {
        try {
            WebElement btn = waitClickable(payNowButton);
            btn.click();
        } catch (Exception e) {
            // Fallback to place order button
            try {
                placeOrderButton.click();
            } catch (Exception ex) {
                confirmPaymentButton.click();
            }
        }
    }
    
    /**
     * Click Place Order button
     */
    public void placeOrder() {
        try {
            WebElement btn = waitClickable(placeOrderButton);
            btn.click();
        } catch (Exception e) {
            clickPayNow();
        }
    }
    
    /**
     * Wait for payment processing to complete
     */
    public void waitForPaymentProcessing(int timeoutSeconds) {
        try {
            Thread.sleep(2000); // Initial wait
            
            // Wait for processing indicator to appear and disappear
            long startTime = System.currentTimeMillis();
            long timeout = timeoutSeconds * 1000L;
            
            while (System.currentTimeMillis() - startTime < timeout) {
                try {
                    if (!processingIndicator.isDisplayed()) {
                        break;
                    }
                } catch (Exception e) {
                    break; // Processing complete
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Check if payment was successful
     */
    public boolean isPaymentSuccessful() {
        try {
            return successMessage.isDisplayed();
        } catch (Exception e) {
            // Check URL for success indicators
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            return currentUrl.contains("success") || 
                   currentUrl.contains("confirmation") ||
                   currentUrl.contains("thank-you");
        }
    }
    
    /**
     * Check if payment failed
     */
    public boolean isPaymentFailed() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            return currentUrl.contains("failed") || currentUrl.contains("error");
        }
    }
    
    /**
     * Get payment error message
     */
    public String getErrorMessage() {
        try {
            return waitVisible(errorMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get payment success message
     */
    public String getSuccessMessage() {
        try {
            return successMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Check if card number validation error is shown
     */
    public boolean hasCardNumberError() {
        try {
            return cardNumberError.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if CVV validation error is shown
     */
    public boolean hasCVVError() {
        try {
            return cvvError.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if expiry validation error is shown
     */
    public boolean hasExpiryError() {
        try {
            return expiryError.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get order total amount
     */
    public String getOrderTotal() {
        try {
            return waitVisible(orderTotalElement).getText().replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            return "0";
        }
    }
    
    /**
     * Get payment amount
     */
    public String getPaymentAmount() {
        try {
            return paymentAmountElement.getText().replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            return getOrderTotal();
        }
    }
    
    /**
     * Verify payment amount matches order total
     */
    public boolean isPaymentAmountCorrect() {
        try {
            String orderTotal = getOrderTotal();
            String paymentAmount = getPaymentAmount();
            
            double total = Double.parseDouble(orderTotal);
            double payment = Double.parseDouble(paymentAmount);
            
            return Math.abs(total - payment) < 0.01;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Click retry payment button
     */
    public void retryPayment() {
        try {
            WebElement btn = waitClickable(retryButton);
            btn.click();
        } catch (Exception e) {
            System.out.println("Retry button not available");
        }
    }
    
    /**
     * Click cancel payment button
     */
    public void cancelPayment() {
        try {
            WebElement btn = waitClickable(cancelButton);
            btn.click();
        } catch (Exception e) {
            System.out.println("Cancel button not available");
        }
    }
    
    /**
     * Check if retry button is available
     */
    public boolean isRetryButtonAvailable() {
        try {
            return retryButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if payment is processing
     */
    public boolean isPaymentProcessing() {
        try {
            return processingIndicator.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Simulate payment timeout by waiting
     */
    public void simulatePaymentTimeout(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Check if redirected to payment gateway
     */
    public boolean isRedirectedToGateway() {
        try {
            return gatewayRedirectMessage.isDisplayed() || 
                   !driver.getCurrentUrl().contains("checkout");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get number of available payment methods
     */
    public int getAvailablePaymentMethodsCount() {
        return paymentMethodRadios.size();
    }
    
    /**
     * Navigate back to shipping
     */
    public void goBack() {
        try {
            WebElement btn = waitClickable(backButton);
            btn.click();
        } catch (Exception e) {
            driver.navigate().back();
        }
    }
}
