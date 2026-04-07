package com.browserstack.test.suites.selfheal;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import io.appium.java_client.MobileBy;

public class BaseAppTest extends AppiumTest {

    /**
     * This test suite demonstrates standard Appium automation on the base app.
     * All locators are expected to match the original app UI. If the UI changes,
     * these tests may fail, showing the limitations of brittle locators.
     */

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