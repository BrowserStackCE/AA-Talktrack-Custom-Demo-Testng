package com.browserstack.test.suites.selfheal;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.appium.java_client.MobileBy;

/**
 * This test suite demonstrates the self-healing feature of BrowserStack.
 * If the app UI changes and locators break, self-heal will attempt to recover
 * and find the correct elements, allowing tests to pass even with locator changes.
 * These tests are intentionally similar to BaseAppTest to show the difference
 * in behavior when self-heal is enabled.
 */
public class SelfHealAppTest extends AppiumTest {

    @Test
    public void shouldSelfHealOnProductCardXpathChange() {
        // Demonstrates self-heal for product card XPath change
        WebElement productsTab = driver.findElement(MobileBy.id("navigation_products"));
        productsTab.click();
        WebElement firstProductCard = new WebDriverWait(driver, 10)
            .until(ExpectedConditions.visibilityOfElementLocated(
                MobileBy.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id='com.example.selfhealingtestapp:id/recycler_products']/androidx.cardview.widget.CardView[1]/android.view.ViewGroup")));
        WebElement firstProductName = firstProductCard.findElement(MobileBy.id("product_name"));
        Assert.assertNotNull(firstProductName);
        WebElement infoIcon = firstProductCard.findElement(MobileBy.id("product_info_icon"));
        infoIcon.click();
        WebElement infoPanel = new WebDriverWait(driver, 5)
            .until(ExpectedConditions.visibilityOf(
                firstProductCard.findElement(MobileBy.id("info_card_product_panel"))));
        Assert.assertNotNull(infoPanel);
        Assert.assertNotNull(firstProductCard.findElement(MobileBy.id("info_card_product_id")));
        Assert.assertNotNull(firstProductCard.findElement(MobileBy.id("info_card_product_class")));
        Assert.assertNotNull(firstProductCard.findElement(MobileBy.id("info_card_product_xpath")));
        infoPanel.click();
    }

    @Test
    public void shouldSelfHealOnCheckboxIdChange() {
        // Demonstrates self-heal for checkbox ID change
        WebElement interactiveTab = driver.findElement(MobileBy.id("navigation_interactive"));
        interactiveTab.click();
        new WebDriverWait(driver, 10)
            .until(ExpectedConditions.visibilityOfElementLocated(MobileBy.id("demo_button_primary_action")));
        WebElement featureBtn = driver.findElement(MobileBy.id("demo_switch_example"));
        featureBtn.click();
        Assert.assertEquals(featureBtn.getAttribute("checked"), "true");
        WebElement acceptTermBtn = driver.findElement(MobileBy.id("demo_checkbox_example"));
        acceptTermBtn.click();
        Assert.assertEquals(acceptTermBtn.getAttribute("checked"), "true");
        acceptTermBtn.click();
        Assert.assertEquals(acceptTermBtn.getAttribute("checked"), "false");
    }

    @Test
    public void shouldSelfHealOnSwitchInfoIconContentDescChange() {
        // Demonstrates self-heal for info icon content-desc change
        WebElement interactiveTab = driver.findElement(MobileBy.id("navigation_interactive"));
        interactiveTab.click();
        new WebDriverWait(driver, 10)
            .until(ExpectedConditions.visibilityOfElementLocated(MobileBy.id("demo_button_primary_action")));
        WebElement infoIconSwitch = driver.findElement(MobileBy.xpath("//android.widget.ImageView[@content-desc='Switch Info Icon']"));
        infoIconSwitch.click();
    }
}