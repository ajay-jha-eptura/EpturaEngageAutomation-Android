import re

# Read the file
file_path = r'src\test\java\com\client\app\pages\LoginPage.java'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# The broken method to replace
old_method_pattern = r'//CUMA-C226538 Perform Login with valid credential\s+public void perform_Forms_Login\(String serverName, String userName, String password\) throws InterruptedException \{\s+\{\s+\{\s+try \{\s+util\.waitforElementUntillPresent\(profileButton_id\);\s+if\(driver\.findElement\(profileButton_id\)\.isDisplayed\(\)\)\s+\{\s+System\.out\.println\(" User already Logged-in"\);\s+\}\s+\} catch \(Exception e\) \{\s+util\.waitforElementUntillPresent\(CondecoURL\);\s+driver\.findElement\(CondecoURL\)\.click\(\);\s+driver\.findElement\(CondecoURL\)\.sendKeys\(serverName\);\s+driver\.findElement\(Continue_btn\)\.click\(\);\s+util\.waitforElementUntillPresent\(Username_id\);\s+driver\.findElement\(Username_id\)\.sendKeys\(userName\);\s+driver\.findElement\(Password_id\)\.sendKeys\(password\);\s+driver\.findElement\(Continue_btn\)\.click\(\);\s+//util\.waitforElementUntillPresent\(profileButton_id\);\s+\}\s+\}'

# The fixed method
new_method = '''//CUMA-C226538 Perform Login with valid credential
   public void perform_Forms_Login(String serverName, String userName, String password) throws InterruptedException {
       try {
           TestLogger.separator();
           TestLogger.step("Performing Forms Login...");
           
           // Check if user is already logged in
           if (Utility.isElementPresent(Profile_menu_btn, 3)) {
               TestLogger.info("User already logged in");
               return;
           }
           
           // Handle server URL entry if on URL screen
           if (Utility.isElementPresent(EpturaURL, 5)) {
               TestLogger.debug("Entering server URL: " + serverName);
               WebElement urlField = driver.findElement(EpturaURL);
               urlField.click();
               urlField.clear();
               urlField.sendKeys(serverName);
               driver.findElement(Continue_btn).click();
               TestLogger.pass("Server URL submitted");
               Thread.sleep(2000);
           }
           
           // Wait for credentials dialog to appear
           TestLogger.debug("Waiting for credentials dialog...");
           if (!waitForCredentialsDialog(30)) {
               TestLogger.warn("Credentials dialog not found, checking current state...");
           }
           
           // Clear any existing text in credential fields
           clearCredentialFields();
           
           // Enter username
           TestLogger.debug("Entering username...");
           WebElement usernameField = findUsernameField();
           if (usernameField != null) {
               enterTextWithVerification(usernameField, userName, "Username");
           } else {
               throw new RuntimeException("Username field not found");
           }
           
           // Enter password
           TestLogger.debug("Entering password...");
           WebElement passwordField = findPasswordField();
           if (passwordField != null) {
               enterTextWithVerification(passwordField, password, "Password");
           } else {
               throw new RuntimeException("Password field not found");
           }
           
           // Hide keyboard if open
           try {
               driver.hideKeyboard();
           } catch (Exception e) {
               // Keyboard may not be open
           }
           
           // Click Continue/Login button
           TestLogger.debug("Clicking Continue button...");
           if (Utility.isElementPresent(Continue_btn, 5)) {
               driver.findElement(Continue_btn).click();
               TestLogger.pass("Login credentials submitted successfully");
           } else {
               throw new RuntimeException("Continue button not found");
           }
           
           TestLogger.separator();
           
           // Wait for page to load after login
           TestLogger.debug("Waiting for page to load after login...");
           Thread.sleep(3000);
           
           // Check for any error messages
           TestLogger.debug("Checking for error messages after login submission...");
           checkForLoginErrors();
           
       } catch (Exception e) {
           TestLogger.error("ERROR during login", e);
           throw new RuntimeException("Login failed: " + e.getMessage(), e);
       }
   }'''

# Try to find and replace the broken method
if re.search(old_method_pattern, content, re.DOTALL):
    content = re.sub(old_method_pattern, new_method, content, flags=re.DOTALL)
    print("Method replaced using regex pattern")
else:
    # Fallback: Find the method by simpler pattern and replace
    start_marker = '//CUMA-C226538 Perform Login with valid credential'
    end_marker = 'public Boolean verify_invalid_login()'
    
    start_idx = content.find(start_marker)
    end_idx = content.find(end_marker)
    
    if start_idx != -1 and end_idx != -1:
        # Replace everything between start and end markers
        content = content[:start_idx] + new_method + '\n\n    ' + content[end_idx:]
        print("Method replaced using marker-based approach")
    else:
        print(f"Could not find markers. Start: {start_idx}, End: {end_idx}")

# Write the file back
with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("File updated successfully!")
