package com.client.app.stepDefs;
import com.client.app.pages.LoginPage;
import appium.webdriver.extensions.Utility;
import appium.webdriver.logging.TestLogger;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import java.net.MalformedURLException;

import org.testng.Assert;


public class LoginStepDef {
	
   private final LoginPage loginPage = new LoginPage();
   
	String getServerName = Utility.getProperty("mobile.app.login.servername");
	String getUserName = Utility.getProperty("mobile.app.login.username");
	String getPassword = Utility.getProperty("mobile.app.login.password");
	
   @Given("User is on Login page")
   public void UserLogin() throws MalformedURLException, InterruptedException
	{
		TestLogger.step("User is on Login page");
		TestLogger.app("App Launched");
		loginPage.ensureLoginPageIsDisplayed();
	}
   
	@When("User performs Forms Login")
	public void User_performs_Forms_Login() throws InterruptedException
	{
		TestLogger.step("User performs Forms Login");
		loginPage.perform_Forms_Login(getServerName, getUserName, getPassword);
	}
   
	
	@When("^User attempts Forms Login with wrong password as \"([^\"]*)\"$")
	public void user_performs_Forms_Login_with_invalid_credentials(String incorrectpassword) throws InterruptedException
	{
		TestLogger.step("User attempts Forms Login with wrong password");
		loginPage.perform_Forms_Login(getServerName, getUserName, incorrectpassword);
	}
	
	
	@Then("Verify authentication failed message")
	public void verify_invalid_Forms_login()
	{
		TestLogger.step("Verify authentication failed message");
		Assert.assertTrue(loginPage.verify_invalid_login(), "Authentication failed message not displayed");
	}
	
	@Then("User successfully logged-in")
	public void verify_UserLogin()
	{
		TestLogger.step("Verify User successfully logged-in");
		loginPage.verify_Valid_Login();
	}
}