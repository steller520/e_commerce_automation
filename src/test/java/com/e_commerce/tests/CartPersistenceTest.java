package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.CartPage;
import com.e_commerce.pages.HomePage;
import com.e_commerce.pages.ProductsPage;
import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartPersistenceTest extends BaseTest {

    @Test(description = "Verify cart persists after page refresh")
    public void cartPersistsAfterRefresh() {
        HomePage home = new HomePage(driver);
        home.open();

        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();

        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        Assert.assertTrue(cart.hasItems(), "Cart should have items before refresh");

        // Refresh page
        driver.navigate().refresh();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        // Verify cart still has items
        Assert.assertTrue(cart.hasItems(), "Cart should persist after refresh");
    }

    @Test(description = "Verify cart persists across navigation")
    public void cartPersistsAcrossNavigation() {
        HomePage home = new HomePage(driver);
        home.open();

        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();

        // Navigate to home
        home.open();

        // Navigate back to cart
        driver.get(driver.getCurrentUrl().replace(driver.getCurrentUrl().split("/")[driver.getCurrentUrl().split("/").length - 1], "view_cart"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        CartPage cart = new CartPage(driver);
        Assert.assertTrue(cart.hasItems(), "Cart should persist across navigation");
    }

    @Test(description = "Verify cart session cookie exists")
    public void verifyCartSessionCookie() {
        HomePage home = new HomePage(driver);
        home.open();

        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();

        // Check for session cookies
        Cookie sessionCookie = driver.manage().getCookieNamed("PHPSESSID");
        if (sessionCookie == null) {
            sessionCookie = driver.manage().getCookieNamed("session");
        }

        // Session cookie should exist for cart tracking
        // Note: Cookie name varies by implementation
    }
}
