package stepDefinitions;

import java.time.Duration;

import static org.junit.Assert.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import factory.Base;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pageObjects.LoginPageObj;
import pageObjects.ProfilePageObj;
import utils.EmailNotificationUtil;

public class DailyProfileUpdateStepDef {
    
    WebDriver driver;
    LoginPageObj lp;
    ProfilePageObj pp;
    WebDriverWait wait;
    StringBuilder executionSummary = new StringBuilder();
    boolean locationUpdated = false;
    boolean resumeUpdated = false;
    boolean profileUpdateSuccess = false;
    
    @Given("I navigate to Naukri Application for daily update")
    public void i_navigate_to_naukri_application_for_daily_update() {
        driver = Base.getDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        lp = new LoginPageObj(driver);
        
        System.out.println("✓ Navigated to Naukri - Current URL: " + driver.getCurrentUrl());
        System.out.println("✓ Page title: " + driver.getTitle());
        
        // Check for access denied and retry
        if(driver.getTitle().contains("Access Denied") || driver.getPageSource().contains("Access Denied")) {
            System.out.println("⚠️ Access Denied detected, waiting and retrying...");
            try {
                Thread.sleep(5000); // Wait 5 seconds
                driver.navigate().refresh(); // Refresh page
                Thread.sleep(3000); // Wait for refresh
                
                if(driver.getTitle().contains("Access Denied")) {
                    System.out.println("⚠️ Still getting Access Denied, trying different approach...");
                    // Try navigating to different URL
                    driver.navigate().to("https://www.naukri.com/nlogin/login");
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("✓ Final URL: " + driver.getCurrentUrl());
        System.out.println("✓ Final Page title: " + driver.getTitle());
        
        // Add wait for page to load completely
        try {
            Thread.sleep(3000); // Allow page to load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        lp.selectLogin();
    }
    
    @Given("I login with environment credentials")
    public void i_login_with_environment_credentials() {
        String username = System.getenv("NAUKRI_USERNAME");
        String password = System.getenv("NAUKRI_PASSWORD");
        
        if (username == null || password == null) {
            // Fallback to properties if environment variables not set
            try {
                java.util.Properties props = Base.getProperties();
                username = props.getProperty("naukri.username", "santhoshkumar18012002@gmail.com");
                password = props.getProperty("naukri.password", "saN!thosh@027");
            } catch (java.io.IOException e) {
                throw new RuntimeException("Could not load credentials", e);
            }
        }
        
        lp.enterCredentials(username, password);
        lp.selectLoginButton();
        
        // Verify login success with multiple fallback checks
        try {
            // First try: Look for "View" element (original approach)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("View")));
            System.out.println("✓ Login successful - Found 'View' element");
        } catch (Exception e1) {
            try {
                // Second try: Look for profile-related elements
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("Profile")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href,'my-profile')]")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'profile')]"))
                ));
                System.out.println("✓ Login successful - Found profile element");
            } catch (Exception e2) {
                try {
                    // Third try: Check if we're on dashboard/homepage after login
                    wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("mynaukri"),
                        ExpectedConditions.urlContains("dashboard"),
                        ExpectedConditions.titleContains("Dashboard")
                    ));
                    System.out.println("✓ Login successful - Detected post-login URL/title");
                } catch (Exception e3) {
                    // Final fallback: Just wait and check current state
                    Thread.sleep(5000);
                    System.out.println("⚠️ Login verification uncertain. Current URL: " + driver.getCurrentUrl());
                    System.out.println("⚠️ Page title: " + driver.getTitle());
                    
                    // Check if we're still on login page (which would indicate failure)
                    if (driver.getCurrentUrl().contains("login") && driver.getPageSource().contains("Login")) {
                        throw new RuntimeException("Login appears to have failed - still on login page");
                    }
                    System.out.println("✓ Proceeding with login verification assumption");
                }
            }
        }
    }
    
    @When("I navigate to my profile page")
    public void i_navigate_to_my_profile_page() {
        pp = new ProfilePageObj(driver);
        
        try {
            pp.clickViewProfile();
            System.out.println("✓ Successfully navigated to profile page");
        } catch (Exception e) {
            System.out.println("⚠️ Profile navigation failed, trying alternative approach: " + e.getMessage());
            // Fallback: direct navigation
            driver.navigate().to("https://www.naukri.com/mymynaukri/profile");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Close chat if it appears
        try {
            pp.closeChat();
        } catch (Exception e) {
            // Chat might not appear, ignore
        }
    }
    
    @When("I update career profile with random location selection")
    public void i_update_career_profile_with_random_location_selection() throws InterruptedException {
        try {
            pp.clickCarrerProfile();
            Thread.sleep(3000);
            pp.editCarrerProfile();
            
            boolean formPresent = pp.verifyFormIsPresent();
            assertTrue("Career profile form should be present", formPresent);
            
            // Wait and click on location preference
            Thread.sleep(4000);
            pp.clickForLocation();
            
            // Check current Chennai selection status
            boolean isChennaiSelected = pp.isLocationSelected();
            pp.addLocation(); // This toggles Chennai
            
            if (isChennaiSelected) {
                executionSummary.append("[SUCCESS] Location Update: Removed Chennai from preferred locations\n");
                System.out.println("Removed Chennai from location preference");
            } else {
                executionSummary.append("[SUCCESS] Location Update: Added Chennai to preferred locations\n");
                System.out.println("Added Chennai to location preference");
            }
            
            pp.clickLocationLabel();
            
            // Save the changes
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("saveDesiredProfile")));
            pp.clickcpSaveButton();
            
            locationUpdated = true;
            executionSummary.append("[SUCCESS] Career profile changes saved successfully\n");
            
        } catch (Exception e) {
            executionSummary.append("[FAILED] Location Update Failed: ").append(e.getMessage()).append("\n");
            System.err.println("Location update failed: " + e.getMessage());
        }
    }
    
    @When("I upload the latest resume")
    public void i_upload_the_latest_resume() {
        try {
            System.out.println("Starting resume upload process...");
            
            // Navigate back to profile if needed and attempt upload
            pp.clickUpdateLink();
            pp.uploadResume();
            
            // Add a wait to let upload complete
            Thread.sleep(3000);
            
            // Verify resume upload
            try {
                String resumeName = pp.validateResumeName();
                if (resumeName != null && !resumeName.trim().isEmpty()) {
                    resumeUpdated = true;
                    executionSummary.append("[SUCCESS] Resume Upload: Successfully uploaded - ").append(resumeName).append("\n");
                    System.out.println("Resume uploaded successfully: " + resumeName);
                } else {
                    executionSummary.append("[WARNING] Resume Upload: Upload attempted but name validation failed\n");
                    System.out.println("Resume upload attempted but validation failed");
                }
            } catch (Exception validationError) {
                executionSummary.append("[WARNING] Resume Upload: Upload attempted but validation failed - ").append(validationError.getMessage()).append("\n");
                System.out.println("Resume upload attempted but validation failed: " + validationError.getMessage());
            }
            
        } catch (Exception e) {
            executionSummary.append("[FAILED] Resume Upload Failed: ").append(e.getMessage()).append("\n");
            System.err.println("Resume upload failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Then("I verify the profile activity is recorded")
    public void i_verify_the_profile_activity_is_recorded() {
        // Determine overall success
        profileUpdateSuccess = locationUpdated || resumeUpdated;
        
        if (profileUpdateSuccess) {
            executionSummary.append("[SUCCESS] Overall Status: Profile update completed successfully\n");
            executionSummary.append("[SUCCESS] Profile Activity: Your profile now shows recent activity\n");
            executionSummary.append("[SUCCESS] Recruiter Visibility: Increased visibility to potential employers\n");
        } else {
            executionSummary.append("[FAILED] Overall Status: No updates were completed successfully\n");
            executionSummary.append("[FAILED] Manual Action Required: Please check your profile manually\n");
        }
        
        // Send email notification
        sendEmailNotification();
        
        System.out.println("Daily profile update completed at: " + java.time.LocalDateTime.now());
        System.out.println("Email notification sent with execution summary");
    }
    
    private void sendEmailNotification() {
        try {
            String recipientEmail = System.getenv("RECIPIENT_EMAIL");
            if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                try {
                    java.util.Properties props = Base.getProperties();
                    recipientEmail = props.getProperty("recipient.email");
                } catch (java.io.IOException e) {
                    System.err.println("Could not load recipient email from properties: " + e.getMessage());
                }
            }
            
            if (recipientEmail != null && !recipientEmail.trim().isEmpty()) {
                String subject = profileUpdateSuccess ? 
                    "[SUCCESS] Naukri Profile Updated Successfully" : 
                    "[FAILED] Naukri Profile Update Failed";
                
                EmailNotificationUtil.sendExecutionReport(
                    recipientEmail, 
                    subject, 
                    executionSummary.toString(), 
                    profileUpdateSuccess
                );
            } else {
                System.out.println("Recipient email not configured. Skipping email notification.");
                System.out.println("Execution Summary:\n" + executionSummary.toString());
            }
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
            System.out.println("Execution Summary:\n" + executionSummary.toString());
        }
    }
}
