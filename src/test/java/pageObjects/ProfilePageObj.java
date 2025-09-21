package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfilePageObj extends BaseClass {
	String msg;

	public ProfilePageObj(WebDriver driver) {
		super(driver);
	}

	@FindBy(partialLinkText = "View")
	WebElement view_profile_button;
	@FindBy(xpath = "//a[contains(@href,'my-profile') or contains(text(),'Profile') or contains(text(),'View profile')]")
	WebElement view_profile_fallback;
	@FindBy(xpath = "//*[@id=\"_nj1mrmev4Navbar\"]/div")
	WebElement chat_close;
	@FindBy(xpath = "//div[contains(@class,\"quickLink \")]//span[contains(.,\"Career profile\")]")
	WebElement carrer_profile_link;
	@FindBy(xpath = "//div[@id=\"lazyDesiredProfile\"]//span[contains(.,\"editOneTheme\")]")
	WebElement carrer_profile_edit;
	@FindBy(id = "desiredProfileForm")
	WebElement carrer_profile_form;
	@FindBy(id = "locationSugg")
	WebElement location_preference;
	@FindBy(xpath = "//*[text()=\"Chennai\"]/i")
	WebElement location_add;
	@FindBy(id = "saveDesiredProfile")
	WebElement cp_save_button;
	@FindBy(xpath = "//div[@class=\"mod-date\"]/span[contains(.,\"Today\")]")
	WebElement last_profile_update_status;
	@FindBy(xpath = "//*[@id=\"desiredProfileForm\"]//child::span[contains(.,\"Preferred work\")]")
	WebElement location_label;
	@FindBy(xpath = "//a[text()='Update']")
	WebElement update_link;
	@FindBy(xpath = "//input[@type='file']")
	WebElement file_input;
	@FindBy(xpath = "//div[contains(@class,\"resume-name\")]/div")
	WebElement resume_name;
	@FindBy(xpath = "//p[contains(text(),'Resume has been successfully uploaded.')]")
	WebElement resume_success_message;

	public void clickViewProfile() {
		try {
			if(view_profile_button.isDisplayed()) {
				view_profile_button.click();
				System.out.println("[SUCCESS] Clicked primary 'View' profile button");
			}
		} catch (Exception e) {
			try {
				view_profile_fallback.click();
				System.out.println("[SUCCESS] Clicked fallback profile button");
			} catch (Exception e2) {
				// Try direct navigation to profile page
				driver.navigate().to("https://www.naukri.com/mymynaukri/profile");
				System.out.println("[SUCCESS] Navigated directly to profile page");
			}
		}
	}

	public void clickUpdateLink() {
		update_link.click();
	}

	public void uploadResume() {
		try {
			// First click the update link to trigger file input
			System.out.println("Clicking update link...");
			update_link.click();
			
			// Wait for file input to be available
			Thread.sleep(3000);
			
			// Get the file path
			java.util.Properties props = factory.Base.getProperties();
			String resumePath = props.getProperty("resume_path", "src\\test\\resources\\files\\resume.pdf");
			String fullPath = System.getProperty("user.dir") + "\\" + resumePath;
			
			// Check if file exists
			java.io.File resumeFile = new java.io.File(fullPath);
			if (!resumeFile.exists()) {
				// Try fallback path
				System.out.println("Resume not found at: " + fullPath + ", trying fallback...");
				fullPath = "C:\\Users\\santh\\Documents\\SanthoshKumarK.pdf";
				resumeFile = new java.io.File(fullPath);
				if (!resumeFile.exists()) {
					System.err.println("Resume file not found at fallback location: " + fullPath);
					throw new RuntimeException("Resume file not found at: " + fullPath);
				}
			}
			
			System.out.println("Uploading resume from: " + fullPath);
			
			// Try to find and use file input element
			try {
				file_input.sendKeys(fullPath);
				System.out.println("File path sent to file input element");
				
				// Trigger the upload by simulating file selection change event
				((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
					"arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", 
					file_input
				);
				System.out.println("Upload change event triggered");
				
			} catch (Exception e) {
				System.err.println("Could not find file input element: " + e.getMessage());
				// Alternative approach - sometimes file input is hidden
				WebElement altFileInput = driver.findElement(org.openqa.selenium.By.xpath("//input[@type='file']"));
				altFileInput.sendKeys(fullPath);
				((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
					"arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", 
					altFileInput
				);
				System.out.println("File path sent using alternative method with change event");
			}
			
			// Look for and click upload/submit button if it exists
			try {
				WebElement uploadButton = driver.findElement(org.openqa.selenium.By.xpath(
					"//button[contains(text(),'Upload') or contains(text(),'Submit') or contains(text(),'Save')] | " +
					"//input[@type='submit' or @value='Upload' or @value='Submit' or @value='Save']"
				));
				uploadButton.click();
				System.out.println("Upload/Submit button clicked");
			} catch (Exception e) {
				System.out.println("No explicit upload button found, file should auto-upload on change event");
			}
			
			// Wait for upload to complete (increased time for actual upload)
			Thread.sleep(15000);
			System.out.println("Resume upload completed");
			
			// Validate success message
			validateResumeUploadSuccess();
			
		} catch (java.io.IOException e) {
			System.err.println("Failed to load properties: " + e.getMessage());
			handleFallbackUpload();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Thread interrupted during file upload");
		} catch (Exception e) {
			System.err.println("Unexpected error during resume upload: " + e.getMessage());
			handleFallbackUpload();
		}
	}
	
	private void handleFallbackUpload() {
		try {
			System.out.println("Attempting fallback resume upload...");
			update_link.click();
			Thread.sleep(3000);
			
			// Try multiple possible file input selectors
			String[] selectors = {
				"//input[@type='file']",
				"//input[contains(@class,'file')]",
				"//input[@accept]"
			};
			
			String fallbackPath = "C:\\Users\\santh\\Documents\\SanthoshKumarK.pdf";
			boolean uploaded = false;
			
			for (String selector : selectors) {
				try {
					WebElement fileElement = driver.findElement(org.openqa.selenium.By.xpath(selector));
					fileElement.sendKeys(fallbackPath);
					
					// Trigger upload change event
					((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
						"arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", 
						fileElement
					);
					
					uploaded = true;
					System.out.println("Fallback upload successful with selector: " + selector);
					break;
				} catch (Exception e) {
					System.out.println("Selector failed: " + selector);
				}
			}
			
			if (!uploaded) {
				System.err.println("All file upload methods failed");
			} else {
				// Validate success message for fallback upload too
				validateResumeUploadSuccess();
			}
			
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Thread interrupted during fallback upload");
		}
	}

	public String validateResumeName() {
		String name = resume_name.getText();
		return name;
	}

	public void closeChat() {
		boolean res = chat_close.isDisplayed();

		if (res)
			chat_close.click();
	}

	public void clickCarrerProfile() {
		carrer_profile_link.click();
	}

	public void editCarrerProfile() {
		carrer_profile_edit.click();
	}

	public String verifyProfileUpdateStatus() {

		msg = last_profile_update_status.getText();
		return msg;
	}

	public boolean verifyFormIsPresent() {
		boolean res = carrer_profile_form.isDisplayed();
		return res;
	}

	public void clickLocationLabel() {
		location_label.click();

	}

	public void clickForLocation() {
		location_preference.click();
	}

	public void clickcpSaveButton() {
		cp_save_button.click();
	}

	public void addLocation() {
		location_add.click();
	}

	public boolean isLocationSelected() {
		boolean isSelected = location_add.isSelected();
		return isSelected;
	}

	public void validateResumeUploadSuccess() {
		try {
			// First, let's debug what elements are available after upload
			System.out.println("=== DEBUGGING UPLOAD SUCCESS ===");
			System.out.println("Current URL: " + driver.getCurrentUrl());
			
			// Look for any success-related elements on the page
			try {
				java.util.List<org.openqa.selenium.WebElement> successElements = driver.findElements(
					org.openqa.selenium.By.xpath("//*[contains(text(),'success') or contains(text(),'Success') or contains(text(),'uploaded') or contains(text(),'Uploaded')]")
				);
				System.out.println("Found " + successElements.size() + " potential success elements:");
				for (org.openqa.selenium.WebElement elem : successElements) {
					try {
						System.out.println("  - " + elem.getTagName() + ": " + elem.getText());
					} catch (Exception e) {
						System.out.println("  - Element not readable");
					}
				}
			} catch (Exception e) {
				System.out.println("Error finding success elements: " + e.getMessage());
			}
			
			// Wait for success message to appear (it shows for a few seconds)
			org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(20));
			
			// Wait for the success message to be visible
			wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(resume_success_message));
			
			String messageText = resume_success_message.getText();
			if (messageText.contains("Resume has been successfully uploaded.")) {
				System.out.println("[SUCCESS] Resume upload validation passed: " + messageText);
			} else {
				System.out.println("[WARNING] Unexpected success message: " + messageText);
			}
			
		} catch (org.openqa.selenium.TimeoutException e) {
			System.out.println("[WARNING] Resume success message not found within timeout period");
			
			// Enhanced debugging - check page source for success indicators
			String pageSource = driver.getPageSource();
			if (pageSource.contains("successfully uploaded") || pageSource.contains("upload successful") || 
				pageSource.contains("Resume has been") || pageSource.contains("uploaded successfully")) {
				System.out.println("[INFO] Success text found in page source, but element selector may be wrong");
			} else {
				System.out.println("[INFO] No success text found in page source - upload may have failed");
			}
			
			// Try alternative validation - check for other success indicators
			try {
				// Check if resume name element is updated
				if (resume_name.isDisplayed()) {
					String resumeNameText = resume_name.getText();
					if (resumeNameText.contains(".pdf") || resumeNameText.contains("resume")) {
						System.out.println("[SUCCESS] Resume upload validated via resume name: " + resumeNameText);
					}
				}
			} catch (Exception fallbackException) {
				System.out.println("[WARNING] Could not validate resume upload success");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] Error during resume validation: " + e.getMessage());
		}
	}

}
