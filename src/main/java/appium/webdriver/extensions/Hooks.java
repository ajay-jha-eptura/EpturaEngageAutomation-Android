package appium.webdriver.extensions;
import appium.webdriver.reporting.AllureReportManager;
import appium.webdriver.logging.TestLogger;
import io.cucumber.java.*;
import io.qameta.allure.Allure;
import java.util.concurrent.atomic.AtomicInteger;

public class Hooks {
   private static boolean isLastTest = false;
   private static final AtomicInteger scenarioCounter = new AtomicInteger(0);
   private static final AtomicInteger completedScenarios = new AtomicInteger(0);
   private static boolean appStarted = false;
   
   @BeforeAll
   public static void beforeAll() {
       TestLogger.suiteStart("Eptura Engage Android Tests");
       AllureReportManager.addEnvironmentInfo();
       DriverManager.startServer();
       scenarioCounter.set(0);
       completedScenarios.set(0);
       appStarted = false;
   }
   
   @Before
   public void beforeScenario(Scenario scenario) throws Exception {
       int currentScenario = scenarioCounter.incrementAndGet();
       TestLogger.scenarioStart(scenario.getName());
       TestLogger.info("Starting scenario #" + currentScenario);
       
       try {
           DriverManager.createDriver();
           
           if (!appStarted) {
               appStarted = true;
               TestLogger.app("App started for the first time");
           }
           
           // Quick check for any leftover notifications from previous test
           // No sleep needed - handleAppNotifications uses short timeouts
           Utility.handleAppNotifications();
           
       } catch (Exception e) {
           TestLogger.error("Driver initialization failed", e);
           Allure.addAttachment("Error Details", "text/plain", e.getMessage());
           scenario.log("Driver initialization failed: " + e.getMessage());
           scenario.attach(e.getMessage().getBytes(), "text/plain", "Error Details");
           throw e;
       }
   }
   
   @After
   public void afterScenario(Scenario scenario) {
       try {
           int completed = completedScenarios.incrementAndGet();
           boolean isActuallyLastScenario = completed >= scenarioCounter.get() || isLastTest;
           
           TestLogger.info("Completed scenario #" + completed + " out of " + scenarioCounter.get());
           
           if (DriverManager.isDriverInitialized()) {
               String screenshotName = scenario.isFailed() ? "Failed_" + scenario.getName() : "Passed_" + scenario.getName();
               DriverManager.captureScreenshot(screenshotName);
               
               if (scenario.isFailed()) {
                   TestLogger.scenarioEnd(scenario.getName(), false);
               } else {
                   TestLogger.scenarioEnd(scenario.getName(), true);
               }
               
               if (!isActuallyLastScenario) {
                   TestLogger.info("Preparing app for next test...");
                   restartAppWithRetry(1);
               } else {
                   TestLogger.info("Final test completed, skipping app restart");
               }
           } else if (!scenario.isFailed()) {
               TestLogger.fail("Scenario Failed: Driver was never initialized");
               scenario.log("Driver was never initialized");
           }
       } catch (Exception e) {
           TestLogger.error("Error during cleanup", e);
           try {
               TestLogger.app("Final fallback: Quitting driver and creating new session");
               DriverManager.quitDriver();
           } catch (Exception e2) {
               TestLogger.fail("Final cleanup failed: " + e2.getMessage());
           }
       }
   }

   private void restartAppWithRetry(int maxRetries) {
       int retryCount = 0;
       boolean success = false;
       
       while (!success && retryCount < maxRetries) {
           try {
               TestLogger.app("Restarting app for next test... (Attempt " + (retryCount + 1) + ")");
               DriverManager.restartApp();
               success = true;
           } catch (Exception e) {
               retryCount++;
               TestLogger.fail("App restart attempt " + retryCount + " failed: " + e.getMessage());
               if (retryCount >= maxRetries) {
                   TestLogger.warn("Max restarts reached, continuing without restart");
                   break;
               }
               
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException ie) {
                   Thread.currentThread().interrupt();
               }
           }
       }
   }

   public static void setLastTest(boolean isLast) {
       isLastTest = isLast;
       if (isLast) {
           TestLogger.info("Final test execution phase marked");
       }
   }

   @AfterAll
   public static void afterAll() {
       scenarioCounter.set(0);
       completedScenarios.set(0);
       
       TestLogger.info("Cleaning up resources and quitting driver");
       if (DriverManager.isDriverInitialized()) {
           DriverManager.quitDriver();
       }
       DriverManager.stopServer();
       TestLogger.suiteEnd("Eptura Engage Android Tests", completedScenarios.get(), 0);
   }
}