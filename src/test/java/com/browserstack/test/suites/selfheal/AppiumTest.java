package com.browserstack.test.suites.selfheal;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.MutableCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.appium.java_client.android.AndroidDriver;

/**
 * Base test class for self-heal demo tests.
 * Matches the GitHub reference AppiumTest.java pattern.
 * BrowserStack SDK handles session setup via browserstack.yml.
 */
public class AppiumTest {

    public AndroidDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        MutableCapabilities capabilities = new MutableCapabilities();
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}