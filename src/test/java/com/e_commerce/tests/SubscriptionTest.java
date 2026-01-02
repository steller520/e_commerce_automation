package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.HomePage;
import org.testng.annotations.Test;

public class SubscriptionTest extends BaseTest {

    @Test(description = "Subscribe to newsletter from footer")
    public void subscribeFooterNewsletter() {
        HomePage home = new HomePage(driver);
        home.open();
        home.subscribeNewsletter("test" + System.currentTimeMillis() + "@example.com");
    }
}
