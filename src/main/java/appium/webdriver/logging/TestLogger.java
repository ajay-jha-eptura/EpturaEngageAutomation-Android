package appium.webdriver.logging;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized Test Logger for the automation framework.
 * Provides consistent logging across console, log files, and Allure reports.
 * 
 * Usage:
 *   TestLogger.info("Starting test...");
 *   TestLogger.pass("Login successful");
 *   TestLogger.fail("Element not found");
 *   TestLogger.warn("Retrying operation...");
 *   TestLogger.debug("Variable value: " + value);
 */
public class TestLogger {

    private static final Logger logger = LoggerFactory.getLogger("TestAutomation");
    
    // ANSI color codes for console output
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    
    // Emojis for visual distinction
    private static final String EMOJI_INFO = "ℹ️ ";
    private static final String EMOJI_PASS = "✅ ";
    private static final String EMOJI_FAIL = "❌ ";
    private static final String EMOJI_WARN = "⚠️ ";
    private static final String EMOJI_DEBUG = "🔍 ";
    private static final String EMOJI_STEP = "▶️ ";
    private static final String EMOJI_SCENARIO = "📋 ";
    private static final String EMOJI_SCREENSHOT = "📸 ";
    private static final String EMOJI_APP = "📱 ";
    private static final String EMOJI_SERVER = "🖥️ ";
    private static final String EMOJI_ELEMENT = "🔎 ";

    /**
     * Log an informational message
     */
    public static void info(String message) {
        String formattedMsg = EMOJI_INFO + message;
        logger.info(formattedMsg);
        System.out.println(BLUE + formattedMsg + RESET);
    }

    /**
     * Log a success/pass message - also adds to Allure report
     */
    @Step("✅ {message}")
    public static void pass(String message) {
        String formattedMsg = EMOJI_PASS + message;
        logger.info(formattedMsg);
        System.out.println(GREEN + formattedMsg + RESET);
    }

    /**
     * Log a failure message - also adds to Allure report
     */
    @Step("❌ {message}")
    public static void fail(String message) {
        String formattedMsg = EMOJI_FAIL + message;
        logger.error(formattedMsg);
        System.out.println(RED + formattedMsg + RESET);
    }

    /**
     * Log a warning message - also adds to Allure report
     */
    @Step("⚠️ {message}")
    public static void warn(String message) {
        String formattedMsg = EMOJI_WARN + message;
        logger.warn(formattedMsg);
        System.out.println(YELLOW + formattedMsg + RESET);
    }

    /**
     * Log a debug message (only visible when debug level is enabled)
     */
    public static void debug(String message) {
        String formattedMsg = EMOJI_DEBUG + message;
        logger.debug(formattedMsg);
        if (logger.isDebugEnabled()) {
            System.out.println(CYAN + formattedMsg + RESET);
        }
    }

    /**
     * Log a test step - adds to Allure report
     */
    @Step("{stepDescription}")
    public static void step(String stepDescription) {
        String formattedMsg = EMOJI_STEP + stepDescription;
        logger.info(formattedMsg);
        System.out.println(BLUE + formattedMsg + RESET);
    }

    /**
     * Log scenario start
     */
    public static void scenarioStart(String scenarioName) {
        String separator = "═".repeat(60);
        String formattedMsg = EMOJI_SCENARIO + "SCENARIO: " + scenarioName;
        logger.info(separator);
        logger.info(formattedMsg);
        logger.info(separator);
        System.out.println("\n" + CYAN + separator + RESET);
        System.out.println(CYAN + formattedMsg + RESET);
        System.out.println(CYAN + separator + RESET);
    }

    /**
     * Log scenario end with status
     */
    public static void scenarioEnd(String scenarioName, boolean passed) {
        String status = passed ? "PASSED" : "FAILED";
        String emoji = passed ? EMOJI_PASS : EMOJI_FAIL;
        String color = passed ? GREEN : RED;
        String formattedMsg = emoji + "SCENARIO " + status + ": " + scenarioName;
        String separator = "─".repeat(60);
        
        if (passed) {
            logger.info(formattedMsg);
        } else {
            logger.error(formattedMsg);
        }
        System.out.println(color + formattedMsg + RESET);
        System.out.println(color + separator + RESET + "\n");
    }

