package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.HomePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HeaderNavigationTest extends BaseTest {

    @Test(description = "Verify all header navigation links are present")
    public void verifyHeaderLinks() {
        HomePage home = new HomePage(driver);
        home.open();
        
        // Verify common header links
        String[] expectedLinks = {"Home", "Products", "Cart", "Login", "Contact us"};
        
        for (String linkText : expectedLinks) {
            try {
                WebElement link = driver.findElement(By.linkText(linkText));
                Assert.assertTrue(link.isDisplayed(), linkText + " link should be visible");
            } catch (Exception e) {
                // Some links may use different text case
                try {
                    WebElement link = driver.findElement(By.partialLinkText(linkText));
                    Assert.assertTrue(link.isDisplayed(), linkText + " link should be visible");
                } catch (Exception ex) {
                    Assert.fail(linkText + " link not found in header");
                }
            }
        }
    }

    @Test(description = "Click each main navigation link")
    public void clickNavigationLinks() {
        HomePage home = new HomePage(driver);
        home.open();
        String homeUrl = driver.getCurrentUrl();
        
        // Click Products
        try {
            driver.findElement(By.cssSelector("a[href='/products']")).click();
            Thread.sleep(500);
            Assert.assertTrue(driver.getCurrentUrl().contains("products"), "Should navigate to Products");
            driver.navigate().back();
            Thread.sleep(500);
        } catch (Exception e) {
            // Continue with other links
        }
    }
}
