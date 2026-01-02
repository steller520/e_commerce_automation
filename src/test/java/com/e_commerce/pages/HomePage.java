package com.e_commerce.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {
    private static final String BASE_URL = "https://automationexercise.com/";

    @FindBy(css = "div.header-middle")
    private WebElement headerLogo;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(BASE_URL);
    }

    public boolean isLoaded() {
        waitVisible(headerLogo);
        String title = driver.getTitle();
        return title != null && title.toLowerCase().contains("automation");
    }

    // Footer subscription
    @FindBy(id = "susbscribe_email")
    private WebElement subscribeEmailInput;

    @FindBy(id = "subscribe")
    private WebElement subscribeButton;

    @FindBy(css = ".footer-widget .alert-success, .footer-widget .alert")
    private WebElement subscriptionAlert;

    public void subscribeNewsletter(String email) {
        waitVisible(subscribeEmailInput).clear();
        subscribeEmailInput.sendKeys(email);
        waitClickable(subscribeButton);
        subscribeButton.click();
        waitVisible(subscriptionAlert);
    }
}
