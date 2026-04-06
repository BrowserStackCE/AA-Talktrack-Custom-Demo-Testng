package com.browserstack.test.suites.selfheal;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.appium.java_client.MobileBy;

public class BaseAppTest extends AppiumTest {

    /**
     * This test suite demonstrates standard Appium automation on the base app.
     * All locators are expected to match the original app UI. If the UI changes,
     * these tests may fail, showing the limitations of brittle locators.
     */

    @Test
    public void shouldSelfHealOnProductCardXpathChange() {
        // Verifies product card and info panel using original XPath (no UI change)
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
        // Verifies switch and checkbox using original IDs (no UI change)
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
        // Verifies info icon using original content-desc (no UI change)
        WebElement interactiveTab = driver.findElement(MobileBy.id("navigation_interactive"));
        interactiveTab.click();
        new WebDriverWait(driver, 10)
            .until(ExpectedConditions.visibilityOfElementLocated(MobileBy.id("demo_button_primary_action")));
        WebElement infoIconSwitch = driver.findElement(MobileBy.xpath("//android.widget.ImageView[@content-desc='Switch Info Icon']"));
        infoIconSwitch.click();
    }
}