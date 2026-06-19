package com.browserstack.advance_use_cases;

import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class BiometricAuthTest {

    private AndroidDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        // BrowserStack SDK (browserstack-media.yml) overrides this URL automatically
        driver = new AndroidDriver(
            new URL("http://127.0.0.1:4723/wd/hub"),
            capabilities
        );
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void testBiometricAuthentication() {
        // Click on biometric prompt button
        WebElement bioPromptButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                MobileBy.id("com.example.all_in_one:id/bio_prompt")
            )
        );
        bioPromptButton.click();

        // Click on prompt button
        WebElement promptButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                MobileBy.id("com.example.all_in_one:id/prompt")
            )
        );
        promptButton.click();

        // Simulate successful biometric authentication via BrowserStack executor
        // Wait briefly for the biometric prompt to appear before executing
        wait.until(ExpectedConditions.not(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                MobileBy.id("com.example.all_in_one:id/prompt")
            )
        ));
        driver.executeScript(
            "browserstack_executor: {\"action\":\"biometric\", \"arguments\": {\"biometricMatch\": \"pass\"}}"
        );
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}