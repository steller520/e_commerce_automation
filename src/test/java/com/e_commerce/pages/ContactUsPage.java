package com.e_commerce.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ContactUsPage extends BasePage {

    @FindBy(css = "a[href='/contact_us']")
    private WebElement contactUsNavLink;

    @FindBy(name = "name")
    private WebElement nameInput;

    @FindBy(name = "email")
    private WebElement emailInput;

    @FindBy(name = "subject")
    private WebElement subjectInput;

    @FindBy(name = "message")
    private WebElement messageTextarea;

    @FindBy(name = "submit")
    private WebElement submitButton;

    @FindBy(css = ".status.alert-success, .status")
    private WebElement statusAlert;

    public ContactUsPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        waitVisible(contactUsNavLink);
        try {
            waitClickable(contactUsNavLink);
            contactUsNavLink.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", contactUsNavLink);
        }
    }

    public void submitContactForm(String name, String email, String subject, String message) {
        waitVisible(nameInput).sendKeys(name);
        emailInput.sendKeys(email);
        subjectInput.sendKeys(subject);
        messageTextarea.sendKeys(message);
        waitClickable(submitButton);
        submitButton.click();
        // Handle JS alert that appears after submission
        try {
            driver.switchTo().alert().accept();
        } catch (Exception ignored) {}
        waitVisible(statusAlert);
    }
}
