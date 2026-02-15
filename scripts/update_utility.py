#!/usr/bin/env python3
"""Script to update Utility.java with robust click handling"""

import os

UTILITY_CONTENT = '''package appium.webdriver.extensions;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import appium.webdriver.logging.TestLogger;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.Point;
import org.openqa.selenium.Dimension;
import java.io.InputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class Utility {
    private static Properties properties;
    static {
        try {
            InputStream inputStream = Utility.class.getClassLoader().getResourceAsStream("Config.properties");
            if (inputStream == null) {
                inputStream = Utility.class.getClassLoader().getResourceAsStream("config.properties");
            }
            if (inputStream == null) {
                throw new RuntimeException("Could not find Config.properties in classpath. Ensure the file exists in src/main/resources/");
            }
            properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            TestLogger.pass("Config.properties loaded successfully from classpath");
        } catch (IOException e) {
            TestLogger.error("Could not load config.properties file", e);
            throw new RuntimeException("Could not load config.properties file: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static WebElement waitForElementUntilPresent(By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static void clickElement(By locator, int timeoutInSeconds) {
        TestLogger.element("Click", locator.toString());
        WebElement element = waitForElementUntilPresent(locator, timeoutInSeconds);
        element.click();
    }

    public static void sendKeys(By locator, String text, int timeoutInSeconds) {
        TestLogger.element("SendKeys", locator.toString());
        WebElement element = waitForElementUntilPresent(locator, timeoutInSeconds);
        element.clear();
        element.sendKeys(text);
    }

    public static String getTextFromid(By locator, int timeoutInSeconds) {
        WebElement element = waitForElementUntilPresent(locator, timeoutInSeconds);
        String value = element.getText();
        TestLogger.debug("Got text '" + value + "' from element: " + locator.toString());
        return value;
    }

    public static boolean isElementPresent(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    public static void handleAppNotifications() {
        TestLogger.debug("Checking for app notifications...");
        By appNotificationAllowButton = By.id("com.android.permissioncontroller:id/permission_allow_button");
        By automaticCheckInCancelButton = By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn");
        By dialogDismissButton = By.id("com.condecosoftware.condeco:id/core_dlg_negative_button");

        for (int i = 0; i < 3; i++) {
            boolean foundAnyNotification = false;
            try {
                if (isElementPresentQuick(appNotificationAllowButton, 1)) {
                    clickElementRobust(appNotificationAllowButton, "permission allow button");
                    foundAnyNotification = true;
                    Thread.sleep(500);
                    continue;
                }
                if (isElementPresentQuick(automaticCheckInCancelButton, 1)) {
                    clickElementRobust(automaticCheckInCancelButton, "Automatic Check-In Cancel button");
                    foundAnyNotification = true;
                    Thread.sleep(500);
                    continue;
                }
                if (isElementPresentQuick(dialogDismissButton, 1)) {
                    clickElementRobust(dialogDismissButton, "dialog dismiss button");
                    foundAnyNotification = true;
                    Thread.sleep(500);
                    continue;
                }
                if (!foundAnyNotification) {
                    break;
                }
            } catch (Exception e) {
                TestLogger.debug("Notification check " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    public static boolean isElementPresentQuick(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void clickElementQuick(By locator) {
        try {
            DriverManager.getDriver().findElement(locator).click();
        } catch (Exception e) {
            TestLogger.debug("Quick click failed: " + e.getMessage());
        }
    }

    /**
     * Robust click method that tries multiple strategies to click an element.
     * Returns true if click was successful, false otherwise.
     * 
     * Strategies:
     * 1. Wait for element to be clickable, then click
     * 2. Find element and click directly
     * 3. Tap by coordinates using element's center
     * 4. Use mobile tap gesture on element
     */
    public static boolean clickElementRobust(By locator, String elementName) {
        AndroidDriver driver = DriverManager.getDriver();
        try {
            // Strategy 1: Wait for element to be clickable
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                TestLogger.pass("Clicked " + elementName + " (Strategy 1: clickable wait)");
                return true;
            } catch (Exception e) {
                TestLogger.debug("Strategy 1 failed for " + elementName + ": " + e.getMessage());
            }
            
            // Strategy 2: Direct click
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed() && element.isEnabled()) {
                    element.click();
                    TestLogger.pass("Clicked " + elementName + " (Strategy 2: direct click)");
                    return true;
                }
            } catch (Exception e) {
                TestLogger.debug("Strategy 2 failed for " + elementName + ": " + e.getMessage());
            }
            
            // Strategy 3: Tap by coordinates
            try {
                WebElement element = driver.findElement(locator);
                Point location = element.getLocation();
                Dimension size = element.getSize();
                int centerX = location.getX() + size.getWidth() / 2;
                int centerY = location.getY() + size.getHeight() / 2;
                TestLogger.debug("Tapping at coordinates: (" + centerX + ", " + centerY + ")");
                tapAtCoordinates(centerX, centerY);
                TestLogger.pass("Clicked " + elementName + " (Strategy 3: tap by coordinates)");
                return true;
            } catch (Exception e) {
                TestLogger.debug("Strategy 3 failed for " + elementName + ": " + e.getMessage());
            }
            
            // Strategy 4: Mobile tap gesture
            try {
                WebElement element = driver.findElement(locator);
                tapOnElement(element);
                TestLogger.pass("Clicked " + elementName + " (Strategy 4: mobile tap gesture)");
                return true;
            } catch (Exception e) {
                TestLogger.debug("Strategy 4 failed for " + elementName + ": " + e.getMessage());
            }
            
            TestLogger.warn("All click strategies failed for " + elementName);
            return false;
        } catch (Exception e) {
            TestLogger.error("Error in clickElementRobust for " + elementName + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Tap at specific screen coordinates using W3C Actions
     */
    public static void tapAtCoordinates(int x, int y) {
        AndroidDriver driver = DriverManager.getDriver();
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(tap));
    }

    /**
     * Tap on a WebElement using W3C Actions
     */
    public static void tapOnElement(WebElement element) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        int centerX = location.getX() + size.getWidth() / 2;
        int centerY = location.getY() + size.getHeight() / 2;
        tapAtCoordinates(centerX, centerY);
    }

    /**
     * Handles notifications with extended wait - use this right after login
     * when you expect the Automatic Check-In dialog to appear.
     * Uses multiple click strategies for robustness.
     */
    public static void handlePostLoginNotifications() {
        TestLogger.info("Handling post-login notifications (with wait)...");
        By automaticCheckInCancelButton = By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn");
        By appNotificationAllowButton = By.id("com.android.permissioncontroller:id/permission_allow_button");
        By cancelButtonByText = By.xpath("//*[@text='Cancel' or @text='CANCEL' or @content-desc='Cancel']");
        
        // Wait for dialogs to appear after login
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Check multiple times with short intervals - total ~15 seconds of checking
        for (int i = 0; i < 15; i++) {
            boolean found = false;
            try {
                // Check for permission dialog first
                if (isElementPresentQuick(appNotificationAllowButton, 1)) {
                    TestLogger.info("Found permission dialog, clicking Allow...");
                    if (clickElementRobust(appNotificationAllowButton, "permission allow button")) {
                        found = true;
                        Thread.sleep(500);
                        continue;
                    }
                }
                
                // Check for Automatic Check-In dialog - this is the main one
                if (isElementPresentQuick(automaticCheckInCancelButton, 1)) {
                    TestLogger.info("Found Automatic Check-In dialog, attempting to click Cancel...");
                    if (clickElementRobust(automaticCheckInCancelButton, "Automatic Check-In Cancel button")) {
                        found = true;
                        Thread.sleep(500);
                        // Verify the dialog is dismissed
                        if (!isElementPresentQuick(automaticCheckInCancelButton, 1)) {
                            TestLogger.pass("Automatic Check-In dialog successfully dismissed");
                        } else {
                            TestLogger.warn("Dialog may still be visible, will retry...");
                        }
                        continue;
                    }
                }
                
                // Try alternative locator - Cancel button by text
                if (isElementPresentQuick(cancelButtonByText, 1)) {
                    TestLogger.info("Found Cancel button by text, attempting click...");
                    if (clickElementRobust(cancelButtonByText, "Cancel button (by text)")) {
                        found = true;
                        Thread.sleep(500);
                        continue;
                    }
                }
                
                // If nothing found and we've checked enough times, stop
                if (!found && i >= 5) {
                    TestLogger.debug("No notifications found after " + (i + 1) + " checks, stopping");
                    break;
                }
                
                // Small wait before next check
                if (!found) {
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                TestLogger.debug("Notification check " + (i + 1) + " error: " + e.getMessage());
            }
        }
        TestLogger.info("Post-login notification handling completed");
    }

    /**
     * Force dismiss any visible dialogs - use as a fallback.
     * Uses robust click method with multiple strategies.
     */
    public static void forceDismissDialogs() {
        TestLogger.debug("Force dismissing any visible dialogs...");
        By[] dismissButtons = {
            By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn"),
            By.id("com.condecosoftware.condeco:id/core_dlg_negative_button"),
            By.id("com.condecosoftware.condeco:id/core_dlg_positive_button"),
            By.id("com.android.permissioncontroller:id/permission_allow_button"),
            By.id("android:id/button1"),
            By.id("android:id/button2")
        };
        for (By locator : dismissButtons) {
            try {
                WebElement element = DriverManager.getDriver().findElement(locator);
                if (element.isDisplayed() && element.isEnabled()) {
                    clickElementRobust(locator, locator.toString());
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            } catch (Exception e) {
                // Element not found, continue to next
            }
        }
    }
}
'''

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_dir = os.path.dirname(script_dir)
    utility_path = os.path.join(project_dir, 'src', 'main', 'java', 'appium', 'webdriver', 'extensions', 'Utility.java')
    
    print(f"Updating: {utility_path}")
    
    with open(utility_path, 'w', encoding='utf-8') as f:
        f.write(UTILITY_CONTENT)
    
    print("Utility.java updated successfully!")

if __name__ == '__main__':
    main()
