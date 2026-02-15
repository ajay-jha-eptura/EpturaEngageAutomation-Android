package com.client.app.pages;

import appium.webdriver.extensions.DriverManager;
import appium.webdriver.extensions.Utility;
import appium.webdriver.logging.TestLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class LoginPage extends DriverManager {
   // Locators
   private final By EpturaURL = By.id("com.condecosoftware.condeco:id/editTextServerUrl");
   private final By UserCredentials_Screen = By.id("com.condecosoftware.condeco:id/title");
   private final By Continue_btn = By.id("com.condecosoftware.condeco:id/buttonContinue");
   
   // Multiple locators for Username field
   private final By Username_by_id = By.id("com.condecosoftware.condeco:id/username");
   private final By Username_by_uiautomator = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.condecosoftware.condeco:id/username\")");
   private final By Username_by_xpath = By.xpath("//android.widget.EditText[@resource-id=\"com.condecosoftware.condeco:id/username\"]");
   
   // Multiple locators for Password field
   private final By Password_by_id = By.id("com.condecosoftware.condeco:id/password");
   private final By Password_by_uiautomator = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.condecosoftware.condeco:id/password\")");
   private final By Password_by_xpath = By.xpath("//android.widget.EditText[@resource-id=\"com.condecosoftware.condeco:id/password\"]");
   
   // Default locators
   private final By Username_id = By.id("com.condecosoftware.condeco:id/username");
   private final By Password_id = By.id("com.condecosoftware.condeco:id/password");

   // Home screen locators
   private final By Todaypage_Header = By.xpath("(//android.widget.TextView[@text=\"Today\"])[1]");
   private final By CalendarPage_Header = By.xpath("(//android.widget.TextView[@text=\"Calendar\"])[1]");
   private final By BookPage_Header = By.xpath("(//android.widget.TextView[@text=\"Book\"])[1]");
   private final By YourTeamPage_Header = By.xpath("(//android.widget.TextView[@text=\"Your team\"])[1]");
   private final By textInputErrorOnLogin_id = By.id("com.condecosoftware.condeco:id/textinput_error");
   private final By Profile_menu_btn = By.xpath("//android.widget.LinearLayout[@content-desc=\"Profile\"]/android.widget.ImageView");
   private final By logout_option = By.id("com.condecosoftware.condeco:id/logout");
   private final By logout_confirm = By.id("com.condecosoftware.condeco:id/core_dlg_positive_button");
   
   // ANR dialog locators
   private final By ANR_Dialog_Title = By.id("android:id/alertTitle");
   private final By ANR_Wait_Button = By.id("android:id/aerr_wait");
   private final By ANR_Close_Button = By.id("android:id/aerr_close");
   
   /**
    * Helper method to find and return a working username element by trying multiple locators
    * @return WebElement if found, null if none of the locators work
    */
   private WebElement findUsernameField() {
       List<By> locators = Arrays.asList(
           Username_by_id,
           Username_by_uiautomator,
           Username_by_xpath
       );
       
       TestLogger.debug("Trying to find username field with multiple locators...");
       
       for (int i = 0; i < locators.size(); i++) {
           By locator = locators.get(i);
           try {
               TestLogger.debug("Attempt " + (i + 1) + "/" + locators.size() + ": " + locator.toString());
               WebElement element = driver.findElement(locator);
               if (element != null && element.isDisplayed()) {
                   TestLogger.pass("Found username field with: " + locator.toString());
                   return element;
               }
           } catch (Exception e) {
               TestLogger.debug("Locator failed: " + e.getMessage().split("\n")[0]);
           }
       }
       
       TestLogger.fail("All username locators failed!");
       return null;
   }
   
   /**
    * Helper method to find and return a working password element by trying multiple locators
    * @return WebElement if found, null if none of the locators work
    */
   private WebElement findPasswordField() {
       List<By> locators = Arrays.asList(
           Password_by_id,
           Password_by_uiautomator,
           Password_by_xpath
       );
       
       TestLogger.debug("Trying to find password field with multiple locators...");
       
       for (int i = 0; i < locators.size(); i++) {
           By locator = locators.get(i);
           try {
               TestLogger.debug("Attempt " + (i + 1) + "/" + locators.size() + ": " + locator.toString());
               WebElement element = driver.findElement(locator);
               if (element != null && element.isDisplayed()) {
                   TestLogger.pass("Found password field with: " + locator.toString());
                   return element;
               }
           } catch (Exception e) {
               TestLogger.debug("Locator failed: " + e.getMessage().split("\n")[0]);
           }
       }
       
       TestLogger.fail("All password locators failed!");
       return null;
   }
   
   /**
    * Clears the credential fields (username and password) to ensure fresh entry
    * This is important when running multiple login scenarios back-to-back
    */
   private void clearCredentialFields() {
       TestLogger.info("Clearing credential fields before entry...");
       try {
           // Clear username field
           WebElement usernameElement = findUsernameField();
           if (usernameElement != null) {
               String currentText = usernameElement.getAttribute("text");
               String showingHint = usernameElement.getAttribute("showingHintText");
               TestLogger.debug("Username field - current text: '" + currentText + "', showing-hint: " + showingHint);
               
               if (!"true".equals(showingHint) && currentText != null && !currentText.equals("Username") && !currentText.isEmpty()) {
                   TestLogger.debug("Clearing username field...");
                   usernameElement.click();
                   Thread.sleep(300);
                   usernameElement.clear();
                   Thread.sleep(300);
                   
                   // Verify it's cleared - use backspace as fallback
                   String afterClear = usernameElement.getAttribute("text");
                   if (afterClear != null && !afterClear.equals("Username") && !afterClear.isEmpty() && !"true".equals(usernameElement.getAttribute("showingHintText"))) {
                       TestLogger.debug("Clear didn't work, using backspace...");
                       AndroidDriver androidDriver = (AndroidDriver) driver;
                       for (int i = 0; i < 50; i++) {
                           androidDriver.pressKey(new KeyEvent(AndroidKey.DEL));
                       }
                       Thread.sleep(300);
                   }
                   TestLogger.pass("Username field cleared");
               } else {
                   TestLogger.debug("Username field already empty/showing hint");
               }
           }
           
           // Clear password field
           WebElement passwordElement = findPasswordField();
           if (passwordElement != null) {
               String currentText = passwordElement.getAttribute("text");
               String showingHint = passwordElement.getAttribute("showingHintText");
               TestLogger.debug("Password field - current text: '" + currentText + "', showing-hint: " + showingHint);
               
               if (!"true".equals(showingHint) && currentText != null && !currentText.equals("Password") && !currentText.isEmpty()) {
                   TestLogger.debug("Clearing password field...");
                   passwordElement.click();
                   Thread.sleep(300);
                   passwordElement.clear();
                   Thread.sleep(300);
                   
                   // Verify it's cleared - use backspace as fallback
                   String afterClear = passwordElement.getAttribute("text");
                   if (afterClear != null && !afterClear.equals("Password") && !afterClear.isEmpty() && !"true".equals(passwordElement.getAttribute("showingHintText"))) {
                       TestLogger.debug("Clear didn't work, using backspace...");
                       AndroidDriver androidDriver = (AndroidDriver) driver;
                       for (int i = 0; i < 50; i++) {
                           androidDriver.pressKey(new KeyEvent(AndroidKey.DEL));
                       }
                       Thread.sleep(300);
                   }
                   TestLogger.pass("Password field cleared");
               } else {
                   TestLogger.debug("Password field already empty/showing hint");
               }
           }
           
           // Hide keyboard if open
           try {
               driver.hideKeyboard();
           } catch (Exception e) {
               // Keyboard not open
           }
           
           TestLogger.info("Credential fields clearing completed");
       } catch (Exception e) {
           TestLogger.warn("Error clearing credential fields: " + e.getMessage());
       }
   }
   
   /**
    * Check if any username locator is present on screen
    * @param timeoutSeconds timeout for each locator check
    * @return true if any username locator is found
    */
   private boolean isAnyUsernameLocatorPresent(int timeoutSeconds) {
       List<By> locators = Arrays.asList(
           Username_by_id,
           Username_by_uiautomator,
           Username_by_xpath
       );
       
       for (By locator : locators) {
           try {
               if (Utility.isElementPresent(locator, timeoutSeconds)) {
                   return true;
               }
           } catch (Exception e) {
               // Continue to next locator
           }
       }
       return false;
   }
  
   // method to ensure user is on login page
   public void ensureLoginPageIsDisplayed() {
        try {
            TestLogger.separator();
            TestLogger.step("Ensuring user is on login page...");
            TestLogger.debug("Current Activity: " + driver.currentActivity());
            
            // Give app time to settle after launch
            Thread.sleep(3000);
            
            // First, check for and handle any ANR (Application Not Responding) dialogs
            handleANRDialog();
            
            // First, check if we're already on the login page (URL entry or credentials screen)
            boolean onUrlScreen = Utility.isElementPresent(EpturaURL, 5);
            boolean onCredentialsScreen = Utility.isElementPresent(UserCredentials_Screen, 10);
            
            TestLogger.debug("Initial state - URL screen: " + onUrlScreen + ", Credentials screen: " + onCredentialsScreen);
            
            if (onUrlScreen || onCredentialsScreen) {
                TestLogger.pass("Already on login page");
                return;
            }
            
            TestLogger.info("Not on login page, checking if user is logged in...");
            
            if (Utility.isElementPresent(Profile_menu_btn, 3) || Utility.isElementPresent(Todaypage_Header, 3) || Utility.isElementPresent(CalendarPage_Header, 3) || Utility.isElementPresent(BookPage_Header, 3) || Utility.isElementPresent(YourTeamPage_Header, 3)) {
                TestLogger.info("User already logged in, attempting logout...");
                performLogout();
                
                // After logout, wait for login page
                Thread.sleep(2000);
                if (Utility.isElementPresent(EpturaURL, 5) || isAnyUsernameLocatorPresent(5)) {
                    TestLogger.pass("Logout successful, now on login page");
                    return;
                }
            }
            
            TestLogger.info("Could not reach login page through logout, restarting app...");
            DriverManager.restartApp();
            
            // After restart, wait longer for the app to initialize
            TestLogger.info("Waiting for app to initialize after restart...");
            Thread.sleep(5000);
            
            // Check for ANR dialog again after restart
            handleANRDialog();
            
            // Check again for login page elements with increased timeout
            TestLogger.debug("Checking for login page elements after restart...");
            for (int i = 0; i < 3; i++) {
                TestLogger.debug("Attempt " + (i + 1) + " - Current Activity: " + driver.currentActivity());
                
                // Check for ANR dialog in each iteration
                handleANRDialog();
                
                if (Utility.isElementPresent(EpturaURL, 5)) {
                    TestLogger.pass("Found URL entry field on login page");
                    return;
                }
                
                if (isAnyUsernameLocatorPresent(5)) {
                    TestLogger.pass("Found username field on login page");
                    return;
                }
                
                TestLogger.debug("Login page not detected yet, waiting...");
                Thread.sleep(3000);
            }
            
            // Last resort - print page source for debugging
            TestLogger.fail("Could not reach login page after all attempts");
            TestLogger.debug("Current Activity: " + driver.currentActivity());
            TestLogger.debug("Printing page source for debugging:");
            TestLogger.debug(driver.getPageSource());
            
            throw new RuntimeException("Failed to navigate to login page after multiple attempts");
            
        } catch (InterruptedException e) {
            TestLogger.warn("Thread interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while ensuring login page: " + e.getMessage());
        } catch (Exception e) {
            TestLogger.error("Error ensuring login page is displayed", e);
            throw new RuntimeException("Failed to navigate to login page: " + e.getMessage());
        }
    }
    
    // Helper method to perform logout
    private void performLogout() {
        try {
            TestLogger.step("Performing logout...");
            
            if (Utility.isElementPresent(Profile_menu_btn, 5)) {
                driver.findElement(Profile_menu_btn).click();
                TestLogger.debug("Clicked Profile menu");
                Thread.sleep(1500);
                
                if (Utility.isElementPresent(logout_option, 5)) {
                    driver.findElement(logout_option).click();
                    TestLogger.debug("Clicked Logout option");
                    Thread.sleep(1000);
                    
                    if (Utility.isElementPresent(logout_confirm, 5)) {
                        driver.findElement(logout_confirm).click();
                        TestLogger.pass("Logout confirmed");
                        Thread.sleep(2000);
                    }
                }
            } else {
                TestLogger.warn("Profile menu not found, cannot logout");
            }
        } catch (Exception e) {
            TestLogger.error("Logout failed", e);
            throw new RuntimeException("Could not logout: " + e.getMessage());
        }
    }

   //CUMA-C226538 Perform Login with valid credential
   public void perform_Forms_Login(String serverName, String userName, String password) throws InterruptedException {
        try {
            TestLogger.separator();
            TestLogger.step("Attempting to perform login...");
            TestLogger.info("Server: " + serverName + ", Username: " + userName);

            TestLogger.debug("Waiting for app to stabilize...");
            Thread.sleep(3000);

            boolean onUrlScreen = Utility.isElementPresent(EpturaURL, 10);
            boolean onCredentialsScreen = isAnyUsernameLocatorPresent(10);
            
            TestLogger.debug("Login flow state - URL screen: " + onUrlScreen + ", Credentials screen: " + onCredentialsScreen);
            
            // If neither screen is detected, retry with longer waits
            if (!onUrlScreen && !onCredentialsScreen) {
                TestLogger.warn("Neither login screen detected, attempting recovery...");
                TestLogger.debug("Current Activity: " + driver.currentActivity());
                
                Thread.sleep(2000);
                
                // Retry detection with longer timeout
                for (int attempt = 1; attempt <= 3; attempt++) {
                    TestLogger.debug("Retry attempt " + attempt + "/3...");
                    
                    onUrlScreen = Utility.isElementPresent(EpturaURL, 10);
                    onCredentialsScreen = isAnyUsernameLocatorPresent(10);
                    
                    if (onUrlScreen || onCredentialsScreen) {
                        TestLogger.pass("Login screen detected on retry " + attempt);
                        break;
                    }
                    
                    Thread.sleep(3000);
                }
                
                // If still not detected, print debug info and throw error
                if (!onUrlScreen && !onCredentialsScreen) {
                    TestLogger.fail("Could not detect login screen after retries");
                    throw new RuntimeException("Cannot proceed with login - login screen not detected after multiple attempts");
                }
            }
            
            if (onCredentialsScreen) {
                TestLogger.pass("Already on credentials screen, skipping URL entry");
            } else if (onUrlScreen) {
                TestLogger.step("On URL entry screen, entering server name...");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.visibilityOfElementLocated(EpturaURL));
                
                driver.findElement(EpturaURL).click();
                driver.findElement(EpturaURL).clear();
                driver.findElement(EpturaURL).sendKeys(serverName);
                TestLogger.pass("Server name entered: " + serverName);
                
                // Wait a bit before hiding keyboard
                Thread.sleep(1000);
                
                try {
                    driver.hideKeyboard();
                    TestLogger.debug("Keyboard hidden");
                    Thread.sleep(1000);
                } catch (Exception keyboardEx) {
                    TestLogger.debug("Keyboard already hidden");
                }
                
                Thread.sleep(500);
                TestLogger.debug("Clicking Continue button...");
                driver.findElement(Continue_btn).click();
                TestLogger.pass("Continue button clicked");
                
                // Wait for transition to credentials screen
                TestLogger.debug("Waiting for credentials screen to load...");
                Thread.sleep(2000);
                
                // Use the dedicated method to wait for credentials dialog to be fully interactive
                TestLogger.debug("Waiting for credentials dialog (60 seconds timeout)...");
                boolean usernameFound = waitForCredentialsDialog(60);
                
                if (!usernameFound) {
                    TestLogger.warn("Username field not found after URL submission!");
                    
                    // Check if still on URL entry screen
                    if (Utility.isElementPresent(EpturaURL, 2)) {
                        TestLogger.debug("Still on URL entry screen - clicking Continue again");
                        driver.findElement(Continue_btn).click();
                        Thread.sleep(3000);
                        
                        if (!waitForCredentialsDialog(30)) {
                            TestLogger.fail("Cannot find username field after URL submission.");
                            throw new RuntimeException("Cannot find username field after URL submission.");
                        }
                    }
                }
                
                Thread.sleep(1500); // Give UI time to stabilize after transition
            } else {
                TestLogger.fail("Not on URL screen or credentials screen");
                throw new RuntimeException("Cannot proceed with login - app is in unexpected state");
            }
            
            TestLogger.separator();
            TestLogger.step("Entering credentials...");

            // Give the credentials popup/screen extra time to fully render and become interactive
            TestLogger.debug("Waiting for credentials screen to stabilize...");
            
            
            // Clear any existing credentials from previous test runs
            clearCredentialFields();
           
            
            // Find username field using multiple locators
            TestLogger.debug("Attempting to find and click username field...");
            WebElement usernameElement = findUsernameField();
            
            if (usernameElement == null) {
                TestLogger.fail("Could not find username field with any locator!");
                TestLogger.debug("Page source for debugging:");
                TestLogger.debug(driver.getPageSource());
                throw new RuntimeException("Username field not found with any locator");
            }
            
            // Wait for element to be clickable
            Thread.sleep(1500);
            
            // Enter username using robust method with verification
            TestLogger.debug("Entering username with robust method...");
            boolean usernameEntered = enterTextWithVerification(usernameElement, userName, "Username");
            
            if (!usernameEntered) {
                TestLogger.warn("Username entry verification failed, attempting fallback...");
                // Re-find element and try one more time with basic approach
                usernameElement = findUsernameField();
                if (usernameElement != null) {
                    usernameElement.click();
                    Thread.sleep(500);
                    usernameElement.clear();
                    Thread.sleep(300);
                    usernameElement.sendKeys(userName);
                    Thread.sleep(500);
                }
            }
            TestLogger.pass("Username entry completed: " + userName);
            Thread.sleep(500);
            
            // Find password field using multiple locators
            TestLogger.debug("Attempting to find and click password field...");
            WebElement passwordElement = findPasswordField();
            
            if (passwordElement == null) {
                TestLogger.fail("Could not find password field with any locator!");
                TestLogger.debug("Page source for debugging:");
                TestLogger.debug(driver.getPageSource());
                throw new RuntimeException("Password field not found with any locator");
            }
            
            // Enter password using robust method with verification
            TestLogger.debug("Entering password with robust method...");
            boolean passwordEntered = enterTextWithVerification(passwordElement, password, "Password");
            
            if (!passwordEntered) {
                TestLogger.warn("Password entry verification failed, attempting fallback...");
                // Re-find element and try one more time
                passwordElement = findPasswordField();
                if (passwordElement != null) {
                    passwordElement.click();
                    Thread.sleep(500);
                    passwordElement.clear();
                    Thread.sleep(300);
                    passwordElement.sendKeys(password);
                    Thread.sleep(500);
                }
            }
            TestLogger.pass("Password entry completed");
            Thread.sleep(800);
            
            try {
                driver.hideKeyboard();
                TestLogger.debug("Keyboard hidden after password entry");
                Thread.sleep(800);
            } catch (Exception keyboardEx) {
                TestLogger.debug("Keyboard already hidden");
            }
            
            // Verify the Continue button is enabled before clicking
            Thread.sleep(500);
            WebElement continueBtn = driver.findElement(Continue_btn);
            TestLogger.debug("Continue button enabled: " + continueBtn.isEnabled());
            
            // Capture screenshot before clicking continue button for CI/CD debugging
            TestLogger.screenshot("Login_BeforeContinueClick");
            try {
                DriverManager.captureScreenshot("Login_BeforeContinueClick_Credentials_Entered");
            } catch (Exception screenshotEx) {
                TestLogger.warn("Failed to capture screenshot: " + screenshotEx.getMessage());
            }
            
            if (!continueBtn.isEnabled()) {
                TestLogger.warn("Continue button is disabled! Credentials may not have been entered correctly.");
                
                // Try re-entering credentials one more time
                TestLogger.debug("Attempting to re-enter credentials...");
                
                // Re-enter username
                usernameElement = findUsernameField();
                if (usernameElement != null) {
                    usernameElement.click();
                    Thread.sleep(300);
                    // Use ADB shell input as last resort
                    try {
                        AndroidDriver androidDriver = (AndroidDriver) driver;
                        androidDriver.executeScript("mobile: shell", java.util.Map.of(
                            "command", "input",
                            "args", java.util.Arrays.asList("text", userName)
                        ));
                        Thread.sleep(500);
                    } catch (Exception e) {
                        usernameElement.sendKeys(userName);
                    }
                }
                
                // Re-enter password
                passwordElement = findPasswordField();
                if (passwordElement != null) {
                    passwordElement.click();
                    Thread.sleep(300);
                    try {
                        AndroidDriver androidDriver = (AndroidDriver) driver;
                        androidDriver.executeScript("mobile: shell", java.util.Map.of(
                            "command", "input",
                            "args", java.util.Arrays.asList("text", password)
                        ));
                        Thread.sleep(500);
                    } catch (Exception e) {
                        passwordElement.sendKeys(password);
                    }
                }
                
                try {
                    driver.hideKeyboard();
                } catch (Exception e) {}
                Thread.sleep(500);
                
                // Re-check continue button
                continueBtn = driver.findElement(Continue_btn);
                TestLogger.debug("Continue button enabled after retry: " + continueBtn.isEnabled());
            }
            
            TestLogger.step("Submitting login credentials...");
            continueBtn.click();
            TestLogger.pass("Login credentials submitted successfully");
            TestLogger.separator();

            // Wait for page to load after login submission
            TestLogger.debug("Waiting for page to load after login...");
            Thread.sleep(5000); // Initial wait for transition to start
            
        } catch (Exception e) {
            TestLogger.error("Login failed: " + e.getMessage());
            throw e;
        }
    }
    
    public Boolean verify_invalid_login() {
        TestLogger.separator();
        TestLogger.step("Verifying invalid login - checking for error messages...");
        
        try {
            // First, check if we're still on the login screen (expected for invalid login)
            boolean onLoginScreen = isAnyUsernameLocatorPresent(3) || 
                                    Utility.isElementPresent(Password_by_id, 3) ||
                                    Utility.isElementPresent(EpturaURL, 3);
            
            TestLogger.debug("On login screen: " + onLoginScreen);
            
            // Try to find the textinput_error element first
            if (Utility.isElementPresent(textInputErrorOnLogin_id, 30)) {
                String returnStringMessage = Utility.getTextFromid(textInputErrorOnLogin_id, 10);
                TestLogger.pass("Found error message: " + returnStringMessage);
                if(returnStringMessage.contains("You are not authorized to login") || 
                   returnStringMessage.contains("Please check your user name and password") ||
                   returnStringMessage.contains("Invalid") ||
                   returnStringMessage.contains("incorrect") ||
                   returnStringMessage.contains("failed")) {
                    TestLogger.pass("Authentication error message verified");
                    return true;
                }
            }
            
            // Alternative: Check for toast messages or dialog with error
            By toastMessage = By.xpath("//*[contains(@text, 'not authorized') or contains(@text, 'Invalid') or contains(@text, 'incorrect') or contains(@text, 'failed') or contains(@text, 'error')]");
            if (Utility.isElementPresent(toastMessage, 5)) {
                TestLogger.pass("Found error toast/dialog message");
                return true;
            }
            
            // Alternative: Check for snackbar error
            By snackbarError = By.id("com.condecosoftware.condeco:id/snackbar_text");
            if (Utility.isElementPresent(snackbarError, 5)) {
                String snackbarText = Utility.getTextFromid(snackbarError, 3);
                TestLogger.pass("Found snackbar message: " + snackbarText);
                return true;
            }
            
            // If we're still on login screen after submission, that also indicates login failed
            if (onLoginScreen) {
                TestLogger.pass("Still on login screen after submission - login was rejected");
                return true;
            }
            
            TestLogger.fail("Could not verify invalid login - no error message found and not on login screen");
            // Print page source for debugging
            TestLogger.debug("Current page source:");
            TestLogger.debug(DriverManager.getDriver().getPageSource());
            return false;
            
        } catch (Exception e) {
            TestLogger.error("Error during invalid login verification: " + e.getMessage());
            return false;
        }
    }
    
    public void verify_Valid_Login() {
        TestLogger.separator();
        TestLogger.step("Verifying successful login...");
        
        try {
            // Wait briefly for the app to settle after login
            
            
            // Handle post-login notifications - this waits for and dismisses
            // the "Automatic Check-In" dialog and any permission dialogs
            TestLogger.info("Starting post-login notification handling...");
            Utility.handlePostLoginNotifications();
            
            // Force dismiss any remaining dialogs as a fallback
          //  TestLogger.debug("Running fallback dialog dismissal...");
           // Utility.forceDismissDialogs();
            
            // Check for home screen indicators (positive verification)
            boolean onHomeScreen = false;
            
            // Check for various home screen elements
            if (Utility.isElementPresent(Todaypage_Header, 5)) {
                TestLogger.pass("Found Today page header - user is logged in and is on Today page");
                onHomeScreen = true;
            }
            
            // Also verify we're NOT on login screen anymore
            boolean notOnLoginScreen = !Utility.isElementPresent(EpturaURL, 2) && 
                                       !Utility.isElementPresent(Password_by_id, 2);
            
            TestLogger.debug("On home screen: " + onHomeScreen);
            TestLogger.debug("Not on login screen: " + notOnLoginScreen);
            
            if (onHomeScreen || notOnLoginScreen) {
                TestLogger.pass("User successfully logged in");
                
                // Final check for any delayed notifications that might have appeared
                Utility.handleAppNotifications();
                Utility.forceDismissDialogs();
            } else {
                TestLogger.fail("Login verification failed");
                TestLogger.debug("Current activity: " + driver.currentActivity());
                throw new AssertionError("Login appears to have failed - still on login screen");
            }
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            TestLogger.error("Login verification failed: " + e.getMessage());
            throw new AssertionError("Login verification failed: " + e.getMessage());
        }
    }
    
   /**
    * Check for any error messages or dialogs that appear after login attempt
    */
   private void checkForLoginErrors() {
       try {
           TestLogger.debug("Scanning for error messages...");
           
           // Check for textinput_error element
           if (Utility.isElementPresent(textInputErrorOnLogin_id, 2)) {
               String errorText = Utility.getTextFromid(textInputErrorOnLogin_id, 2);
               TestLogger.warn("Found error message: " + errorText);
           }
           
           // Check for snackbar error
           By snackbarError = By.id("com.condecosoftware.condeco:id/snackbar_text");
           if (Utility.isElementPresent(snackbarError, 2)) {
               try {
                   String snackbarText = driver.findElement(snackbarError).getText();
                   TestLogger.warn("Found snackbar message: " + snackbarText);
               } catch (Exception e) {}
           }
           
           // Check for any dialog with error text
           By errorDialog = By.xpath("//*[contains(@text, 'error') or contains(@text, 'Error') or contains(@text, 'failed') or contains(@text, 'Failed') or contains(@text, 'invalid') or contains(@text, 'Invalid') or contains(@text, 'unauthorized') or contains(@text, 'Unauthorized') or contains(@text, 'incorrect') or contains(@text, 'Incorrect')]");
           if (Utility.isElementPresent(errorDialog, 2)) {
               try {
                   String errorText = driver.findElement(errorDialog).getText();
                   TestLogger.warn("Found error dialog/text: " + errorText);
               } catch (Exception e) {}
           }
           
           // Check for alert dialog
           By alertTitle = By.id("android:id/alertTitle");
           if (Utility.isElementPresent(alertTitle, 1)) {
               try {
                   String alertText = driver.findElement(alertTitle).getText();
                   TestLogger.warn("Found alert dialog: " + alertText);
                   
                   // Try to get the message too
                   By alertMessage = By.id("android:id/message");
                   if (Utility.isElementPresent(alertMessage, 1)) {
                       String messageText = driver.findElement(alertMessage).getText();
                       TestLogger.warn("Alert message: " + messageText);
                   }
                   
                   // Dismiss the alert if there's an OK button
                   By okButton = By.id("android:id/button1");
                   if (Utility.isElementPresent(okButton, 1)) {
                       driver.findElement(okButton).click();
                       TestLogger.pass("Dismissed alert dialog");
                       Thread.sleep(1000);
                   }
               } catch (Exception e) {}
           }
           
           // Check for network error indicators
           By networkError = By.xpath("//*[contains(@text, 'network') or contains(@text, 'Network') or contains(@text, 'connection') or contains(@text, 'Connection') or contains(@text, 'timeout') or contains(@text, 'Timeout')]");
           if (Utility.isElementPresent(networkError, 1)) {
               try {
                   String networkText = driver.findElement(networkError).getText();
                   TestLogger.warn("Possible network error: " + networkText);
               } catch (Exception e) {}
           }
           
       } catch (Exception e) {
           TestLogger.error("Error checking for login errors: " + e.getMessage());
       }
   }
   
   /**
    * Robust text entry method that tries multiple approaches and verifies the text was entered
    * @param element The WebElement to enter text into
    * @param text The text to enter
    * @param fieldName Name of the field for logging
    * @return true if text was successfully entered and verified
    */
   private boolean enterTextWithVerification(WebElement element, String text, String fieldName) {
       TestLogger.debug("Entering text into " + fieldName + " field using robust method...");
       
       // Approach 1: Standard sendKeys with clear
       try {
           TestLogger.debug("  Approach 1: Standard sendKeys...");
           element.click();
           Thread.sleep(500);
           element.clear();
           Thread.sleep(300);
           element.sendKeys(text);
           Thread.sleep(500);
           
           // Verify text was entered
           String enteredText = element.getText();
           String attributeText = element.getAttribute("text");
           TestLogger.debug("  Verification - getText(): '" + enteredText + "', getAttribute('text'): '" + attributeText + "'");
           
           if ((enteredText != null && enteredText.equals(text)) || 
               (attributeText != null && attributeText.equals(text))) {
               TestLogger.pass("  Approach 1 succeeded!");
               return true;
           }
           
           // Check if it's not showing hint anymore (for password fields that mask text)
           String showingHint = element.getAttribute("showingHintText");
           if ("false".equals(showingHint)) {
               TestLogger.pass("  Approach 1 succeeded (hint no longer showing)!");
               return true;
           }
           
           TestLogger.warn("  Approach 1: Text not verified, trying next approach...");
       } catch (Exception e) {
           TestLogger.debug("  Approach 1 failed: " + e.getMessage());
       }
       
       // Approach 2: Click, clear using Actions, then type character by character
       try {
           TestLogger.debug("  Approach 2: Character-by-character entry...");
           element.click();
           Thread.sleep(500);
           
           // Clear field by selecting all and deleting
           AndroidDriver androidDriver = (AndroidDriver) driver;
           
           // Triple-tap to select all text
           element.click();
           element.click();
           element.click();
           Thread.sleep(300);
           
           // Press delete/backspace multiple times to clear
           for (int i = 0; i < 50; i++) {
               androidDriver.pressKey(new KeyEvent(AndroidKey.DEL));
           }
           Thread.sleep(300);
           
           // Now type text
           element.sendKeys(text);
           Thread.sleep(500);
           
           // Verify
           String showingHint = element.getAttribute("showingHintText");
           if ("false".equals(showingHint)) {
               TestLogger.pass("  Approach 2 succeeded!");
               return true;
           }
           
           String attributeText = element.getAttribute("text");
           if (attributeText != null && !attributeText.equals(fieldName) && !attributeText.isEmpty()) {
               TestLogger.pass("  Approach 2 succeeded (text attribute: " + attributeText + ")!");
               return true;
           }
           
           TestLogger.warn("  Approach 2: Text not verified, trying next approach...");
       } catch (Exception e) {
           TestLogger.debug("  Approach 2 failed: " + e.getMessage());
       }
       
       // Approach 3: Use setValue (Appium-specific method)
       try {
           TestLogger.debug("  Approach 3: Using setValue()...");
           element.click();
           Thread.sleep(500);
           
           // Use Appium's setValue which is more reliable for some apps
           ((io.appium.java_client.android.AndroidDriver) driver).executeScript(
               "mobile: type", 
               java.util.Map.of("text", text)
           );
           Thread.sleep(500);
           
           String showingHint = element.getAttribute("showingHintText");
           if ("false".equals(showingHint)) {
               TestLogger.pass("  Approach 3 succeeded!");
               return true;
           }
           
           TestLogger.warn("  Approach 3: Text not verified, trying next approach...");
       } catch (Exception e) {
           TestLogger.debug("  Approach 3 failed: " + e.getMessage());
       }
       
       // Approach 4: Use ADB shell input (most reliable but slower)
       try {
           TestLogger.debug("  Approach 4: Using ADB shell input...");
           element.click();
           Thread.sleep(500);
           
           // Clear using select all + delete via ADB
           AndroidDriver androidDriver = (AndroidDriver) driver;
           
           // Execute shell command to input text
           // First clear by selecting all (Ctrl+A) and delete
           androidDriver.executeScript("mobile: shell", java.util.Map.of(
               "command", "input",
               "args", java.util.Arrays.asList("keyevent", "KEYCODE_CTRL_LEFT", "KEYCODE_A")
           ));
           Thread.sleep(200);
           androidDriver.executeScript("mobile: shell", java.util.Map.of(
               "command", "input",
               "args", java.util.Arrays.asList("keyevent", "KEYCODE_DEL")
           ));
           Thread.sleep(200);
           
           // Input text via ADB - escape special characters
           String escapedText = text.replace(" ", "%s").replace("'", "\\'");
           androidDriver.executeScript("mobile: shell", java.util.Map.of(
               "command", "input",
               "args", java.util.Arrays.asList("text", escapedText)
           ));
           Thread.sleep(500);
           
           String showingHint = element.getAttribute("showingHintText");
           if ("false".equals(showingHint)) {
               TestLogger.pass("  Approach 4 succeeded!");
               return true;
           }
           
           TestLogger.warn("  Approach 4: Text verification inconclusive");
       } catch (Exception e) {
           TestLogger.debug("  Approach 4 failed: " + e.getMessage());
       }
       
       // Final check - if Continue button becomes enabled, text entry likely worked
       try {
           WebElement continueBtn = driver.findElement(Continue_btn);
           if (continueBtn.isEnabled()) {
               TestLogger.pass("  Continue button is enabled - text entry likely succeeded!");
               return true;
           }
       } catch (Exception e) {
           // Ignore
       }
       
       TestLogger.fail("  All approaches completed. Text may or may not have been entered.");
       return false;
   }

   /**
    * Wait for the credentials dialog to appear and become interactive.
    * This is crucial because the credentials popup appears inside a dialog hierarchy
    * (android:id/parentPanel, android:id/custom, etc.) and we need to wait for it to be fully rendered.
    * @param timeoutSeconds Maximum time to wait for the dialog
    * @return true if the credentials dialog is found and interactive, false otherwise
    */
   private boolean waitForCredentialsDialog(int timeoutSeconds) {
       TestLogger.debug("Waiting for credentials dialog to appear and become interactive...");
       
       long startTime = System.currentTimeMillis();
       long endTime = startTime + (timeoutSeconds * 1000L);
       
       By dialogParentPanel = By.id("android:id/parentPanel");
       By dialogCustom = By.id("android:id/custom");
       By dialogContent = By.id("android:id/content");
       
       int attemptCount = 0;
       
       while (System.currentTimeMillis() < endTime) {
           attemptCount++;
           try {
               TestLogger.debug("  Attempt " + attemptCount + " - Checking for credentials dialog...");
               
               boolean dialogFound = false;
               try {
                   if (Utility.isElementPresent(dialogParentPanel, 1)) {
                       TestLogger.debug("    Found dialog parent panel");
                       dialogFound = true;
                   } else if (Utility.isElementPresent(dialogCustom, 1)) {
                       TestLogger.debug("    Found dialog custom container");
                       dialogFound = true;
                   } else if (Utility.isElementPresent(dialogContent, 1)) {
                       TestLogger.debug("    Found dialog content container");
                       dialogFound = true;
                   }
               } catch (Exception e) { }
               
               if (isAnyUsernameLocatorPresent(2)) {
                   WebElement usernameField = findUsernameField();
                   if (usernameField != null) {
                       boolean isDisplayed = usernameField.isDisplayed();
                       boolean isEnabled = usernameField.isEnabled();
                       
                       TestLogger.debug("    Username field found - displayed: " + isDisplayed + ", enabled: " + isEnabled);
                       
                       if (isDisplayed && isEnabled) {
                           try {
                               String bounds = usernameField.getAttribute("bounds");
                               TestLogger.debug("    Username field bounds: " + bounds);
                               
                               if (bounds != null && !bounds.isEmpty()) {
                                   TestLogger.pass("  Credentials dialog is fully loaded and interactive!");
                                   Thread.sleep(500);
                                   return true;
                               }
                           } catch (Exception boundsEx) {
                               TestLogger.debug("  Credentials dialog appears ready");
                               Thread.sleep(500);
                               return true;
                           }
                       }
                   }
               }
               
               Thread.sleep(2000);
               
           } catch (InterruptedException ie) {
               Thread.currentThread().interrupt();
               TestLogger.warn("  Wait interrupted");
               return false;
           } catch (Exception e) {
               TestLogger.debug("    Check failed: " + e.getMessage());
               try {
                   Thread.sleep(2000);
               } catch (InterruptedException ie) {
                   Thread.currentThread().interrupt();
                   return false;
               }
           }
       }
       
       long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
       TestLogger.fail("  Credentials dialog not found after " + elapsedSeconds + " seconds");
       return false;
   }
   
   /**
    * Handle the Android ANR (Application Not Responding) dialog if it appears
    */
   public void handleANRDialog() {
       try {
           TestLogger.debug("Checking for ANR (Application Not Responding) dialog...");
           
           // Check if the ANR dialog title is present
           if (Utility.isElementPresent(ANR_Dialog_Title, 5)) {
               TestLogger.info("ANR dialog detected - taking appropriate action");
               
               // Click the "Wait" button if available
               if (Utility.isElementPresent(ANR_Wait_Button, 2)) {
                   driver.findElement(ANR_Wait_Button).click();
                   TestLogger.pass("Clicked 'Wait' button on ANR dialog");
                   Thread.sleep(2000);
               } 
               
               // Alternatively, click the "Close" button if "Wait" is not available
               else if (Utility.isElementPresent(ANR_Close_Button, 2)) {
                   driver.findElement(ANR_Close_Button).click();
                   TestLogger.pass("Clicked 'Close' button on ANR dialog");
                   Thread.sleep(2000);
               } 
               
               // If neither button is available, print a warning
               else {
                   TestLogger.warn("ANR dialog detected but no action taken - buttons not found");
               }
           } else {
               TestLogger.debug("No ANR dialog detected");
           }
       } catch (Exception e) {
           TestLogger.error("Error handling ANR dialog: " + e.getMessage());
       }
   }
}