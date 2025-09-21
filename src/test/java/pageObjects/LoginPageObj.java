package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPageObj extends BaseClass{
	String msg;
	public LoginPageObj(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(id="login_Layer")	WebElement	login; 
	@FindBy(xpath="//a[contains(@class,'login') or contains(text(),'Login') or contains(@title,'Login')]")	WebElement	loginFallback;
	@FindBy(xpath="//*[contains(@placeholder,\"Username\")]")	WebElement	username;
	@FindBy(xpath="//*[contains(@placeholder,\"password\")]")	WebElement	password;
	@FindBy(xpath="//button[.='Login']")	WebElement	login_btn;
	@FindBy(xpath="//div[contains(@class,\"server-err\")]")	WebElement	alert_msg;
	@FindBy(xpath="//img[@alt=\"Naukri Logo\"][1]")	WebElement	logo;
	
	
	public void selectLogin()
	{
		// Since we're going directly to login page, check if login form is already visible
		try {
			if(username.isDisplayed()) {
				System.out.println("[SUCCESS] Login form is already visible - ready for credentials");
				return;
			}
		} catch (Exception e) {
			// Username field not visible yet, might need to wait or click login
			System.out.println("Login form not immediately visible, attempting to locate login button...");
		}
		
		try {
			// Try primary login element
			if(login.isDisplayed()) {
				System.out.println("[SUCCESS] Found and clicking primary login element");
				login.click();
			}
		} catch (Exception e) {
			try {
				// Fallback to alternative login selector
				System.out.println("Primary login element not found, trying fallback selector...");
				loginFallback.click();
				System.out.println("[SUCCESS] Successfully clicked fallback login element");
			} catch (Exception e2) {
				// Final fallback - check if we're already authenticated
				try {
					if(driver.getCurrentUrl().contains("mymynaukri") || driver.getPageSource().contains("Welcome")) {
						System.out.println("[SUCCESS] Already logged in, skipping login step");
						return;
					}
				} catch (Exception e3) {
					// Log detailed debugging info
					System.out.println("[ERROR] All login attempts failed");
					System.out.println("Current URL: " + driver.getCurrentUrl());
					System.out.println("Page title: " + driver.getTitle());
					System.out.println("Page source length: " + driver.getPageSource().length());
					throw new RuntimeException("Unable to locate login element. Current URL: " + driver.getCurrentUrl());
				}
			}
		}
	}
	public void enterCredentials(String un, String pass)
	{
		username.sendKeys(un);
		password.sendKeys(pass);
	}
	public void selectLoginButton()
	{
		login_btn.click();
	}
	public String verifyAlertMessage()
	{
		boolean res=alert_msg.isDisplayed();
		if(res)
		{
			msg=alert_msg.getText();
		}
		return msg;
	}
	public boolean verifyLogoIsPresent()
	{
		boolean res=logo.isDisplayed();
		return res;
	}
}
