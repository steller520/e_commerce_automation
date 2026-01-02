package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.pages.ContactUsPage;
import com.e_commerce.pages.HomePage;
import org.testng.annotations.Test;

public class ContactUsTest extends BaseTest {

    @Test(description = "Submit contact form and verify success alert")
    public void submitContactForm() {
        HomePage home = new HomePage(driver);
        home.open();

        ContactUsPage contact = new ContactUsPage(driver);
        contact.open();
        contact.submitContactForm("Test User", "user" + System.currentTimeMillis() + "@example.com", "Support", "This is a test message.");
    }
}
