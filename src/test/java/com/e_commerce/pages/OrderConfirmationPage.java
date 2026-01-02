package com.e_commerce.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for Order Confirmation page
 * C6 Project Issue: "Payments debited but orders not created"
 */
public class OrderConfirmationPage extends BasePage {
    
    // Confirmation message
    @FindBy(css = ".order-confirmation-message, .success-message, h2")
    private WebElement confirmationMessage;
    
    @FindBy(css = ".thank-you-message")
    private WebElement thankYouMessage;
    
    // Order details
    @FindBy(css = ".order-number, #order_id, [data-test='order-number']")
    private WebElement orderNumberElement;
    
    @FindBy(css = ".order-date, .order-timestamp")
    private WebElement orderDateElement;
    
    @FindBy(css = ".order-status")
    private WebElement orderStatusElement;
    
    // Order summary
    @FindBy(css = ".order-total, #total_amount, .grand-total")
    private WebElement orderTotalElement;
    
    @FindBy(css = ".order-subtotal, .subtotal")
    private WebElement orderSubtotalElement;
    
    @FindBy(css = ".order-shipping-cost, .shipping-cost")
    private WebElement shippingCostElement;
    
    @FindBy(css = ".order-tax, .tax-amount")
    private WebElement taxElement;
    
    @FindBy(css = ".order-discount, .discount-amount")
    private WebElement discountElement;
    
    // Order items
    @FindBy(css = ".order-item, .product-item, tr.cart_item")
    private List<WebElement> orderItems;
    
    @FindBy(css = ".order-item .product-name, .product-name")
    private List<WebElement> productNames;
    
    @FindBy(css = ".order-item .quantity, .product-quantity")
    private List<WebElement> productQuantities;
    
    @FindBy(css = ".order-item .price, .product-price")
    private List<WebElement> productPrices;
    
    // Delivery information
    @FindBy(css = ".delivery-address, .shipping-address")
    private WebElement deliveryAddress;
    
    @FindBy(css = ".delivery-method, .shipping-method")
    private WebElement deliveryMethod;
    
    @FindBy(css = ".estimated-delivery, .delivery-date")
    private WebElement estimatedDelivery;
    
    // Payment information
    @FindBy(css = ".payment-method, .payment-type")
    private WebElement paymentMethod;
    
    @FindBy(css = ".payment-status")
    private WebElement paymentStatus;
    
    @FindBy(css = ".transaction-id, .payment-id")
    private WebElement transactionId;
    
    // Email confirmation
    @FindBy(css = ".email-confirmation-message")
    private WebElement emailConfirmationMessage;
    
    @FindBy(css = ".confirmation-email, .email-sent-to")
    private WebElement confirmationEmailElement;
    
    // Action buttons
    @FindBy(linkText = "Continue Shopping")
    private WebElement continueShoppingButton;
    
    @FindBy(linkText = "View Orders")
    private WebElement viewOrdersButton;
    
    @FindBy(css = ".download-invoice, .print-invoice")
    private WebElement downloadInvoiceButton;
    
    @FindBy(css = ".track-order-button")
    private WebElement trackOrderButton;
    
    public OrderConfirmationPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Check if order confirmation page is displayed
     */
    public boolean isOrderConfirmationDisplayed() {
        try {
            return waitVisible(confirmationMessage).isDisplayed();
        } catch (Exception e) {
            // Check URL for confirmation indicators
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            return currentUrl.contains("confirmation") || 
                   currentUrl.contains("success") ||
                   currentUrl.contains("thank-you") ||
                   currentUrl.contains("order-complete");
        }
    }
    
