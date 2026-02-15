@echo off
echo ========================================
echo Allure Report Generator
echo ========================================
echo.

REM Check if allure-results folder exists
if not exist "target\allure-results" (
    echo ERROR: No test results found!
    echo Please run your tests first using: mvn clean test
    pause
    exit /b 1
)

echo.
echo Choose an option:
echo [1] Generate and open report in browser (recommended)
echo [2] Generate static HTML report only
echo.
set /p choice="Enter choice (1/2): "

if "%choice%"=="1" (
    echo.
    echo Generating and serving Allure Report...
    echo The report will open in your default browser.
    echo Press Ctrl+C to stop the server when done viewing.
    echo.
    allure serve target\allure-results
) else if "%choice%"=="2" (
    echo.
    echo Generating static Allure Report...
    allure generate target\allure-results -o target\allure-report --clean
    echo.
    echo ========================================
    echo Report generated at: target\allure-report
    echo ========================================
    echo.
    echo NOTE: To view the report properly, run:
    echo   allure open target\allure-report
    echo.
    echo Or run open_allure_report.bat to serve and view it.
    pause
) else (
    echo Invalid choice. Running default option...
    allure serve target\allure-results
)