@echo off
REM ============================================================
REM Run Tests on Emulator or Physical Device
REM Automatically detects and uses available device
REM Generates and opens Allure Report after test completion
REM ============================================================

echo ============================================================
echo Eptura Engage Android Automation - Test Runner
echo ============================================================
echo.

REM Clean up old reports
echo Cleaning up old report files...
if exist "test-output" rmdir /s /q "test-output"
if exist "target\surefire-reports" rmdir /s /q "target\surefire-reports"
if exist "target\allure-results" rmdir /s /q "target\allure-results"
if exist "target\allure-report" rmdir /s /q "target\allure-report"
echo Old reports cleaned up.
echo.

REM Set Android SDK location
set ANDROID_SDK=%LOCALAPPDATA%\Android\Sdk
set PATH=%ANDROID_SDK%\platform-tools;%PATH%

echo [1/5] Checking for connected devices...
echo.

REM Check for any connected device (physical or emulator)
adb devices > devices_temp.txt 2>&1
type devices_temp.txt

REM Count connected devices (excluding header line)
set DEVICE_COUNT=0
for /f "skip=1 tokens=1" %%a in (devices_temp.txt) do (
    if not "%%a"=="" set /a DEVICE_COUNT+=1
)

echo.
echo Found %DEVICE_COUNT% device(s) connected.
echo.

if %DEVICE_COUNT%==0 (
    echo [2/5] No device connected. Starting emulator...
    echo.
    
    REM Check if emulator exists
    set EMULATOR_NAME=Pixel_6_API_35_PlayStore
    
    echo Checking for emulator: %EMULATOR_NAME%...
    "%ANDROID_SDK%\emulator\emulator.exe" -list-avds 2>nul | findstr /C:"%EMULATOR_NAME%" >nul
    
    if errorlevel 1 (
        echo.
        echo ERROR: Emulator '%EMULATOR_NAME%' not found!
        echo.
        echo Please create an emulator first:
        echo   1. Open Android Studio
        echo   2. Go to Tools ^> Device Manager
        echo   3. Click "Create Device"
        echo   4. Select Pixel 6 ^> Next
        echo   5. Select API 35 with Google Play ^> Next
        echo   6. Name it: Pixel_6_API_35_PlayStore ^> Finish
        echo.
        echo Or run: scripts\create_emulator.bat
        echo.
        del devices_temp.txt 2>nul
        pause
        exit /b 1
    )
    
    echo Starting emulator: %EMULATOR_NAME%...
    echo This may take 2-3 minutes...
    echo.
    
    REM Start emulator in background
    start "" "%ANDROID_SDK%\emulator\emulator.exe" -avd %EMULATOR_NAME% -netdelay none -netspeed full -gpu auto -memory 4096
    
    echo Waiting for emulator to boot...
    adb wait-for-device
    
    REM Wait for boot completion
    :WAIT_BOOT
    adb shell getprop sys.boot_completed 2>nul | findstr "1" >nul
    if errorlevel 1 (
        echo   Still booting...
        timeout /t 5 /nobreak >nul
        goto WAIT_BOOT
    )
    
    echo.
    echo Emulator started successfully!
    echo.
    
    REM Update config to use emulator
    set DEVICE_NAME=emulator-5554
) else (
    echo [2/5] Device already connected!
    
    REM Get the device name
    for /f "skip=1 tokens=1" %%a in (devices_temp.txt) do (
        if not "%%a"=="" (
            set DEVICE_NAME=%%a
            goto :FOUND_DEVICE
        )
    )
    :FOUND_DEVICE
    echo Using device: %DEVICE_NAME%
    echo.
)

del devices_temp.txt 2>nul

echo [3/5] Updating configuration for device: %DEVICE_NAME%...
echo.

