@echo off
echo =====================================
echo Starting Naukri Profile Update Test
echo =====================================

REM Set environment variables for testing
set NAUKRI_USERNAME=santhoshkumar18012002@gmail.com
set NAUKRI_PASSWORD=saN!thosh@027

REM Email configuration (set these if you want to test email)
REM set SENDER_EMAIL=your-gmail@gmail.com
REM set SENDER_APP_PASSWORD=your-16-digit-app-password
set RECIPIENT_EMAIL=santhoshkumar18012002@gmail.com

echo.
echo Environment variables set for Naukri login...
echo Username: %NAUKRI_USERNAME%
echo Email notifications: %RECIPIENT_EMAIL%
echo.

echo Running Maven clean and test...
echo.

mvn clean test -Dtest=DailyUpdateTestRunner -Dcucumber.filter.tags="@profileUpdate"

echo.
if %ERRORLEVEL% EQU 0 (
    echo ================================
    echo ✅ Test completed successfully!
    echo ================================
    echo Check target/cucumber-reports/index.html for detailed report
    if defined SENDER_EMAIL (
        echo Email notification should have been sent to %RECIPIENT_EMAIL%
    ) else (
        echo Email notifications disabled (SENDER_EMAIL not set)
    )
) else (
    echo ================================
    echo ❌ Test failed! Check console output for errors.
    echo ================================
    if defined SENDER_EMAIL (
        echo Email notification should have been sent with failure details
    )
)

echo.
echo Opening test report...
if exist "target\cucumber-reports\index.html" (
    start target\cucumber-reports\index.html
) else (
    echo Test report not found. Check Maven output above for errors.
)

echo.
pause
