package TestRunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
       features = "src/test/java/FeatureFiles/UserProfile.feature",
       glue = {"com.client.app.stepDefs", "appium.webdriver.extensions"},
       plugin = {"pretty", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"},
       monochrome = true
)
public class UserProfileTestRunner extends AbstractTestNGCucumberTests {
}