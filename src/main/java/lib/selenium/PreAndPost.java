package lib.selenium;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

//import com.ibps.main.MainClass;

import lib.utils.DataInputProvider;

public class PreAndPost extends WebDriverServiceImpl{

	public String dataSheetName, sheetName;	
    
	@BeforeSuite
	public void beforeSuite() {
		startReport();
	}
    
	@BeforeClass 
	public void beforeClass() {
		startTestCase(testCaseName, testDescription);	
		testCaseStatus = "PASS";
		System.out.println("BeforeClass TestCase Status = " + testCaseStatus);
	}

	@BeforeMethod
	public void beforeMethod() throws EncryptedDocumentException, IOException {	
		startTestModule(nodes);
		test.assignAuthor(authors);
		test.assignCategory(category);
		startApp("chrome");
		loadUrl("url");  
		
	}

	@AfterMethod
	public void afterMethod() {
		endResult();
	}

	@AfterClass
	public void afterClass() {
		System.out.println("AfterClass testCaseStatus = "+testCaseStatus);
	}

	@AfterTest
	public void afterTest() {

	}

	@AfterSuite
	public void afterSuite() throws Throwable {

	}
	
	@DataProvider(name="fetchData")
	public  Object[][] getData(){		
		return DataInputProvider.getSheet(dataSheetName, sheetName);		
	}

}


