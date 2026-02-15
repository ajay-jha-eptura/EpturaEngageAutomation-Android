@echo off
echo ========================================
echo Opening Allure Report
echo ========================================
echo.

if not exist "target\allure-results" (
    echo ERROR: No test results found!
    echo Please run your tests first using: mvn clean test
    pause
    exit /b 1
)

echo Generating and opening Allure Report...
echo.
echo This will open the report in your default browser.
echo Press Ctrl+C in this window to stop the server when done viewing.
echo.

allure serve target\allure-results