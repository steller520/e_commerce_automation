package com.e_commerce.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutPage extends BasePage {

    @FindBy(css = ".checkout-information")
    private WebElement checkoutSection;

    @FindBy(css = "input[name='coupon_code'], input#coupon")
    private WebElement couponInput;

    @FindBy(css = "button[name='apply_coupon'], button.btn-apply-coupon")
    private WebElement applyCouponButton;

    @FindBy(css = ".coupon-success, .alert-success")
    private WebElement couponSuccessMessage;

    @FindBy(css = ".coupon-error, .alert-danger")
    private WebElement couponErrorMessage;

    @FindBy(name = "proceed_to_checkout")
    private WebElement proceedButton;

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void applyCoupon(String couponCode) {
        try {
            waitVisible(couponInput).clear();
            couponInput.sendKeys(couponCode);
            waitClickable(applyCouponButton);
            applyCouponButton.click();
            Thread.sleep(1000); // Wait for coupon processing
        } catch (Exception e) {
            // Coupon field may not exist on all pages
        }
    }

    public boolean isCouponAppliedSuccessfully() {
        try {
            return waitVisible(couponSuccessMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCouponErrorDisplayed() {
        try {
            return couponErrorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void proceedToNextStep() {
        try {
            waitClickable(proceedButton);
            proceedButton.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", proceedButton);
        }
    }
}
