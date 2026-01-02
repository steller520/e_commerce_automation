package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.HomePage;
import com.e_commerce.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginNavigationTest extends BaseTest {

    @Test(description = "Navigate to Login page and verify form is visible")
    public void navigateToLoginShowsForm() {
        HomePage home = new HomePage(driver);
        home.open();
        Assert.assertTrue(home.isLoaded(), "Home page should be loaded");

        LoginPage login = new LoginPage(driver);
        login.openFromNavbar();
        Assert.assertTrue(login.isLoginFormVisible(), "Login form should be visible after navigation");
    }
}
