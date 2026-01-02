package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {

    @Test(description = "Verify home page is visible successfully")
    public void homePageLoadsSuccessfully() {
        HomePage homePage = new HomePage(driver);
        homePage.open();
        Assert.assertTrue(homePage.isLoaded(), "Home page should load and show expected elements");
    }
}
