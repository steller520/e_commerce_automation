package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.CartPage;
import com.e_commerce.pages.HomePage;
import com.e_commerce.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AddProductToCartTest extends BaseTest {

    @Test(description = "Add first product to cart and verify items are present")
    public void addProductAndViewCart() {
        HomePage home = new HomePage(driver);
        home.open();
        Assert.assertTrue(home.isLoaded(), "Home should be loaded");

        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.addFirstProductToCart();

        CartPage cart = new CartPage(driver);
        cart.openFromModal();
        Assert.assertTrue(cart.hasItems(), "Cart should have at least one item");
    }
}
