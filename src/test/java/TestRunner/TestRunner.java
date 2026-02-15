package TestRunner;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import appium.webdriver.extensions.Hooks;

@CucumberOptions(
       features = "src/test/java/FeatureFiles",
       glue = {"com.client.app.stepDefs", "appium.webdriver.extensions"},
       plugin = {"pretty", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"},
       monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
    
    @BeforeClass
    public void beforeClass(ITestContext context) {
        // Reset the last test flag at the beginning of the test run
        Hooks.setLastTest(false);
        System.out.println("🚀 Starting test execution with " + context.getAllTestMethods().length + " test methods");
    }
    
    @AfterClass
    public void afterClass() {
        // Set the last test flag to true when all tests are complete
        Hooks.setLastTest(true);
        System.out.println("✅ Test execution complete - preventing further app restarts");
    }
}