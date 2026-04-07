package com.browserstack.test.suites.selfheal;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import io.appium.java_client.MobileBy;

/**
 * This test suite demonstrates the self-healing feature of BrowserStack.
 * If the app UI changes and locators break, self-heal will attempt to recover
 * and find the correct elements, allowing tests to pass even with locator changes.
 * This test is intentionally identical to BaseAppTest to show the difference
 * in behavior when self-heal is enabled.
 */
public class SelfHealAppTest extends AppiumTest {

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