    /**
     * Log app-related actions
     */
    public static void app(String message) {
        String formattedMsg = EMOJI_APP + message;
        logger.info(formattedMsg);
        System.out.println(BLUE + formattedMsg + RESET);
    }

    /**
     * Log server-related actions
     */
    public static void server(String message) {
        String formattedMsg = EMOJI_SERVER + message;
        logger.info(formattedMsg);
        System.out.println(CYAN + formattedMsg + RESET);
    }

    /**
     * Log element interactions
     */
    public static void element(String action, String elementDescription) {
        String formattedMsg = EMOJI_ELEMENT + action + ": " + elementDescription;
        logger.debug(formattedMsg);
        System.out.println(CYAN + formattedMsg + RESET);
    }

    /**
     * Log screenshot capture
     */
    public static void screenshot(String screenshotName) {
        String formattedMsg = EMOJI_SCREENSHOT + "Screenshot captured: " + screenshotName;
        logger.info(formattedMsg);
        System.out.println(BLUE + formattedMsg + RESET);
    }

    /**
     * Log an exception with stack trace
     */
    public static void error(String message, Throwable throwable) {
        String formattedMsg = EMOJI_FAIL + message;
        logger.error(formattedMsg, throwable);
        System.out.println(RED + formattedMsg + RESET);
        System.out.println(RED + "Exception: " + throwable.getMessage() + RESET);
        
        // Attach exception to Allure
        Allure.addAttachment("Exception Details", "text/plain", 
            message + "\n\nException: " + throwable.getClass().getName() + 
            "\nMessage: " + throwable.getMessage() + 
            "\nStack Trace:\n" + getStackTraceString(throwable));
    }

    /**
     * Log an error message without exception (overloaded method)
     */
    public static void error(String message) {
        String formattedMsg = EMOJI_FAIL + message;
        logger.error(formattedMsg);
        System.out.println(RED + formattedMsg + RESET);
    }

    /**
     * Log with custom emoji
     */
    public static void logWithEmoji(String emoji, String message) {
        String formattedMsg = emoji + " " + message;
        logger.info(formattedMsg);
        System.out.println(formattedMsg);
    }

    /**
     * Add a separator line in logs
     */
    public static void separator() {
        String line = "─".repeat(60);
        logger.info(line);
        System.out.println(line);
    }

    /**
     * Log test suite start
     */
    public static void suiteStart(String suiteName) {
        String border = "╔" + "═".repeat(58) + "╗";
        String middle = "║" + centerText("TEST SUITE: " + suiteName, 58) + "║";
        String bottom = "╚" + "═".repeat(58) + "╝";
        
        logger.info(border);
        logger.info(middle);
        logger.info(bottom);
        
        System.out.println("\n" + GREEN + border + RESET);
        System.out.println(GREEN + middle + RESET);
        System.out.println(GREEN + bottom + RESET + "\n");
    }

    /**
     * Log test suite end
     */
    public static void suiteEnd(String suiteName, int passed, int failed) {
        String border = "╔" + "═".repeat(58) + "╗";
        String title = "║" + centerText("TEST SUITE COMPLETED: " + suiteName, 58) + "║";
        String results = "║" + centerText("Passed: " + passed + " | Failed: " + failed, 58) + "║";
        String bottom = "╚" + "═".repeat(58) + "╝";
        
        String color = failed == 0 ? GREEN : RED;
        
        logger.info(border);
        logger.info(title);
        logger.info(results);
        logger.info(bottom);
        
        System.out.println("\n" + color + border + RESET);
        System.out.println(color + title + RESET);
        System.out.println(color + results + RESET);
        System.out.println(color + bottom + RESET + "\n");
    }

    // Helper method to center text
    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }

    // Helper method to convert stack trace to string
    private static String getStackTraceString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
