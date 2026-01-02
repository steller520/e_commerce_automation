package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.CartPage;
import com.e_commerce.pages.HomePage;
import com.e_commerce.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckoutNavigationTest extends BaseTest {

    @Test(description = "Test back button during checkout preserves cart")
    public void backButtonPreservesCart() {
        HomePage home = new HomePage(driver);
        home.open();

        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        String productsUrl = driver.getCurrentUrl();

        products.addFirstProductToCart();

        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        Assert.assertTrue(cart.hasItems(), "Cart should have items");

        // Navigate back
        driver.navigate().back();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}

        // Navigate forward
        driver.navigate().forward();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}

        // Cart should still have items
        Assert.assertTrue(cart.hasItems(), "Cart should persist after back/forward navigation");
    }

    @Test(description = "Test refresh during checkout maintains state")
    public void refreshDuringCheckout() {
        HomePage home = new HomePage(driver);
        home.open();

        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();

        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        String cartUrl = driver.getCurrentUrl();

        // Refresh on cart page
        driver.navigate().refresh();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        Assert.assertEquals(driver.getCurrentUrl(), cartUrl, "URL should remain same after refresh");
        Assert.assertTrue(cart.hasItems(), "Cart items should persist after refresh");
    }
}
