package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.HomePage;
import com.e_commerce.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductsSearchTest extends BaseTest {

    @Test(description = "Search products and verify results are displayed")
    public void searchProductsShowsResults() {
        HomePage home = new HomePage(driver);
        home.open();
        Assert.assertTrue(home.isLoaded(), "Home page should be loaded before navigating to Products");

        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.search("top");

        Assert.assertTrue(products.hasResults(), "Search should return at least one product result");
    }
}
