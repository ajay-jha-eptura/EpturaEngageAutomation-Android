package appium.webdriver.reporting;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import appium.webdriver.logging.TestLogger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

/**
 * Allure Report Manager - Utility class for Allure reporting integration
 * Works in conjunction with TestLogger for unified logging
 */
public class AllureReportManager {

    /**
     * Log an info step in the Allure report
     */
    @Step("{message}")
    public static void logInfo(String message) {
        TestLogger.info(message);
    }

    /**
     * Log a pass step in the Allure report
     */
    @Step("✅ {message}")
    public static void logPass(String message) {
        TestLogger.pass(message);
    }

    /**
     * Log a fail step in the Allure report
     */
    @Step("❌ {message}")
    public static void logFail(String message) {
        TestLogger.fail(message);
    }

    /**
     * Log a warning step in the Allure report
     */
    @Step("⚠️ {message}")
    public static void logWarning(String message) {
        TestLogger.warn(message);
    }

    /**
     * Attach a screenshot to the Allure report using Base64 string
     */
    public static void attachScreenshot(String name, String base64Screenshot) {
        try {
            byte[] screenshotBytes = Base64.getDecoder().decode(base64Screenshot);
            InputStream screenshotStream = new ByteArrayInputStream(screenshotBytes);
            Allure.addAttachment(name, "image/png", screenshotStream, ".png");
        } catch (Exception e) {
            TestLogger.warn("Failed to attach screenshot to Allure: " + e.getMessage());
        }
    }

    /**
     * Attach a screenshot to the Allure report using byte array
     */
    @Attachment(value = "{name}", type = "image/png")
    public static byte[] attachScreenshotBytes(String name, byte[] screenshot) {
        return screenshot;
    }

    /**
     * Attach text content to the Allure report
     */
    @Attachment(value = "{name}", type = "text/plain")
    public static String attachText(String name, String content) {
        return content;
    }

    /**
     * Add environment information to the Allure report
     */
    public static void addEnvironmentInfo() {
        TestLogger.info("Platform: Android");
        TestLogger.info("Project: Eptura Engage");
        TestLogger.info("Tester: Ajay Jha");
    }

    /**
     * Add a link to the Allure report
     */
    public static void addLink(String name, String url) {
        Allure.link(name, url);
    }

    /**
     * Add an issue link to the Allure report
     */
    public static void addIssue(String issueId) {
        Allure.issue(issueId, "https://jira.eptura.com/browse/" + issueId);
    }

    /**
     * Add a test case ID link to the Allure report
     */
    public static void addTestCaseId(String testCaseId) {
        Allure.tms(testCaseId, "https://testrail.eptura.com/index.php?/cases/view/" + testCaseId);
    }
}