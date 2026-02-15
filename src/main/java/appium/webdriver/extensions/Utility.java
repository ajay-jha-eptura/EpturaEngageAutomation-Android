package appium.webdriver.extensions;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumBy;

import java.io.InputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
public class Utility {
    private static Properties properties;
    static {
        try {
            // Use classpath-based resource loading instead of file path
            // This works both locally and in CI/CD pipeline
            InputStream inputStream = Utility.class.getClassLoader().getResourceAsStream("Config.properties");
            if (inputStream == null) {
                // Fallback: try alternative filename (lowercase)
                inputStream = Utility.class.getClassLoader().getResourceAsStream("config.properties");
            }
            if (inputStream == null) {
                throw new RuntimeException("Could not find Config.properties in classpath. Ensure the file exists in src/main/resources/");
            }
            properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            System.out.println("Config.properties loaded successfully from classpath");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load config.properties file: " + e.getMessage());
        }
    }
    
    /**
     * Gets a property value with support for system property and environment variable overrides.
     * Priority: System Property > Environment Variable > Config.properties
     * This allows CI/CD pipelines to override values without modifying config files.
     */
    public static String getProperty(String key) {
        // First check system properties (set via -D flag in Maven)
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty() && !value.equals("${" + key + "}")) {
            System.out.println("Using system property for " + key + ": " + value);
            return value;
        }
        
        // Then check environment variables (convert key to uppercase with underscores)
        String envKey = key.toUpperCase().replace(".", "_");
        value = System.getenv(envKey);
        if (value != null && !value.isEmpty()) {
            System.out.println("Using environment variable for " + key + ": " + value);
            return value;
        }
        
        // Finally fall back to config.properties
        return properties.getProperty(key);
    }
    public static WebElement waitForElementUntilPresent(By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    public static void clickElement(By locator, int timeoutInSeconds) {
        WebElement element = waitForElementUntilPresent(locator, timeoutInSeconds);
        element.click();
    }
    public static void sendKeys(By locator, String text, int timeoutInSeconds) {
        WebElement element = waitForElementUntilPresent(locator, timeoutInSeconds);
        element.clear();
        element.sendKeys(text);
    }
    public static String getTextFromid(By locator, int timeoutInSeconds) {
        WebElement element = waitForElementUntilPresent(locator, timeoutInSeconds);
        String value = element.getText();
        return value;
    }
    /**
     * Checks if an element is present on the page within the specified timeout.
     */
    public static boolean isElementPresent(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }
    /**
     * Handles and dismisses app-specific and system-level notifications.
     * This method will try to click the buttons for both notifications in a loop.
     */
    public static void handleAppNotifications() {
        System.out.println("Checking for and handling app notifications...");
        // Define the locators for the notifications
        By appNotificationAllowButton = By.id("com.android.permissioncontroller:id/permission_allow_button");
        // Multiple locators for the Automatic Check-In Cancel button for better reliability
        By automaticCheckInCancelById = By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn");
        By automaticCheckInCancelByUiAutomator = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn\")");
        By automaticCheckInCancelByXpath = By.xpath("//android.widget.TextView[@resource-id=\"com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn\"]");
        By automaticCheckInCancelButton = automaticCheckInCancelById;

        // Loop a few times to handle potential race conditions
        for (int i = 0; i < 3; i++) {
            try {
                // Check for system notification
                if (isElementPresent(appNotificationAllowButton, 10)) {
                    clickElement(appNotificationAllowButton, 5);
                    System.out.println("Allowed App to send notifications");
                }

                // Check for app-specific notification
                if (isElementPresent(automaticCheckInCancelButton, 20)) {
                    clickElement(automaticCheckInCancelButton, 5);
                    System.out.println("Automatic Check-In notification dismissed.");
                }

                // If neither notification is present, we can break the loop
                if (!isElementPresent(appNotificationAllowButton, 1) && !isElementPresent(automaticCheckInCancelButton, 1)) {
                    break;
                }

            } catch (Exception e) {
                // Catch any exception and continue the loop
                System.out.println("Notification handling attempt failed: " + e.getMessage());
            }
        }
    }
    
    /**
     * Handles post-login notifications including permission dialogs and app-specific popups.
     * This method should be called after successful login to dismiss any dialogs that appear.
     */
    public static void handlePostLoginNotifications() {
        System.out.println("Handling post-login notifications...");
        
        // Define locators for common post-login dialogs
        By appNotificationAllowButton = By.id("com.android.permissioncontroller:id/permission_allow_button");
        By automaticCheckInCancelButton = By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn");
        By dontAllowButton = By.id("com.android.permissioncontroller:id/permission_deny_button");
        By okButton = By.id("android:id/button1");
        By cancelButton = By.id("android:id/button2");
        
        // Loop multiple times to handle multiple dialogs that may appear sequentially
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
                
                // Handle permission allow button
                if (isElementPresent(appNotificationAllowButton, 30)) {
                    clickElement(appNotificationAllowButton, 3);
                    System.out.println("Clicked 'Allow' on permission dialog");
                    continue;
                }
                
                // Handle Automatic Check-In dialog
                if (isElementPresent(automaticCheckInCancelButton, 30)) {
                    clickElement(automaticCheckInCancelButton, 3);
                    System.out.println("Dismissed Automatic Check-In dialog");
                    continue;
                }
                
                // Handle generic OK button
                if (isElementPresent(okButton, 2)) {
                    clickElement(okButton, 2);
                    System.out.println("Clicked OK button on dialog");
                    continue;
                }
                
                // If no dialogs found, break the loop
                break;
                
            } catch (Exception e) {
                System.out.println("Post-login notification handling attempt " + (i + 1) + " completed: " + e.getMessage());
            }
        }
        
        System.out.println("Post-login notification handling completed");
    }
    
    /**
     * Force dismisses any remaining dialogs on screen by trying various button locators.
     * This is a fallback method to ensure no dialogs block the UI.
     */
    public static void forceDismissDialogs() {
        System.out.println("Force dismissing any remaining dialogs...");
        
        // List of common dialog button locators
        By[] dialogButtons = {
            By.id("android:id/button1"),  // OK/Positive button
            By.id("android:id/button2"),  // Cancel/Negative button
            By.id("android:id/button3"),  // Neutral button
            By.id("com.android.permissioncontroller:id/permission_allow_button"),
            By.id("com.android.permissioncontroller:id/permission_deny_button"),
            By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn"),
            By.id("com.condecosoftware.condeco:id/core_dlg_positive_button"),
            By.id("com.condecosoftware.condeco:id/core_dlg_negative_button")
        };
        
        for (By buttonLocator : dialogButtons) {
            try {
                if (isElementPresent(buttonLocator, 1)) {
                    clickElement(buttonLocator, 1);
                    System.out.println("Dismissed dialog using: " + buttonLocator.toString());
                    // Wait a bit after dismissing a dialog
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                // Ignore and continue to next button
            }
        }
        
        System.out.println("Force dismiss dialogs completed");
    }
}