package factory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Base {

	 static WebDriver driver;
     static Properties p;
  	     
public static WebDriver initilizeBrowser() throws IOException
{
	p = getProperties();
    String executionEnv = p.getProperty("execution_env");
    String browser = p.getProperty("browser").toLowerCase();
    String os = p.getProperty("os").toLowerCase();
	
	if(executionEnv.equalsIgnoreCase("remote"))
	{
		DesiredCapabilities capabilities = new DesiredCapabilities();
		
		//os
		 switch (os) {
         case "windows":
             capabilities.setPlatform(Platform.WINDOWS);
             break;
         case "mac":
             capabilities.setPlatform(Platform.MAC);
             break;
         case "linux":
             capabilities.setPlatform(Platform.LINUX);
             break;
         default:
             System.out.println("No matching OS");
             return null;
        }
		
		//browser
		 switch (browser) {
         case "chrome":
        	 ChromeOptions op=new ChromeOptions();// 3 lines to skip the session not created exception
        	 op.addArguments("--no-sandbox");
        	 op.addArguments("--disable-dev-shm-usage");
        	 if("true".equalsIgnoreCase(p.getProperty("headless"))) {
        		 op.addArguments("--headless");
        	 }
             capabilities.setBrowserName("chrome");
             break;
         case "edge":
        	 //WebDriverManager.edgedriver().setup();
             capabilities.setBrowserName("MicrosoftEdge");
             break;
         case "firefox":
             capabilities.setBrowserName("firefox");
             break;
         default:
             System.out.println("No matching browser");
             return null;
         }
       
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),capabilities);
		
	}
	else if(executionEnv.equalsIgnoreCase("local"))
		{
			switch(browser.toLowerCase()) 
			{
			case "chrome":
				WebDriverManager.chromedriver().setup();
				ChromeOptions chromeOptions = new ChromeOptions();
				
				// Anti-detection measures
				chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36");
				chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
				chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
				chromeOptions.setExperimentalOption("useAutomationExtension", false);
				chromeOptions.addArguments("--disable-web-security");
				chromeOptions.addArguments("--allow-running-insecure-content");
				chromeOptions.addArguments("--disable-extensions");
				chromeOptions.addArguments("--disable-plugins");
				chromeOptions.addArguments("--no-first-run");
				chromeOptions.addArguments("--disable-default-apps");
				chromeOptions.addArguments("--disable-popup-blocking");
				
				if("true".equalsIgnoreCase(p.getProperty("headless"))) {
					chromeOptions.addArguments("--headless=new");
					chromeOptions.addArguments("--no-sandbox");
					chromeOptions.addArguments("--disable-dev-shm-usage");
					chromeOptions.addArguments("--disable-gpu");
					chromeOptions.addArguments("--window-size=1920,1080");
					chromeOptions.addArguments("--remote-debugging-port=9222");
				} else {
					chromeOptions.addArguments("--start-maximized");
				}
				
		        driver=new ChromeDriver(chromeOptions);
		        
		        // Execute anti-detection script
		        JavascriptExecutor js = (JavascriptExecutor) driver;
		        js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
		        js.executeScript("Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3, 4, 5]})");
		        js.executeScript("Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en']})");
		        js.executeScript("window.chrome = { runtime: {} }");
		        
		        break;
		    case "edge":
		    	WebDriverManager.edgedriver().setup(); //wbmanager installs the compatible version of browser with driver and runs
		    	driver=new EdgeDriver();
		        break;
		    case "firefox":
		    	WebDriverManager.firefoxdriver().setup();
		    	driver=new FirefoxDriver();
		        break;
		    default:
		        System.out.println("No matching browser");
		        driver=null;
			}
		}
	 driver.manage().deleteAllCookies(); 
	 driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	 //driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
	 
	 return driver;
	 
}

public static WebDriver getDriver() {
		return driver;
	}

public static Properties getProperties() throws IOException
{		 
    FileReader file=new FileReader(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+"config.properties");
   	p=new Properties();
	p.load(file);
	return p;
}
}
