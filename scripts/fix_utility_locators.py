#!/usr/bin/env python3
"""Script to update Utility.java with additional imports and enhanced locators for Cancel Automatic Check-In button."""

import os

def update_utility_java():
    file_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), 
                             'src', 'main', 'java', 'appium', 'webdriver', 'extensions', 'Utility.java')
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Add AppiumBy import after AndroidDriver import
    if 'import io.appium.java_client.AppiumBy;' not in content:
        content = content.replace(
            'import io.appium.java_client.android.AndroidDriver;',
            'import io.appium.java_client.android.AndroidDriver;\nimport io.appium.java_client.AppiumBy;'
        )
        print('Added AppiumBy import')
    
    # Add List import after Arrays import
    if 'import java.util.List;' not in content:
        content = content.replace(
            'import java.util.Arrays;',
            'import java.util.Arrays;\nimport java.util.List;'
        )
        print('Added List import')
    
    # Update handlePostLoginNotifications method to use multiple locators
    old_method = '''    public static void handlePostLoginNotifications() {
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
    }'''
    
    new_method = '''    public static void handlePostLoginNotifications() {
        TestLogger.info("Handling post-login notifications (with wait)...");
        
        // Multiple locators for the Automatic Check-In Cancel button for better reliability
        By automaticCheckInCancelById = By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn");
        By automaticCheckInCancelByUiAutomator = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\\"com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn\\")");
        By automaticCheckInCancelByXpath = By.xpath("//android.widget.TextView[@resource-id=\\"com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn\\"]");
        
        List<By> automaticCheckInLocators = Arrays.asList(
            automaticCheckInCancelById,
            automaticCheckInCancelByUiAutomator,
            automaticCheckInCancelByXpath
        );
        
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

                // Check for Automatic Check-In dialog using multiple locators
                for (By locator : automaticCheckInLocators) {
                    if (isElementPresentQuick(locator, 1)) {
                        TestLogger.info("Found Automatic Check-In dialog with locator: " + locator.toString());
                        if (clickElementRobust(locator, "Automatic Check-In Cancel button")) {
                            found = true;
                            Thread.sleep(500);
                            // Verify the dialog is dismissed
                            if (!isElementPresentQuick(automaticCheckInCancelById, 1)) {
                                TestLogger.pass("Automatic Check-In dialog successfully dismissed");
                            } else {
                                TestLogger.warn("Dialog may still be visible, will retry...");
                            }
                            break;
                        }
                    }
                }
                
                if (found) {
                    continue;
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
    }'''
    
    if old_method in content:
        content = content.replace(old_method, new_method)
        print('Updated handlePostLoginNotifications method with multiple locators')
    else:
        print('WARNING: Could not find handlePostLoginNotifications method to update - may already be updated or format differs')
    
    # Update forceDismissDialogs method to include additional locators
    old_force_dismiss = '''    public static void forceDismissDialogs() {
        TestLogger.debug("Force dismissing any visible dialogs...");
        By[] dismissButtons = {
            By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn"),
            By.id("com.condecosoftware.condeco:id/core_dlg_negative_button"),
            By.id("com.condecosoftware.condeco:id/core_dlg_positive_button"),
            By.id("com.android.permissioncontroller:id/permission_allow_button"),
            By.id("android:id/button1"),
            By.id("android:id/button2")
        };'''
    
    new_force_dismiss = '''    public static void forceDismissDialogs() {
        TestLogger.debug("Force dismissing any visible dialogs...");
        By[] dismissButtons = {
            By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn"),
            AppiumBy.androidUIAutomator("new UiSelector().resourceId(\\"com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn\\")"),
            By.xpath("//android.widget.TextView[@resource-id=\\"com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn\\"]"),
            By.id("com.condecosoftware.condeco:id/core_dlg_negative_button"),
            By.id("com.condecosoftware.condeco:id/core_dlg_positive_button"),
            By.id("com.android.permissioncontroller:id/permission_allow_button"),
            By.id("android:id/button1"),
            By.id("android:id/button2")
        };'''
    
    if old_force_dismiss in content:
        content = content.replace(old_force_dismiss, new_force_dismiss)
        print('Updated forceDismissDialogs method with additional locators')
    else:
        print('WARNING: Could not find forceDismissDialogs method to update - may already be updated or format differs')
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print('Utility.java updated successfully!')

if __name__ == '__main__':
    update_utility_java()