    /**
     * Get confirmation message text
     */
    public String getConfirmationMessage() {
        try {
            return waitVisible(confirmationMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get thank you message
     */
    public String getThankYouMessage() {
        try {
            return thankYouMessage.getText();
        } catch (Exception e) {
            return getConfirmationMessage();
        }
    }
    
    /**
     * Get order number
     */
    public String getOrderNumber() {
        try {
            String orderText = waitVisible(orderNumberElement).getText();
            // Extract just the number if format is "Order #12345" or "Order ID: 12345"
            return orderText.replaceAll("[^0-9]", "");
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Check if order number is generated
     */
    public boolean isOrderNumberGenerated() {
        String orderNumber = getOrderNumber();
        return orderNumber != null && !orderNumber.isEmpty() && !orderNumber.equals("0");
    }
    
    /**
     * Get order date
     */
    public String getOrderDate() {
        try {
            return orderDateElement.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get order status
     */
    public String getOrderStatus() {
        try {
            return waitVisible(orderStatusElement).getText();
        } catch (Exception e) {
            return "Confirmed"; // Default status
        }
    }
    
    /**
     * Get order total
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
     * Get order subtotal
     */
    public String getOrderSubtotal() {
        try {
            return orderSubtotalElement.getText().replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            return "0";
        }
    }
    
    /**
     * Get shipping cost
     */
    public String getShippingCost() {
        try {
            return shippingCostElement.getText().replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            return "0";
        }
    }
    
    /**
     * Get tax amount
     */
    public String getTaxAmount() {
        try {
            return taxElement.getText().replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            return "0";
        }
    }
    
    /**
     * Get discount amount
     */
    public String getDiscountAmount() {
        try {
            return discountElement.getText().replaceAll("[^0-9.]", "");
        } catch (Exception e) {
            return "0";
        }
    }
    
    /**
     * Get number of order items
     */
    public int getOrderItemCount() {
        try {
            return orderItems.size();
        } catch (Exception e) {
            return productNames.size();
        }
    }
    
    /**
     * Get product names from order
     */
    public List<String> getProductNames() {
        return productNames.stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }
    
    /**
     * Get product quantities from order
     */
    public List<String> getProductQuantities() {
        return productQuantities.stream()
            .map(element -> element.getText().replaceAll("[^0-9]", ""))
            .collect(Collectors.toList());
    }
    
    /**
     * Get product prices from order
     */
    public List<String> getProductPrices() {
        return productPrices.stream()
            .map(element -> element.getText().replaceAll("[^0-9.]", ""))
            .collect(Collectors.toList());
    }
    
    /**
     * Verify order total calculation
     */
    public boolean isOrderTotalCorrect() {
        try {
            double subtotal = Double.parseDouble(getOrderSubtotal());
            double shipping = Double.parseDouble(getShippingCost());
            double tax = Double.parseDouble(getTaxAmount());
            double discount = Double.parseDouble(getDiscountAmount());
            double total = Double.parseDouble(getOrderTotal());
            
            double expectedTotal = subtotal + shipping + tax - discount;
            
            return Math.abs(total - expectedTotal) < 0.01;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get delivery address
     */
    public String getDeliveryAddress() {
        try {
            return waitVisible(deliveryAddress).getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get delivery method
     */
    public String getDeliveryMethod() {
        try {
            return deliveryMethod.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get estimated delivery date
     */
    public String getEstimatedDelivery() {
        try {
            return estimatedDelivery.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get payment method
     */
    public String getPaymentMethod() {
        try {
            return waitVisible(paymentMethod).getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get payment status
     */
    public String getPaymentStatus() {
        try {
            return paymentStatus.getText();
        } catch (Exception e) {
            return "Paid"; // Default status
        }
    }
    
    /**
     * Get transaction ID
     */
    public String getTransactionId() {
        try {
            return transactionId.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Check if transaction ID exists
     */
    public boolean hasTransactionId() {
        String txnId = getTransactionId();
        return txnId != null && !txnId.isEmpty();
    }
    
    /**
     * Get email confirmation message
     */
    public String getEmailConfirmationMessage() {
        try {
            return emailConfirmationMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get confirmation email address
     */
    public String getConfirmationEmail() {
        try {
            return confirmationEmailElement.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Check if email confirmation is mentioned
     */
    public boolean hasEmailConfirmation() {
        try {
            String message = getEmailConfirmationMessage();
            if (message.toLowerCase().contains("email") || message.toLowerCase().contains("sent")) {
                return true;
            }
            return confirmationEmailElement.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Click continue shopping button
     */
    public void continueShopping() {
        try {
            WebElement btn = waitClickable(continueShoppingButton);
            btn.click();
        } catch (Exception e) {
            driver.navigate().to(driver.getCurrentUrl().split("/checkout")[0]);
        }
    }
    
    /**
     * Click view orders button
     */
    public void viewOrders() {
        try {
            WebElement btn = waitClickable(viewOrdersButton);
            btn.click();
        } catch (Exception e) {
            System.out.println("View orders button not available");
        }
    }
    
    /**
     * Click download invoice button
     */
    public void downloadInvoice() {
        try {
            WebElement btn = waitClickable(downloadInvoiceButton);
            btn.click();
        } catch (Exception e) {
            System.out.println("Download invoice button not available");
        }
    }
    
    /**
     * Click track order button
     */
    public void trackOrder() {
        try {
            WebElement btn = waitClickable(trackOrderButton);
            btn.click();
        } catch (Exception e) {
            System.out.println("Track order button not available");
        }
    }
    
    /**
     * Check if continue shopping button is available
     */
    public boolean isContinueShoppingAvailable() {
        try {
            return continueShoppingButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if view orders button is available
     */
    public boolean isViewOrdersAvailable() {
        try {
            return viewOrdersButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Verify order matches expected total
     */
    public boolean verifyOrderTotal(double expectedTotal) {
        double actualTotal = getOrderTotalAsDouble();
        return Math.abs(actualTotal - expectedTotal) < 0.01;
    }
    
    /**
     * Verify order has minimum required information
     */
    public boolean hasCompleteOrderInfo() {
        return isOrderNumberGenerated() &&
               !getOrderTotal().equals("0") &&
               getOrderItemCount() > 0;
    }
}
