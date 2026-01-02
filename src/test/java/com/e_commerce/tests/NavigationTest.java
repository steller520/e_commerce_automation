package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.HomePage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NavigationTest extends BaseTest {

    @Test(description = "Test browser back and forward navigation")
    public void backForwardNavigation() {
        HomePage home = new HomePage(driver);
        home.open();
        String homeUrl = driver.getCurrentUrl();
        
        // Navigate to products
        driver.findElement(org.openqa.selenium.By.cssSelector("a[href='/products']")).click();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        String productsUrl = driver.getCurrentUrl();
        Assert.assertTrue(productsUrl.contains("products"), "Should be on products page");
        
        // Go back
        driver.navigate().back();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        Assert.assertEquals(driver.getCurrentUrl(), homeUrl, "Should be back on home page");
        
        // Go forward
        driver.navigate().forward();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        Assert.assertTrue(driver.getCurrentUrl().contains("products"), "Should be on products page again");
    }

    @Test(description = "Test page refresh maintains state")
    public void pageRefresh() {
        HomePage home = new HomePage(driver);
        home.open();
        String originalUrl = driver.getCurrentUrl();
        
        driver.navigate().refresh();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        
        Assert.assertEquals(driver.getCurrentUrl(), originalUrl, "URL should remain the same after refresh");
        Assert.assertTrue(home.isLoaded(), "Home page should still be loaded after refresh");
    }
}
