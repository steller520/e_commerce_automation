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
}
