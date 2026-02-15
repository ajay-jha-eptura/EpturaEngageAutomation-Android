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
    
    # Update handlePostLoginNotifications method - replace the single locator with multiple locators
    old_locator_line = 'By automaticCheckInCancelButton = By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn");'
    
    new_locator_lines = '''// Multiple locators for the Automatic Check-In Cancel button for better reliability
        By automaticCheckInCancelById = By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn");
        By automaticCheckInCancelByUiAutomator = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\\"com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn\\")");
        By automaticCheckInCancelByXpath = By.xpath("//android.widget.TextView[@resource-id=\\"com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn\\"]");
        By automaticCheckInCancelButton = automaticCheckInCancelById;'''
    
    if old_locator_line in content:
        content = content.replace(old_locator_line, new_locator_lines)
        print('Updated locator declarations in handlePostLoginNotifications')
    else:
        print('WARNING: Could not find locator line to update in handlePostLoginNotifications')
    
    # Update forceDismissDialogs method to include additional locators
    old_force_dismiss = '''By[] dismissButtons = {
            By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn"),'''
    
    new_force_dismiss = '''By[] dismissButtons = {
            By.id("com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn"),
            AppiumBy.androidUIAutomator("new UiSelector().resourceId(\\"com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn\\")"),
            By.xpath("//android.widget.TextView[@resource-id=\\"com.condecosoftware.condeco:id/buttonCancelAutomaticCheckIn\\"]"),'''
    
    if old_force_dismiss in content:
        content = content.replace(old_force_dismiss, new_force_dismiss)
        print('Updated forceDismissDialogs method with additional locators')
    else:
        print('WARNING: Could not find forceDismissDialogs method to update')
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print('Utility.java updated successfully!')

if __name__ == '__main__':
    update_utility_java()