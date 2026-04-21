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

public class ImageInjection {

    private AndroidDriver driver;
    private WebDriverWait wait;
    private WebDriverWait shortWait;

    @BeforeMethod
    public void setUp() throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        // BrowserStack SDK (browserstack-media.yml) overrides this URL and injects
        // enableCameraImageInjection: true automatically from the yml config
        driver = new AndroidDriver(
            new URL("http://127.0.0.1:4723/wd/hub"),
            capabilities
        );
        wait = new WebDriverWait(driver, 45);
        shortWait = new WebDriverWait(driver, 5);
    }

    @Test
    public void testCameraImageInjection() {
        // Click on Camera Intent button on the home screen
        WebElement cameraIntentButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                MobileBy.id("com.example.all_in_one:id/camintent")
            )
        );
        cameraIntentButton.click();

        // Click on the camera button to open the camera
        WebElement cameraButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                MobileBy.id("com.example.all_in_one:id/camera_button")
            )
        );
        cameraButton.click();

        // Allow "While using the app" camera permission if prompted
        try {
            WebElement allowForegroundButton = shortWait.until(
                ExpectedConditions.elementToBeClickable(
                    MobileBy.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button")
                )
            );
            allowForegroundButton.click();
        } catch (Exception e) {
            // Permission dialog may not appear if already granted
        }

        // Allow any additional permission prompts (e.g., storage, media)
        try {
            WebElement allowButton = shortWait.until(
                ExpectedConditions.elementToBeClickable(
                    MobileBy.id("com.android.permissioncontroller:id/permission_allow_button")
                )
            );
            allowButton.click();
        } catch (Exception e) {
            // Permission dialog may not appear if already granted
        }

        // Handle "Turn on Location tags?" dialog if it appears
        try {
            WebElement turnOnButton = shortWait.until(
                ExpectedConditions.elementToBeClickable(
                    MobileBy.xpath("//android.widget.Button[@text='Turn on']")
                )
            );
            turnOnButton.click();
        } catch (Exception e) {
            // Location tags dialog may not appear
        }

        // Wait for the camera viewfinder/shutter to be ready before injecting
        WebElement shutterButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                MobileBy.xpath("//*[@content-desc='Shutter' or @content-desc='Take photo' or contains(@resource-id,'shutter')]")
            )
        );

        // Inject the image AFTER camera is confirmed open and shutter is visible
        driver.executeScript(
            "browserstack_executor: {\"action\":\"cameraImageInjection\", \"arguments\": {\"imageUrl\": \"media://22892692ccf96ca859fbe0eaf9ecc410e5fa855e\"}}"
        );

        // Wait for injected image to appear in viewfinder, then tap shutter
        wait.until(ExpectedConditions.elementToBeClickable(
            MobileBy.xpath("//*[@content-desc='Shutter' or @content-desc='Take photo' or contains(@resource-id,'shutter')]")
        ));
        shutterButton.click();

        // Tap Done/Save to confirm the captured photo (Samsung Camera on Galaxy S23)
        WebElement doneButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                MobileBy.xpath("//*[@content-desc='Done' or @content-desc='Save' or @content-desc='OK' or @text='Done' or @text='Save' or @resource-id='com.sec.android.app.camera:id/okay' or @resource-id='com.sec.android.app.camera:id/done_button']")
            )
        );
        doneButton.click();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}