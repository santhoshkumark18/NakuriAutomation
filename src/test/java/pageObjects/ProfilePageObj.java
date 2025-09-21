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

	public void clickViewProfile() {
		try {
			if(view_profile_button.isDisplayed()) {
				view_profile_button.click();
				System.out.println("✓ Clicked primary 'View' profile button");
			}
		} catch (Exception e) {
			try {
				view_profile_fallback.click();
				System.out.println("✓ Clicked fallback profile button");
			} catch (Exception e2) {
				// Try direct navigation to profile page
				driver.navigate().to("https://www.naukri.com/mymynaukri/profile");
				System.out.println("✓ Navigated directly to profile page");
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
			} catch (Exception e) {
				System.err.println("Could not find file input element: " + e.getMessage());
				// Alternative approach - sometimes file input is hidden
				driver.findElement(org.openqa.selenium.By.xpath("//input[@type='file']")).sendKeys(fullPath);
				System.out.println("File path sent using alternative method");
			}
			
			// Wait for upload to complete
			Thread.sleep(8000);
			System.out.println("Resume upload completed");
			
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
					uploaded = true;
					System.out.println("Fallback upload successful with selector: " + selector);
					break;
				} catch (Exception e) {
					System.out.println("Selector failed: " + selector);
				}
			}
			
			if (!uploaded) {
				System.err.println("All file upload methods failed");
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

}
