package com.e_commerce.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ProductsPage extends BasePage {

    @FindBy(css = "a[href='/products']")
    private WebElement navProductsLink;

    @FindBy(id = "search_product")
    private WebElement searchInput;

    @FindBy(id = "submit_search")
    private WebElement searchButton;

    @FindBy(css = ".features_items")
    private WebElement resultsContainer;

    @FindBy(css = ".features_items .product-image-wrapper")
    private List<WebElement> productCards;

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public void openFromNavbar() {
        waitVisible(navProductsLink);
        try {
            waitClickable(navProductsLink);
            navProductsLink.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navProductsLink);
        }
        waitVisible(resultsContainer);
    }

    public void search(String keyword) {
        WebElement input = waitVisible(searchInput);
        input.clear();
        input.sendKeys(keyword);
        waitClickable(searchButton);
        searchButton.click();
        waitVisible(resultsContainer);
    }

    public boolean hasResults() {
        return productCards != null && !productCards.isEmpty();
    }

    public void addFirstProductToCart() {
        // Click the first visible 'Add to cart' button with scroll + JS fallback
        WebElement addBtn = productCards.get(0).findElement(org.openqa.selenium.By.cssSelector("a.add-to-cart"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", addBtn);
        try {
            waitClickable(addBtn);
            addBtn.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", addBtn);
        }
    }
}