REM Update Config.properties with detected device
(
echo # Device Configuration - Auto-detected
echo # Updated by run_tests.bat on %date% %time%
echo DeviceName=%DEVICE_NAME%
echo deviceName=%DEVICE_NAME%
echo AndroidOSVersion=15
echo platformName=Android
echo appPackage=com.condecosoftware.condeco
echo appActivity=com.condecosoftware.deskbooking.application.startup.DeskStartupActivity
echo browser_name=Android
echo service.timeout=60000
echo mobile.app.login.servername=unified1.condecodev.com
echo mobile.app.login.username=user141
echo mobile.app.login.password=1
) > src\main\resources\Config.properties

echo Configuration updated!
echo.

echo [4/5] Checking if APK is installed...
adb shell pm list packages | findstr "com.condecosoftware.condeco" >nul
if errorlevel 1 (
    echo.
    echo WARNING: Eptura Engage app is not installed!
    echo Please install the APK first:
    echo   adb install path\to\EpturaEngage.apk
    echo.
    echo Or download from Google Play Store on the emulator.
    echo.
    set /p CONTINUE_ANYWAY="Continue anyway? (y/n): "
    if /i not "%CONTINUE_ANYWAY%"=="y" (
        pause
        exit /b 1
    )
)

echo [5/5] Starting tests...
echo.

REM Run Maven tests
call mvn clean test -Dtestng.xml=testng.xml

REM Capture the test result
set TEST_RESULT=%ERRORLEVEL%

echo.
echo ============================================================
echo Test execution completed!
echo ============================================================
echo.

REM ============================================================
REM ALLURE REPORT GENERATION AND DISPLAY
REM ============================================================

echo ============================================================
echo Generating and Opening Allure Report...
echo ============================================================
echo.

REM Check if allure-results exist
if not exist "target\allure-results" (
    echo ERROR: No allure-results folder found!
    echo Tests may not have run successfully.
    echo.
    pause
    exit /b 1
)

REM Count result files
set RESULT_COUNT=0
for %%f in (target\allure-results\*.json) do set /a RESULT_COUNT+=1
echo Found %RESULT_COUNT% result file(s) in target\allure-results
echo.

REM Check if allure CLI is available
where allure >nul 2>&1
if %ERRORLEVEL%==0 (
    echo Allure CLI found. Serving report...
    echo.
    echo ============================================================
    echo The Allure Report will open in your default browser.
    echo Press Ctrl+C in this window to stop the server when done.
    echo ============================================================
    echo.
    
    REM Use allure serve - this is the proper way to view Allure reports
    allure serve target\allure-results
) else (
    echo Allure CLI not found. Trying Maven plugin...
    echo.
    
    REM Generate report using Maven
    call mvn allure:report
    
    if exist "target\allure-report\index.html" (
        echo.
        echo Report generated at: target\allure-report\index.html
        echo.
        echo ============================================================
        echo IMPORTANT: For best viewing experience, install Allure CLI:
        echo.
        echo Option 1 - Using Scoop (recommended for Windows):
        echo   powershell -Command "irm get.scoop.sh | iex"
        echo   scoop install allure
        echo.
        echo Option 2 - Using npm:
        echo   npm install -g allure-commandline
        echo.
        echo Option 3 - Using Chocolatey:
        echo   choco install allure
        echo ============================================================
        echo.
        
        REM Try to serve using Python if available
        where python >nul 2>&1
        if %ERRORLEVEL%==0 (
            echo Starting local server to view report...
            echo Report will be available at: http://localhost:8000
            echo Press Ctrl+C to stop the server.
            echo.
            cd target\allure-report
            start "" "http://localhost:8000"
            python -m http.server 8000
        ) else (
            echo Opening report directly (may have limited functionality)...
            start "" "target\allure-report\index.html"
        )
    ) else (
        echo ERROR: Failed to generate Allure report.
        echo Check Maven output for errors.
    )
)

echo.
if %TEST_RESULT%==0 (
    echo ============================================================
    echo ALL TESTS PASSED!
    echo ============================================================
) else (
    echo ============================================================
    echo SOME TESTS FAILED - Check the Allure report for details
    echo ============================================================
)

pause
