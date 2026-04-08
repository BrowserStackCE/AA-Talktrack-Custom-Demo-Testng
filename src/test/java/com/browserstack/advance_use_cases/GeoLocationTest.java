package com.browserstack.advance_use_cases;

import com.browserstack.test.suites.TestBase;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Test
public class GeoLocationTest extends TestBase {

    @Test
    public void listAllOffersAfterLogin() throws InterruptedException {
        // Define platform-specific selectors for Offers menu item
        By offersMenuItem = mobileHelper.isAndroid() ? 
            MobileBy.xpath("//*[@text = 'Offers']") : 
            MobileBy.id("Offers");

        // Step 1: Open hamburger menu
        driver.findElement(MobileBy.AccessibilityId("menu")).click();
        
        // Step 2: Click on Sign In
        driver.findElement(MobileBy.AccessibilityId("nav-signin")).click();

        // Step 3: Login with first dropdown values for username
        driver.findElement(MobileBy.AccessibilityId("username-input")).click();
        mobileHelper.selectFromPickerWheel(
            "//XCUIElementTypePickerWheel[@value='Accepted usernames are']", 
            "fav_user"
        );

        // Step 4: Login with first dropdown values for password
        driver.findElement(MobileBy.AccessibilityId("password-input")).click();
        mobileHelper.selectFromPickerWheel(
            "//XCUIElementTypePickerWheel[@value='Password for all users']", 
            "testingisfun99"
        );

        // Step 5: Click login button
        driver.findElement(MobileBy.AccessibilityId("login-btn")).click();

        // Step 6: Open hamburger menu again
        driver.findElement(MobileBy.AccessibilityId("menu")).click();
        
        // Step 7: Click on Offers
        driver.findElement(offersMenuItem).click();
        
        // Step 8: Handle location permission if prompted
        Thread.sleep(2000); // Wait for permission dialog
        if (mobileHelper.isiOS()) {
            try {
                driver.findElement(By.id("Allow Once")).click();
            } catch (Exception e) {
                // Permission dialog may not appear
            }
        } else if (mobileHelper.isAndroid()) {
            try {
                driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button")).click();
            } catch (Exception e) {
                // Permission dialog may not appear
            }
        }

        // Step 9: Wait for offers to load
        Thread.sleep(3000);

        // Step 10: List all offers
        List<WebElement> offers = driver.findElements(MobileBy.AccessibilityId("offer"));
        
        System.out.println("=== OFFERS LIST ===");
        System.out.println("Total offers found: " + offers.size());
        
        for (int i = 0; i < offers.size(); i++) {
            WebElement offer = offers.get(i);
            System.out.println("Offer " + (i + 1) + ": " + offer.getText());
        }
        System.out.println("===================");

        // Verify that offers are displayed
        Assert.assertTrue(offers.size() > 0, "No offers found on the page");
    }
}