package com.e_commerce.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CartPage extends BasePage {

    @FindBy(css = "div.cart_info")
    private WebElement cartTable;

    @FindBy(css = "div.cart_info table tbody tr")
    private List<WebElement> cartItems;

    @FindBy(css = "a[href='/view_cart']")
    private WebElement viewCartLinkInModal;

    @FindBy(css = "a.btn.btn-default.check_out")
    private WebElement proceedToCheckoutButton;

    private final By deleteItemButton = By.cssSelector("a.cart_quantity_delete");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public void openFromModal() {
        waitVisible(viewCartLinkInModal);
        viewCartLinkInModal.click();
        waitVisible(cartTable);
    }

    public boolean hasItems() {
        waitVisible(cartTable);
        return cartItems != null && !cartItems.isEmpty();
    }

    public void removeFirstItem() {
        WebElement firstDelete = driver.findElement(deleteItemButton);
        waitClickable(firstDelete);
        firstDelete.click();
    }

    public void proceedToCheckout() {
        waitClickable(proceedToCheckoutButton);
        proceedToCheckoutButton.click();
    }

    public int getCartItemCount() {
        waitVisible(cartTable);
        return cartItems.size();
    }
}
