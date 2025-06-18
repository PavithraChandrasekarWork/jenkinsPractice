package lib.utils;

import java.io.IOException;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.MediaEntityModelProvider;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import lib.selenium.WebDriverServiceImpl;

public abstract class HTMLReporter {

	public static ExtentHtmlReporter html;
	public static ExtentReports extent;
	public ExtentTest testSuite, test;
	public String testCaseName, testDescription, nodes, authors, category, reportName;
	public static String  testCaseStatus;	
	protected static long newNumber;
	
	public void startReport() {
		newNumber = (long) Math.floor(Math.random() * 900000000L) + 10000000L;
		if(new WebDriverServiceImpl().environment.equals("local")) {
			//_"+newNumber+"
			html = new ExtentHtmlReporter("./src/test/java/reports/Livewire_Report.html");
			html.setAppendExisting(true);
			//html.loadXMLConfig("./src/main/java/propertiesFile/extent-config.xml")
			extent = new ExtentReports();
			extent.attachReporter(html); 
		}else if(new WebDriverServiceImpl().environment.equals("jenkins")) {
			//_"+newNumber+"
			html = new ExtentHtmlReporter(System.getProperty("user.dir") +"/src/test/java/reports/Livewire_Report.html");
			html.setAppendExisting(true);
			//html.loadXMLConfig("./src/main/java/propertiesFile/extent-config.xml")
			extent = new ExtentReports();
			extent.attachReporter(html); 
		} else { 
			html = new ExtentHtmlReporter("classes//reports//sanityReport_"+newNumber+".html"); 
			html.setAppendExisting(true);
			//html.loadXMLConfig("classes//propertiesFile//extent-config.xml");
			extent = new ExtentReports();
			extent.attachReporter(html);
		}
	}

	public ExtentTest startTestCase(String testCaseName, String testDescription) {
		testSuite = extent.createTest(testCaseName, testDescription);		
		return testSuite;
	}

	public ExtentTest startTestModule(String nodes) {
		test = testSuite.createNode(nodes);
		return test;
	}

	public abstract long takeSnap();
	public abstract long cdriverTakeSnap();
	protected long snapNumber = 100000L;
	public void reportStep(String desc, String status, boolean bSnap)  {

		MediaEntityModelProvider img = null;
		if(bSnap && !status.equalsIgnoreCase("INFO") && !status.equalsIgnoreCase("PASS")){

			//long snapNumber = 100000L;
			snapNumber = takeSnap();   
			try {
				if(new WebDriverServiceImpl().environment.equals("local")) {
				img = MediaEntityBuilder.createScreenCaptureFromPath
						("..\\reports\\images\\"+snapNumber+".jpg").build();
				}else if(new WebDriverServiceImpl().environment.equals("jenkins")) {
					img = MediaEntityBuilder.createScreenCaptureFromPath
							(System.getProperty("user.dir") +"/src/test/java/reports/images/"+snapNumber+".jpg").build();
				}else {
					img = MediaEntityBuilder.createScreenCaptureFromPath
							("..//reports//images//"+snapNumber+".jpg").build();
				}} catch (IOException e) {	
			}
		}
		if(status.equalsIgnoreCase("PASS")) {
			test.pass(desc);	
		}else if (status.equalsIgnoreCase("FAIL")) {
			test.fail(desc, img);
			//System.out.println("TestCase Status before"+testCaseStatus);
			testCaseStatus = "FAIL";
			//System.out.println("TestCase Status after"+testCaseStatus);
			//throw new RuntimeException();
		}else if (status.equalsIgnoreCase("WARNING")) {
			test.warning(desc, img);
		}else if (status.equalsIgnoreCase("INFO")) {
			test.info(desc);
		}						
	}
	public void cdriverReportStep(String desc, String status, boolean bSnap)  {

		MediaEntityModelProvider img = null;
		if(bSnap && !status.equalsIgnoreCase("INFO")){

			long snapNumber = 100000L;
			snapNumber = cdriverTakeSnap();
			try {
				if(new WebDriverServiceImpl().environment.equals("local")) {
				img = MediaEntityBuilder.createScreenCaptureFromPath
						("..\\reports\\images\\"+snapNumber+".jpg").build();
				}else if(new WebDriverServiceImpl().environment.equals("local")) {
					img = MediaEntityBuilder.createScreenCaptureFromPath
							(System.getProperty("user.dir") +"/src/test/java/reports/images/"+snapNumber+".jpg").build();
				}else {
					img = MediaEntityBuilder.createScreenCaptureFromPath
							("..//reports//images//"+snapNumber+".jpg").build();
				}} catch (IOException e) {	
			}
		}
		if(status.equalsIgnoreCase("PASS")) {
			test.pass(desc);	
		}else if (status.equalsIgnoreCase("FAIL")) {
			test.fail(desc, img);
			testCaseStatus = "FAIL";
			throw new RuntimeException();
		}else if (status.equalsIgnoreCase("WARNING")) {
			test.warning(desc, img);
		}else if (status.equalsIgnoreCase("INFO")) {
			test.info(desc);
		}						
	}

	public void reportStep(String desc, String status) {
		reportStep(desc, status, true);
	}
	public void cdriverReportStep(String desc, String status) {
		cdriverReportStep(desc, status, true);
	}

	public void endResult() {
		extent.flush();
	}		


}
