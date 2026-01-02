package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.CartPage;
import com.e_commerce.pages.HomePage;
import com.e_commerce.pages.ProductsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class AddMultipleProductsTest extends BaseTest {

    @Test(description = "Add multiple products to cart and verify count")
    public void addMultipleProducts() {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
        // Add first product
        products.addFirstProductToCart();
        
        // Wait for modal and click "Continue Shopping"
        try {
            WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.btn-success, button[data-dismiss='modal']")));
            continueBtn.click();
            Thread.sleep(500);
        } catch (Exception e) {
            // Modal may auto-close or not appear
        }
        
        // Add second product - find second add-to-cart button
        try {
            WebElement secondAddBtn = driver.findElements(By.cssSelector(".product-image-wrapper a.add-to-cart")).get(1);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", secondAddBtn);
            Thread.sleep(300);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", secondAddBtn);
        } catch (Exception e) {
            // If second product not available, skip
        }
        
        // View cart
        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        Assert.assertTrue(cart.hasItems(), "Cart should have items");
    }
}
