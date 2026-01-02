package com.e_commerce.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(css = "a[href='/login']")
    private WebElement loginNavLink;

    @FindBy(css = ".login-form")
    private WebElement loginForm;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void openFromNavbar() {
        waitVisible(loginNavLink);
        try {
            waitClickable(loginNavLink);
            loginNavLink.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginNavLink);
        }
        waitVisible(loginForm);
    }

    public boolean isLoginFormVisible() {
        return loginForm != null && loginForm.isDisplayed();
    }
}
