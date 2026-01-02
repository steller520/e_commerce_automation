package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.CartPage;
import com.e_commerce.pages.HomePage;
import com.e_commerce.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartRemoveItemTest extends BaseTest {

    @Test(description = "Remove item from cart and verify cart empties")
    public void removeItemFromCart() {
        HomePage home = new HomePage(driver);
        home.open();
        Assert.assertTrue(home.isLoaded());

        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();

        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        Assert.assertTrue(cart.hasItems(), "Cart should have items before removal");
        cart.removeFirstItem();
        // Allow brief time for removal
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        Assert.assertFalse(cart.hasItems(), "Cart should be empty after removing item");
    }
}
