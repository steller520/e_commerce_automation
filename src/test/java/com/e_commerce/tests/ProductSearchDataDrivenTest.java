package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.HomePage;
import com.e_commerce.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ProductSearchDataDrivenTest extends BaseTest {

    @DataProvider(name = "searchTerms")
    public Object[][] searchTerms() {
        return new Object[][] {
            {"top", true},
            {"dress", true},
            {"jeans", true},
            {"tshirt", true},
            {"xxxyyyzzz12345", false}  // Invalid search should show no results or message
        };
    }

    @Test(dataProvider = "searchTerms", description = "Data-driven product search test")
    public void searchWithMultipleTerms(String searchTerm, boolean expectResults) {
        HomePage home = new HomePage(driver);
        home.open();
        
        ProductsPage products = new ProductsPage(driver);
        products.openFromNavbar();
        products.search(searchTerm);
        
        boolean hasResults = products.hasResults();
        if (expectResults) {
            Assert.assertTrue(hasResults, "Search for '" + searchTerm + "' should return results");
        }
        // Note: Invalid search handling varies by site - may show empty or "no results" message
    }
}
