package lib.selenium;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


import lib.utils.HTMLReporter;

public class WebDriverServiceImpl extends HTMLReporter implements WebDriverService {

	public static RemoteWebDriver driver;
	public ChromeOptions options;
	public Properties prop;

	public String environment = System.getenv("TEST_ENV") != null ? System.getenv("TEST_ENV") : "local";
	//public String environment ="jenkins";
	//public String environment = "local";
	// public String environment = "AWS";

	public WebDriverServiceImpl() {
		prop = new Properties();
		try {
			if (environment.equals("local")) {
				prop.load(new FileInputStream(
						new File(System.getProperty("user.dir") + "./src/test/resources/locators.properties")));
			} else if (environment.equals("jenkins")) {
				prop.load(new FileInputStream(
						new File(System.getProperty("user.dir") + "/src/test/resources/locators.properties")));
				
			}
			else {
				prop.load(
						new FileInputStream(new File(System.getProperty("user.dir") + "classes//locators.properties")));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startApp(String browser) {
		try {
			if (environment.equals("local")) {
				if (browser.equalsIgnoreCase("chrome")) {
					System.out.println("inside start app chrome");
					System.setProperty("webdriver.chrome.driver", "./driver/chromedriver.exe");
					ChromeOptions op = new ChromeOptions();
//					op.addArguments("disable-infobars");
					downloadFile();
					driver = new ChromeDriver(options);
				} else if (browser.equalsIgnoreCase("firefox")) {
					System.setProperty("webdriver.gecko.driver", "./driver/geckodriver.exe");
					driver = new FirefoxDriver();
				}
				
			} else if(environment.equals("jenkins")) {
				if (browser.equalsIgnoreCase("chrome")) {
					System.out.println("inside jenkins chrome setup");
//					System.out.println("inside start app chrome Linux");
//					System.out.println(System.getProperty("user.dir") + "/driver/chromedriverLinux");
//					System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/driver/chromedriverLinux");
//					
//					ChromeOptions op = new ChromeOptions();
////					op.addArguments("disable-infobars");
//					downloadFile();
//					driver = new ChromeDriver(options);
					
					String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
					System.out.println("chome path"+chromeDriverPath);
					if (chromeDriverPath == null || chromeDriverPath.isEmpty()) {
						System.out.println("driver path is empty");
					    chromeDriverPath =  System.getProperty("user.dir") + "/driver/chromedriverLinux"; // fallback for local dev
					}
					System.setProperty("webdriver.chrome.driver", chromeDriverPath);
					
					options = new ChromeOptions();
					options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");

					// Optional prefs setup, if you have a downloadFile() method that sets prefs
					downloadFile(); // This modifies `this.options`

					System.out.println("ChromeOptions is null? " + (options == null));
					driver = new ChromeDriver(options);
					
				} else if (browser.equalsIgnoreCase("firefox")) {
					System.out.println(System.getProperty("user.dir") +  "/driver/geckodriverLinux");
					System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir") +  "/driver/geckodriverLinux");
					driver = new FirefoxDriver();
				}
					
				}
				else {
					FirefoxBinary firefoxBinary = new FirefoxBinary();
					firefoxBinary.addCommandLineOptions("--headless");
					System.setProperty("webdriver.firefox.driver", "./driver/firefoxdriver.exe");
					FirefoxOptions firefoxOptions = new FirefoxOptions();
					firefoxOptions.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, false);
					firefoxOptions.setBinary(firefoxBinary);
					driver = new FirefoxDriver(firefoxOptions);
				}		
			
			
			
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			System.out.println("Test Started");
			reportStep("[" + browser + "] Launched Successfully", "PASS");
			
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void downloadFile() {
		String fileDownloadPath;
		if (environment.equals("local")) {
			fileDownloadPath = System.getProperty("user.dir") + "\\src\\test\\java\\downloadFiles";
		} else if(environment.equals("jenkins")){
			fileDownloadPath = Paths.get(System.getProperty("user.dir"), "src", "test", "java", "downloadFiles").toString();
		}else {
			fileDownloadPath = "classes//downloadFiles";
		}
		Map<String, Object> prefsMap = new HashMap<String, Object>();
		prefsMap.put("profile.default_content_settings.popups", 0);
		prefsMap.put("download.default_directory", fileDownloadPath);

		options = new ChromeOptions();
		options.setExperimentalOption("prefs", prefsMap);
		options.addArguments("--test-type");
		options.addArguments("--disable-extensions");
		
		if (!environment.equals("local")) {
		    options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
		}

//	    options.addArguments("--incognito");
	}

	public void loadUrl(String url) {
		try {
			String appURL = prop.getProperty(url);
			driver.get(appURL);
			System.out.println(prop.getProperty(url));
			reportStep("[" + appURL + "] Launched Successfully", "PASS");
		} catch (Exception e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public WebElement locateElement(String locator, String locValue) {
		try {
			switch (locator) {
			case "id":
				return driver.findElementById(prop.getProperty(locValue));
			case "name":
				return driver.findElementByName(prop.getProperty(locValue));
			case "class":
				return driver.findElementByClassName(prop.getProperty(locValue));
			case "link":
				return driver.findElementByLinkText(prop.getProperty(locValue));
			case "xpath":
				return driver.findElementByXPath(prop.getProperty(locValue));
			case "tagname":
				return driver.findElementByTagName(prop.getProperty(locValue));
			default:
				break;
			}
		} catch (NoSuchElementException e) {
			reportStep("Element with Locator Value : " + prop.getProperty(locValue) + " Not Found", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return null;
	}

	public WebElement locateElementWithoutProp(String locator, String locValue) {
		try {
			switch (locator) {
			case "id":
				return driver.findElementById(locValue);
			case "name":
				return driver.findElementByName(locValue);
			case "class":
				return driver.findElementByClassName(locValue);
			case "link":
				return driver.findElementByLinkText(locValue);
			case "xpath":
				return driver.findElementByXPath(locValue);
			case "tagname":
				return driver.findElementByTagName(locValue);
			default:
				break;
			}
			
		} catch (NoSuchElementException e) {
			reportStep("Element with Locator : " + prop.getProperty(locValue) + " Not Found", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return null;
	}
	
	public WebElement locateElementXpath(String locValue) {
		try {
			return driver.findElementByXPath(prop.getProperty(locValue));
		} catch (NoSuchElementException e) {
			reportStep("Element with Locator [" + locValue + "] Not Found", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return null;

	}

	public List<WebElement> locateElements(String locator, String locValue) {
		try {
			switch (locator) {
			case "id":
				return driver.findElementsById(prop.getProperty(locValue));
			case "name":
				return driver.findElementsByName(prop.getProperty(locValue));
			case "class":
				return driver.findElementsByClassName(prop.getProperty(locValue));
			case "link":
				return driver.findElementsByLinkText(prop.getProperty(locValue));
			case "xpath":
				return driver.findElementsByXPath(prop.getProperty(locValue));
			case "tagname":
				return driver.findElementsByTagName(prop.getProperty(locValue));
			default:
				break;
			}
		} catch (NoSuchElementException e) {
			reportStep("Element with Locator [" + locValue + "] Not Found", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return null;
	}

	public void type(WebElement ele, String data) {
		try {
			ele.clear();
			ele.sendKeys(data);
			reportStep("[" + data + "] Entered Successfully", "PASS");
		} catch (InvalidElementStateException e) {
			reportStep("[" + data + "] Couldn't Enter", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void typeValue(WebElement ele, String data) {
		try {
			ele.clear();
			ele.sendKeys(data, Keys.ENTER);
			reportStep("[" + data + "] Entered Successfully", "PASS");
		} catch (InvalidElementStateException e) {
			reportStep("[" + data + "] Couldn't Enter", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void typeValueWithTab(WebElement ele, String data) {
		try {
			ele.clear();
			ele.sendKeys(data, Keys.TAB);
			reportStep("[" + data + "] Entered Successfully", "PASS");
		} catch (InvalidElementStateException e) {
			reportStep("[" + data + "] Couldn't Enter", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException: " + e, "FAIL");
		}
	}

	public void click(WebElement element) {
		String elementTxt = "";
		try {
			moveToElement(element);
			if (!element.getText().equals("")) {
				elementTxt = element.getText();
			} else {
				elementTxt = element.getAttribute("value");
			} 
			element.click();
			Thread.sleep(500);
			reportStep("[" + elementTxt + "] is Clicked", "PASS");
		} catch (InvalidElementStateException e) {
			reportStep("InvalidElementStateException : " + e, "FAIL");
		} catch (Exception e) {
			System.out.println("Click catch"+e);
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}
	
	public void click(WebElement element, String value) {
		try {
			moveToElement(element);
			element.click();
			Thread.sleep(500);
			reportStep("[" + value + "] is Clicked", "PASS");
		} catch (InvalidElementStateException e) {
			reportStep("InvalidElementStateException : " + e, "FAIL");
		} catch (Exception e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void moveToElement(WebElement ele) {
		String text = "";
		try {
			Actions builder = new Actions(driver);
			builder.moveToElement(ele).pause(2000).perform();
			text = ele.getText();
		} catch (InvalidElementStateException e) {
			reportStep("Couldn't Move to the Element : " + text, "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void jsClick(WebElement ele) {
		String text = "";
		try {
			text = ele.getText();
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", ele);
			reportStep("[" + text + "] is Clicked", "PASS");
		} catch (InvalidElementStateException e) {
			reportStep("[" + text + "] Couldn't Click", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException: " + e, "FAIL");
		}
	}

	public void jsClickWithText(WebElement ele,String text) {
		try {
			//text = ele.getText();
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", ele);
			reportStep("[" + text + "] is Clicked", "PASS");
		} catch (InvalidElementStateException e) {
			reportStep("[" + text + "] Couldn't Click", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException: " + e, "FAIL");
		}
	}

	public void clickWithNoSnap(WebElement ele) {
		String text = "";
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.elementToBeClickable(ele));
			text = ele.getText();
			ele.click();
			reportStep("[" + text + "]  is Clicked", "PASS", false);
		} catch (InvalidElementStateException e) {
			reportStep("[" + text + "] Couldn't Click", "FAIL", false);
		} catch (WebDriverException e) {
			reportStep("WebDriverException: " + e, "FAIL");
		}
	}

	public void dragAndDrop(WebElement ele1, WebElement ele2) {
		try {
			Actions builder = new Actions(driver);
			builder.dragAndDrop(ele1, ele2).perform();
			reportStep("Element Dropped Successfully", "PASS");
		} catch (InvalidElementStateException e) {
			reportStep("Element is Not Dropped", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void clickAndHold(WebElement ele1, WebElement ele2) {
		try {
			Actions builder = new Actions(driver);
			builder.clickAndHold(ele1).release(ele2).perform();
			reportStep("Element Dropped Successfully", "PASS");
		} catch (InvalidElementStateException e) {
			reportStep("Element is Not Dropped", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public String getText(WebElement ele) {
		String bReturn = "";
		try {
			bReturn = ele.getText().trim();
			reportStep("Element Text is [" + ele + "]", "PASS");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return bReturn;
	}

	public String getTitle() {
		String bReturn = "";
		try {
			bReturn = driver.getTitle();
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return bReturn;
	}

	public String getAttribute(WebElement ele, String attribute) {
		String bReturn = "";
		try {
			bReturn = ele.getAttribute(attribute);
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return bReturn;
	}
	
	public void selectDropDownUsingText(WebElement locator, String text) throws Exception {
		try {
			if (!text.equalsIgnoreCase("N/A")) {
				moveToElement(locator);
				new Select(locator).selectByVisibleText(text.trim());
				reportStep("Dropdown [" + locator + "] is Selected with Text : " + text, "PASS");
				Thread.sleep(500);
				if (isAlertPresent() == true) {
					acceptAlert();
				}
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}
		
	public void selectDropDownUsingValue(WebElement locator, String value) {
		try {
			if (!value.equalsIgnoreCase("N/A")) {
				moveToElement(locator);
				new Select(locator).selectByValue(value);
				reportStep("Dropdown [" + locator + "] is Selected with Value : " + value, "PASS");
				if (isAlertPresent() == true) {
					acceptAlert();
				}
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}
	
	public void selectDropDownUsingIndex(WebElement ele, int index) {
		try {
			new Select(ele).selectByIndex(index);
			reportStep("Dropdown [" + ele + "] is Selected with Index : " + index, "PASS");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}

	}

	public boolean verifyExactTitle(String title) {
		boolean bReturn = false;
		try {
			if (getTitle().equals(title)) {
				reportStep("Title : " + driver.getTitle() + " MATCHES [" + title + "]", "PASS");
				bReturn = true;
			} else {
				reportStep("Title : " + driver.getTitle() + " MISMATCHED [" + title + "]", "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return bReturn;
	}

	public void verifyExactText(WebElement ele, String expectedText) {
		try {
			if (ele.getText().trim().equalsIgnoreCase(expectedText)) {
				reportStep("Text : [" + ele.getText() + "] MATCHES [" + expectedText + "]", "PASS");
			} else {
				reportStep("Text : [" + ele.getText() + "] MISMATCHED [" + expectedText + "]", "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public boolean verifyTwoStringValues(String actualText, String expectedText) {
		boolean bool = false;
		try {
			if (actualText.equals(expectedText)) {
				bool = true;
				reportStep("Text : [" + actualText + "] MATCHES [" + expectedText + "]", "PASS");
			} else {
				bool = false;
				reportStep("Text : [" + actualText + "] MISMATCHED [" + expectedText + "]", "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return bool;
	}

	public String verifyTwoStringPartialValues(String expectedText, String actualText) {
		try {
			if (expectedText.contains(actualText)) {
				reportStep("Text :[" + expectedText + "] MATCHES [" + actualText + "]", "PASS");
			} else {
				reportStep("Text : [" + expectedText + "] MISMATCHED [" + expectedText + "]", "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return expectedText;
	}

	public void verifyPartialText(WebElement ele, String expectedText) {
		try {
			String text = ele.getText();
			System.out.println("Expected Text is: " + expectedText + "  ||  " + "Actual text is: " + text);

			if (text.contains(expectedText)) {
				reportStep("Text : [" + text + "] CONTAINS [" + expectedText + "]", "PASS");
			} else {
				reportStep("Text : [" + text + "] NOT CONTAINED [" + expectedText + "]", "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void verifyAttributeValue(WebElement ele, String expectedText, String attribute) {

		try {
			String text = ele.getAttribute(attribute);
			if (text.contains(expectedText)) {
				reportStep("Attribute : [" + expectedText + "] CONTAINS [" + text + "]", "PASS");
			} else {
				reportStep("Attribute : [" + expectedText + "] NOT CONTAINED [" + text + "]", "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void verifyExactAttribute(WebElement ele, String attribute, String value) {
		try {
			if (getAttribute(ele, attribute).equals(value)) {
				reportStep("Attribute : [" + attribute + "] MATCHES [" + value + "]", "PASS");
			} else {
				reportStep("Attribute : [" + attribute + "] MISMATCHED [" + value + "]", "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}

	}

	public void verifyPartialAttribute(WebElement ele, String attribute, String value) {
		try {
			if (getAttribute(ele, attribute).contains(value)) {
				reportStep("Attribute : [" + attribute + "] CONTAINS [" + value + "]", "PASS");
			} else {
				reportStep("Attribute : [" + attribute + "] NOT CONTAINED [" + value + "]", "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void verifySelected(WebElement ele) {
		try {
			if (ele.isSelected()) {
				reportStep("[" + ele + "] is Selected", "PASS");
			} else {
				reportStep("[" + ele + "] is Not Selected", "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void verifyDisplayed(WebElement ele, String text) {
		try {
			if (ele.isDisplayed()) {
				reportStep("[" + text + "] is Displayed", "PASS");
			} else {
				reportStep("[" + text + "] is Not Displayed ", "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public static Set<String> allWindowHandles;

	public void switchToWindow(int index) {
		try {
			allWindowHandles = driver.getWindowHandles();
			List<String> allHandles = new ArrayList<>();
			System.out.println("Opened Window Count : " + allWindowHandles.size());
			allHandles.addAll(allWindowHandles);
			driver.switchTo().window(allHandles.get(index));
		} catch (NoSuchWindowException e) {
			reportStep("Driver Couldn't Ove to the Given Window by Index " + index, "FAIL");
		} catch (IndexOutOfBoundsException e) {
			reportStep("IndexOutOfBoundsException Occured : " + index, "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void switchToFrame(WebElement ele) {
		try {
			driver.switchTo().frame(ele);
			reportStep("Switched In to the Frame [" + ele + "]", "PASS");
		} catch (NoSuchFrameException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void switchToFrame(int index) {
		try {
			driver.switchTo().frame(index);
			reportStep("Switched In to the Frame [" + index + "]", "PASS");
		} catch (NoSuchFrameException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void switchToFrame(String ele) {
		try {
			driver.switchTo().frame(ele);
			reportStep("Switched In to the Frame [" + ele + "]", "PASS");
		} catch (NoSuchFrameException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void defaultContent() {
		try {
			driver.switchTo().defaultContent();
			reportStep("Comeout of the Frame", "PASS");
		} catch (NoSuchFrameException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}
	
	public void acceptAlert() {
		try {
			Alert alert = driver.switchTo().alert();
			String text = alert.getText();
			alert.accept();
			reportStep("Alert [" + text + "] is Accepted", "PASS");
		} catch (NoAlertPresentException e) {
			reportStep("There is No Alert Present", "FAIL");
		} catch (UnhandledAlertException e) {
			reportStep("Unhandled Alert Exception : " + e, "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void accept2Alert() {
		try {
			Alert alert = driver.switchTo().alert();
			String text = alert.getText();
			alert.accept();
			alert.accept();
			reportStep("Alert [" + text + "] is Accepted", "PASS");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void dismissAlert() {
		String text = "";
		try {
			Alert alert = driver.switchTo().alert();
			text = alert.getText();
			alert.dismiss();
			reportStep("Alert [" + text + "] is Dismissed", "PASS");
		} catch (NoAlertPresentException e) {
			reportStep("There is No Alert Present", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public String getAlertText() {
		String text = "";
		try {
			Alert alert = driver.switchTo().alert();
			text = alert.getText();
		} catch (NoAlertPresentException e) {
			reportStep("There is No Alert Present", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return text;
	}

	public void scrollDown(String value) {
		try {
			WebElement reg = locateElement("xpath", value);
			int y = reg.getLocation().getY();
			System.out.println("Location of y :" + y);
			Actions builder = new Actions(driver);
			builder.sendKeys(Keys.PAGE_DOWN).build().perform();
			reportStep("Element Scrolled", "PASS");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}
 
	public void scrolldown() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	public void scrolltoview(String value) {
		try {
			JavascriptExecutor je = (JavascriptExecutor) driver;
			// Identify the WebElement which will appear after scrolling down
			WebElement element = locateElement("xpath", value);
			// now execute query which actually will scroll until that element is
			// not appeared on page.
			je.executeScript("arguments[0].scrollIntoView(true);", element);
			Thread.sleep(500);
			// reportStep("Element scrolled", "PASS");
		} catch (Exception e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void scrolltoviewAllElements(String value) {
		try {
			JavascriptExecutor je = (JavascriptExecutor) driver;
			// Identify the WebElement which will appear after scrolling down
			List<WebElement> element = locateElements("xpath", value);
			// now execute query which actually will scroll until that element is
			// not appeared on page.
			je.executeScript("arguments[0].scrollIntoView(true);", element);
			reportStep("Element scrolled", "PASS");
		} catch (Exception e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void scroll() {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("scroll(0, 650);");
	}

	public void scrollToTopOfPage() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, 0)");
	}

	public void threadSleep() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void waitUntilInvisibilityOfWebElement(String ele) throws InterruptedException {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.invisibilityOf(driver.findElementByXPath(prop.getProperty(ele))));
			reportStep("Element Invisible Successfully", "PASS");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void waitUntilVisibilityOfWebElement(String ele) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 60);
			wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(prop.getProperty(ele)))));
//			reportStep("Element Visibled Successfully", "PASS");
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void closeActiveBrowser() {
		try {
			driver.close();
			reportStep("Browser Has Been Closed", "PASS", false);
		} 
		catch (Exception e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	public void closeAllBrowsers() {
		try {
			driver.quit();
			reportStep("Browsers Has Been Closed", "PASS", false);
		} catch (Exception e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	long number;

	@Override
	public long takeSnap() {
		long number = (long) Math.floor(Math.random() * 900000000L) + 10000000L;
		try {
			if (environment.equals("local")) {
				// FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE) , new
				// File("./reports/images/"+number+".jpg"));
				FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE),
						new File("src\\main\\java\\reports\\images\\" + number + ".jpg"));
			} else {
				FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE),
						new File("classes//reports//images//" + number + ".jpg"));
			}
		} catch (WebDriverException e) {
			System.out.println("Browser Has Been Closed");
		} catch (IOException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return number;
	}

	public RemoteWebDriver cdriver;

	public long cdriverTakeSnap() {
		long number = (long) Math.floor(Math.random() * 900000000L) + 10000000L;
		try {
			if (environment.equals("local")) {
				// FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE) , new
				// File("./reports/images/"+number+".jpg"));
				FileUtils.copyFile(cdriver.getScreenshotAs(OutputType.FILE),
						new File("src\\main\\java\\reports\\images\\" + number + ".jpg"));
			} else {
				FileUtils.copyFile(cdriver.getScreenshotAs(OutputType.FILE),
						new File("classes//reports//images//" + number + ".jpg"));
			}
		} catch (WebDriverException e) {
			System.out.println("Browser Has Been Closed");
		} catch (IOException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return number;
	}

	public boolean verifyPartialTitle(String title) {
		boolean bReturn = false;
		try {
			if (getTitle().contains(title)) {
				reportStep("Title : Matches the Value :" + title, "PASS");
				bReturn = true;
			} else {
				reportStep("Title :" + driver.getTitle() + " Mismatched the Value :" + title, "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return bReturn;
	}

	public boolean verifyEnabled(WebElement ele) {
		boolean enabled = false;
		try {
			if (ele.isEnabled()) {
				enabled = true;
			} else {
				enabled = false;
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return enabled;
	}

	public boolean verifyClickable(WebElement ele) {
		boolean clickable = false;
		try {
			if (ExpectedConditions.elementToBeClickable(ele) != null) {
				clickable = true;
			} else {
				clickable = false;
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return clickable;
	}
	
	public void checkListOfElements(String locator, String expList, int subs, String splChar) {
		try {
			List<WebElement> elements = locateElements("xpath", locator);
			String listElement = "";

			for (WebElement element : elements) {
				//String ele=element.getText().replaceFirst("^\\s+", "");
				listElement = listElement + splChar + element.getText().trim();
			}
			verifyTwoStringValues(expList, listElement.substring(subs));
			System.out.println(expList);
			System.out.println(listElement.substring(subs));
		} catch (Exception e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}

	@SuppressWarnings("deprecation")
	public String readData(int coloum, int rows, String sheetname, String fileName) throws IOException {
		// String fileName = null;
		String data = null;
		Workbook wb = null;
		if (environment.equals("local")) {
			fileName = "./src/main/java/data/" + fileName + ".xlsx";
		} else {
			fileName = "classes//data//" + fileName + ".xlsx";
		}
		// System.err.println("File path is : "+fileName);
		FileInputStream fileInputStream = new FileInputStream(fileName);
		// fileInputStream.reset();
		String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
		// System.err.println("File Extension : "+fileExtensionName);
		if (fileExtensionName.equals(".xlsx")) {
			wb = new XSSFWorkbook(fileInputStream);
			XSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
		} else if (fileExtensionName.equals(".xls")) {
			wb = new HSSFWorkbook(fileInputStream);
			HSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
		}
		Sheet sheet = wb.getSheet(sheetname);
		Row row = sheet.getRow(rows);
		Cell cell = row.getCell(coloum);
		cell.setCellType(Cell.CELL_TYPE_STRING);

		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
//			System.out.println("String Value: " + cell.getStringCellValue());
			data = cell.getStringCellValue();
		}
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			System.out.println("numeric: " + cell.getNumericCellValue());
		}
//		System.out.println("Value from Sheet: " + cell.toString());
		data = cell.toString();
		return data;
	}

	@SuppressWarnings({ "null", "deprecation" })
	public void writeData(int coloum, int rows, String text, String sheetname, String fileName, String color)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		// String fileName =null;
		Workbook wb1 = null;
		Sheet sheet;
		Cell cell;
		Row row;
		CellStyle style = null;
		if (environment.equals("local")) {
			String rootPath = System.getProperty("user.dir");
			fileName = rootPath + "\\src\\main\\java\\data\\" + fileName + ".xlsx";
		} else {
			fileName = "classes//data//" + fileName + ".xlsx";
		}
		FileInputStream fileInputStream = new FileInputStream(fileName);
		String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
		if (fileExtensionName.equals(".xlsx")) {
			wb1 = new XSSFWorkbook(fileInputStream);
			XSSFFormulaEvaluator.evaluateAllFormulaCells(wb1);
			style = wb1.createCellStyle();

			Font font = wb1.createFont();
			if (color.equalsIgnoreCase("Green")) {
				font.setColor(IndexedColors.GREEN.getIndex());
			} else if (color.equalsIgnoreCase("Red")) {
				font.setColor(IndexedColors.RED.getIndex());
			} else {
				font.setColor(IndexedColors.BLACK.getIndex());
			}
			style.setFont(font);
		}

		else if (fileExtensionName.equals(".xls")) {
			wb1 = new HSSFWorkbook(fileInputStream);
			HSSFFormulaEvaluator.evaluateAllFormulaCells(wb1);
			style = wb1.createCellStyle();
			Font font = wb1.createFont();

			if (color.equalsIgnoreCase("Green")) {
				font.setColor(HSSFColor.GREEN.index);
			} else if (color.equalsIgnoreCase("Red")) {
				font.setColor(HSSFColor.RED.index);
			} else {
				font.setColor(HSSFColor.BLACK.index);
			}
			style.setFont(font);
		}

		if (wb1 == null) {
			sheet = wb1.createSheet();
		}

		sheet = wb1.getSheet(sheetname);
		row = sheet.getRow(rows);
		if (row == null) {
			row = sheet.createRow(rows);
		}
		cell = row.getCell(coloum);
		if (cell == null)
			cell = row.createCell(coloum);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue(text);
		cell.setCellStyle(style);
		if (environment.equals("local")) {
			FileOutputStream fileOut = new FileOutputStream(fileName);
			wb1.write(fileOut);
			fileOut.close();
		} else {
			FileOutputStream fileOut = new FileOutputStream(fileName);
			wb1.write(fileOut);
			fileOut.close();
		}
	}

	public void writeDataToLiveire(int coloum, int rows, String text, String sheetname, String fileName, String color)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		// String fileName =null;
		Workbook wb1 = null;
		Sheet sheet;
		Cell cell;
		Row row;
		CellStyle style = null;
		if (environment.equals("local")) {
			fileName = "./src/main/java/data/csvfiles/Livewire Files/" + fileName + ".xlsx";
		} else {
			fileName = "classes//data//csvfiles//LiveWire Files//" + fileName + ".xlsx";
		}
		FileInputStream fileInputStream = new FileInputStream(fileName);
		String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
		if (fileExtensionName.equals(".xlsx")) {
			wb1 = new XSSFWorkbook(fileInputStream);
			XSSFFormulaEvaluator.evaluateAllFormulaCells(wb1);
			style = wb1.createCellStyle();
			Font font = wb1.createFont();
			if (color.equalsIgnoreCase("Green")) {
				font.setColor(IndexedColors.GREEN.getIndex());
			} else if (color.equalsIgnoreCase("Red")) {
				font.setColor(IndexedColors.RED.getIndex());
			} else {
				font.setColor(IndexedColors.BLACK.getIndex());
			}
			style.setFont(font);
		}

		else if (fileExtensionName.equals(".xls")) {
			wb1 = new HSSFWorkbook(fileInputStream);
			HSSFFormulaEvaluator.evaluateAllFormulaCells(wb1);
			style = wb1.createCellStyle();
			Font font = wb1.createFont();
			if (color.equalsIgnoreCase("Green")) {
				//font.setColor(HSSFColor.GREEN.index);
			} else if (color.equalsIgnoreCase("Red")) {
				//font.setColor(HSSFColor.RED.index);
			} else {
				//font.setColor(HSSFColor.BLACK.index);
			}
			style.setFont(font);
		}

		if (wb1 == null) {
			sheet = wb1.createSheet();
		}

		sheet = wb1.getSheet(sheetname);
		row = sheet.getRow(rows);
		if (row == null) {
			row = sheet.createRow(rows);
		}
		cell = row.getCell(coloum);
		if (cell == null)
			cell = row.createCell(coloum);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue(text);
		cell.setCellStyle(style);
		if (environment.equals("local")) {
			FileOutputStream fileOut = new FileOutputStream(fileName);
			wb1.write(fileOut);
			fileOut.close();
		} else {
			FileOutputStream fileOut = new FileOutputStream(fileName);
			wb1.write(fileOut);
			fileOut.close();
		}
	}

	public int getRowNo(String sheetname, String fileName) throws IOException {
		// String fileName =null;
		Workbook wb = null;
		if (environment.equals("local")) {
			fileName = "./src/main/java/data/" + fileName + ".xlsx";
		} else {
			fileName = "classes//data//" + fileName + ".xlsx";
		}
		// System.err.println("File path is : "+fileName);
		FileInputStream fileInputStream = new FileInputStream(fileName);
		// fileInputStream.reset();
		String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
		// System.err.println("File Extension : "+fileExtensionName);
		if (fileExtensionName.equals(".xlsx")) {
			wb = new XSSFWorkbook(fileInputStream);
			// XSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
		} else if (fileExtensionName.equals(".xls")) {
			wb = new HSSFWorkbook(fileInputStream);
			// HSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
		}
		Sheet sheet = wb.getSheet(sheetname);
		int lastRowNum = sheet.getLastRowNum();

		return lastRowNum;
	}

	public static void uploadfile(String Pathtxt) throws AWTException, InterruptedException {

		Thread.sleep(5000);
		// Copy your file's absolute path to the clipboard
		StringSelection ss = new StringSelection(Pathtxt);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		// Paste the file's absolute path into the File name field of the File
		// Upload dialog box
		// native key strokes for CTRL, V and ENTER keys
		Robot robot = new Robot();

		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(500);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
	}

	public void dateSelection(String data, String sDate, String eDate) throws Exception {
		// click(locateElement("xpath", data));

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date givenSdate = dateFormat.parse(sDate);
		// get start Date month year seperately in dd mmm yyyy formats seperately
		String setSdate = new SimpleDateFormat("dd").format(givenSdate);
		String setSmonth = new SimpleDateFormat("MMM").format(givenSdate);
		String setSyear = new SimpleDateFormat("yyyy").format(givenSdate);
		int sYear = Integer.parseInt(setSyear);

		Date givenEdate = dateFormat.parse(eDate);
		// get end Date month year seperately in dd mmm yyyy formats seperately
		String setEdate = new SimpleDateFormat("dd").format(givenEdate);
		String setEmonth = new SimpleDateFormat("MMM").format(givenEdate);
		String setEyear = new SimpleDateFormat("yyyy").format(givenEdate);
		int eYear = Integer.parseInt(setEyear);

		String displayedMY1 = getText(locateElement("xpath", "//th[@class='month']"));
		String[] disp1 = displayedMY1.split(" ");
		int dispYear1 = Integer.parseInt(disp1[1]);

		// Select Start Date
		while ((sYear < dispYear1) || (!displayedMY1.equalsIgnoreCase(setSmonth + " " + setSyear))) {
			locateElement("xpath", "//th[@class='prev available']/i").click();
			displayedMY1 = getText(locateElement("xpath", "//th[@class='month']"));
			disp1 = displayedMY1.split(" ");
			dispYear1 = Integer.parseInt(disp1[1]);
		}
		List<WebElement> DatesList1 = driver
				.findElements(By.xpath("//th[text()='" + setSmonth + " " + setSyear + "']/../../../tbody/tr/td"));

		for (WebElement Date : DatesList1) {
			String dateElement = Date.getText();
			String dateElementClass = Date.getAttribute("class");
			if (dateElement.length() == 1) {
				dateElement = "0" + dateElement;
			}
			if ((dateElement.equalsIgnoreCase(setSdate)) && (dateElementClass.equalsIgnoreCase("available"))) {
				Date.click();
				Thread.sleep(5000);
				break;
			}
		}
		// Select End Date
		String displayedMY2 = getText(locateElement("xpath", "(//th[@class='month'])[2]"));
		String[] disp2 = displayedMY2.split(" ");
		int dispYear2 = Integer.parseInt(disp2[1]);

		while (((dispYear1 < eYear) && (dispYear2 < eYear))
				|| ((!displayedMY1.equalsIgnoreCase(setEmonth + " " + setEyear))
						&& (!displayedMY2.equalsIgnoreCase(setEmonth + " " + setEyear)))) {
			locateElement("xpath", "//th[@class='next available']/i").click();
			displayedMY1 = getText(locateElement("xpath", "//th[@class='month']"));
			disp1 = displayedMY1.split(" ");
			dispYear1 = Integer.parseInt(disp1[1]);
			displayedMY2 = getText(locateElement("xpath", "(//th[@class='month'])[2]"));
			disp2 = displayedMY2.split(" ");
			dispYear2 = Integer.parseInt(disp2[1]);
		}

		List<WebElement> DatesList2 = driver
				.findElements(By.xpath("//th[text()='" + setEmonth + " " + setEyear + "']/../../../tbody/tr/td"));

		for (WebElement Date : DatesList2) {
			String dateElement = Date.getText();
			String dateElementClass = Date.getAttribute("class");
			if (dateElement.length() == 1) {
				dateElement = "0" + dateElement;
			}
			if ((dateElement.equalsIgnoreCase(setEdate)) && (dateElementClass.equalsIgnoreCase("available"))) {
				Date.click();
				Thread.sleep(5000);
				break;
			}
		}
	}	
	
	public static String getAlphabetString(int n) {
		// Chose a Random Character from this String
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz";

		// Create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {
			// generate a random number between 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());

			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
	}	
	
	public static String getDate(String format) {		
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String currentDate = formatter.format(now);
		return currentDate;
	}
	
	public static String getTime(String format) {
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String currentTime = formatter.format(now);
		return currentTime;
	}
	
	public void clearText(WebElement element) {
		element.click();
		element.sendKeys(Keys.CONTROL+"A");
		element.sendKeys(Keys.DELETE);
	}

	public void clearTextWithField(WebElement element,String fieldName) {
		try {
			element.click();
			element.sendKeys(Keys.CONTROL+"A");
			element.sendKeys(Keys.DELETE);
			reportStep("[ "+fieldName+" ] "+" contents are cleared", "PASS");
		}
		catch(Exception e){
			reportStep("[ "+fieldName+" ]"+" contents are not cleared", "FAIL");
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}
		
	public static String getDynamicText(String prefixTitle) {
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
		String timeStamp = formatter.format(now);
		String dynamicText = prefixTitle + " - " + timeStamp;
		return dynamicText;
	}
	
	public static String getNewTitle(String prefixTitle) {
		Date cDate = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("MMddhhmmss");
		String timeStamp = ft.format(cDate);
		String IngestionTitle = prefixTitle + timeStamp;
		return IngestionTitle;
	}

	
	/*************************************SARAVANA PRIYA*************************************/	
	
	public boolean verifyTwoIntValues(String type, int actual, int expected) {
		boolean bool = false;
		try {
			if (actual == expected) {
				bool = true;
				reportStep(type + " : [" + actual + "] MATCHES [" + expected + "]", "PASS");
			} else {
				bool = false;
				reportStep(type + " : [" + actual + "] MISMATCHED [" + expected + "]", "FAIL");
			}
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
		return bool;
	}
	
	public void scrolltoview(WebElement element) throws InterruptedException {
		JavascriptExecutor je = (JavascriptExecutor) driver;
		je.executeScript("arguments[0].scrollIntoView(true);", element);
		Thread.sleep(500);
	}
 
	public void waitUntilClickableOfWebElement(String ele) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 15);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(ele)));
		} catch (WebDriverException e) {
			reportStep("WebDriverException : " + e, "FAIL");
		}
	}
	
	

	public void clickMenu(String menu) {
		try {
			String xpath = "";
			switch (menu) {
			case "Home"   			   : 	xpath = "Home_homeMenuBtn";       break;
			case "Course"    		   : 	xpath = "Home_courseMenuBtn";     break;
			case "User"    			   : 	xpath = "Home_userMenuBtn";       break;
			case "Workflow"    		   : 	xpath = "Home_workflowMenuBtn";   break;
			case "Event Notification"  : 	xpath = "Home_eventNotiMenuBtn";  break;
			case "Administration"      : 	xpath = "Home_adminMenuBtn";      break;
			case "Reports"    		   : 	xpath = "Home_reportsMenuBtn";    break;
			default 				   :  break;
			}

			WebElement element = locateElementXpath(xpath);
			if (!element.getAttribute("aria-expanded").equalsIgnoreCase("true")) {
				click(element);
			}

//			if (menu.equalsIgnoreCase("Home")) {
//				Thread.sleep(3000);
//				String testingPpFrame = prop.getProperty("Home_testingPpFrame");
//				driver.switchTo().frame(driver.findElement(By.xpath(testingPpFrame)));
//				click(locateElementXpath("Home_testingPpClsBtn") , "Testing Popup Close Button");
//				driver.switchTo().defaultContent();
//			}
			
			if (menu.equalsIgnoreCase("Home")) {
	            WebDriverWait wait = new WebDriverWait(driver, 10);
	            String testingPpFrame = prop.getProperty("Home_testingPpFrame");
	            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath(testingPpFrame)));
	            WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(locateElementXpath("Home_testingPpClsBtn")));
	            click(closeBtn, "Testing Popup Close Button");
	            driver.switchTo().defaultContent();
	        }

			Thread.sleep(2000);
		} catch (Exception e) {
			reportStep("WebDriverException : ", e.getMessage());
		}
	}
	
	public void clickSubMenu(String xpath1, String xpath2, String value) {
		try {
			List<WebElement> subMenu1 = locateElements("xpath", xpath1);
			for (WebElement element1 : subMenu1) {
				if (element1.getAttribute("class").contains("subMenu")) {
					if (!element1.getAttribute("aria-expanded").equalsIgnoreCase("true")) {
						JavascriptExecutor executor = (JavascriptExecutor) driver;
						executor.executeScript("arguments[0].click();", element1);
					}					
				}
			}

			List<WebElement> subMenu2 = locateElements("xpath", xpath2);
			for (WebElement element2 : subMenu2) {
				if (element2.getText().contains(value)) {
					moveToElement(element2);
					click(element2);
				}
			}
			Thread.sleep(2000);
		} catch (Exception e) {
			reportStep("WebDriverException : ", e.getMessage());
		}
	}
	
	public int getTableEntries(int index) {
		String footerEntry = locateElementXpath("COM_footerEntry").getText();
		String[] splitedfooterEntry = footerEntry.split(" ");
		int totalBannerEntry = Integer.parseInt(splitedfooterEntry[index]);
		return totalBannerEntry;
	}

	public void jsClick(WebElement ele, String value) {
		try {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", ele);
			reportStep("[" + value + "] is Clicked", "PASS");
		} catch (InvalidElementStateException e) {
			reportStep("[" + value + "] Couldn't Click", "FAIL");
		} catch (WebDriverException e) {
			reportStep("WebDriverException: " + e, "FAIL");
		}
	}


}

	

