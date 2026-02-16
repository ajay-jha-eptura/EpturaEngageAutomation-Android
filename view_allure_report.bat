@echo off
echo ========================================
echo Allure Report Viewer
echo ========================================
echo.

REM Check if a folder path was provided as argument
if "%~1"=="" (
    echo Usage: view_allure_report.bat [path-to-downloaded-artifact-folder]
    echo.
    echo Examples:
    echo   view_allure_report.bat "C:\Downloads\AllureResults"
    echo   view_allure_report.bat "C:\Downloads\AllureReport"
    echo.
    echo If you have allure-results folder (JSON files):
    echo   This script will generate and open the report
    echo.
    echo If you have allure-report folder (HTML files):
    echo   This script will serve and open it directly
    echo.
    
    REM Check if local target folders exist
    if exist "target\allure-report" (
        echo Found local allure-report folder. Opening it...
        echo.
        allure open target\allure-report
        goto :end
    )
    
    if exist "target\allure-results" (
        echo Found local allure-results folder. Generating and opening report...
        echo.
        allure serve target\allure-results
        goto :end
    )
    
    echo No local Allure folders found.
    echo Please provide the path to your downloaded artifact folder.
    pause
    exit /b 1
)

set "ARTIFACT_PATH=%~1"

REM Check if the path exists
if not exist "%ARTIFACT_PATH%" (
    echo ERROR: Path does not exist: %ARTIFACT_PATH%
    pause
    exit /b 1
)

REM Check if it's an allure-report folder (contains index.html)
if exist "%ARTIFACT_PATH%\index.html" (
    echo Detected: Allure HTML Report folder
    echo Opening report with Allure CLI...
    echo.
    echo Press Ctrl+C to stop the server when done viewing.
    echo.
    allure open "%ARTIFACT_PATH%"
    goto :end
)

REM Check if it's an allure-results folder (contains JSON files)
dir /b "%ARTIFACT_PATH%\*.json" >nul 2>&1
if %errorlevel%==0 (
    echo Detected: Allure Results folder (JSON files)
    echo Generating and opening HTML report...
    echo.
    echo Press Ctrl+C to stop the server when done viewing.
    echo.
    allure serve "%ARTIFACT_PATH%"
    goto :end
)

REM Check for nested folders
if exist "%ARTIFACT_PATH%\allure-report\index.html" (
    echo Detected: Nested allure-report folder
    echo Opening report...
    echo.
    allure open "%ARTIFACT_PATH%\allure-report"
    goto :end
)

if exist "%ARTIFACT_PATH%\allure-results" (
    echo Detected: Nested allure-results folder
    echo Generating and opening report...
    echo.
    allure serve "%ARTIFACT_PATH%\allure-results"
    goto :end
)

echo ERROR: Could not detect Allure report or results in the specified folder.
echo.
echo The folder should contain either:
echo   - index.html (for allure-report)
echo   - *.json files (for allure-results)
echo.
echo Contents of %ARTIFACT_PATH%:
dir "%ARTIFACT_PATH%"
pause
exit /b 1

:end
echo.
echo ========================================
echo Report viewer closed.
echo ========================================
