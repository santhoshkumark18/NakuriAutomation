@echo off
echo Starting Naukri Profile Update Test with Email Notifications...

REM Set environment variables for testing
set NAUKRI_USERNAME=santhoshkumar18012002@gmail.com
set NAUKRI_PASSWORD=saN!thosh@027

REM Email configuration (update these with your details)
set SENDER_EMAIL=your-gmail@gmail.com
set SENDER_APP_PASSWORD=your-16-digit-app-password
set RECIPIENT_EMAIL=santhoshkumar18012002@gmail.com

echo Environment variables set...
echo Running Maven test with email notifications...

mvn clean test -Dtest=DailyUpdateTestRunner -Dcucumber.filter.tags="@profileUpdate"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ================================
    echo Test completed successfully!
    echo ================================
    echo Check target/cucumber-reports/index.html for detailed report
    echo Email notification should have been sent to %RECIPIENT_EMAIL%
    echo.
) else (
    echo.
    echo ================================
    echo Test failed! Check console output for errors.
    echo ================================
    echo Email notification should have been sent with failure details
    echo.
)

echo Press any key to open test report...
pause >nul
start target\cucumber-reports\index.html
