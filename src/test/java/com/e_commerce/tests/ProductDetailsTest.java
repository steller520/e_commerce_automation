package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.HomePage;
import com.e_commerce.pages.ProductsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class ProductDetailsTest extends BaseTest {

    @Test(description = "View product details and verify information is displayed")
    public void viewProductDetails() {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        
        // Click "View Product" on first item
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement viewProductLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("a[href^='/product_details/']")));
        viewProductLink.click();
        
        // Verify product details page
        wait.until(ExpectedConditions.urlContains("product_details"));
        Assert.assertTrue(driver.getCurrentUrl().contains("product_details"), 
            "Should navigate to product details page");
    }
}
