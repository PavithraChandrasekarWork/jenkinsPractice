package com.jenkinsPractice.testcases;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jenkinsPractice.pages.*;

import lib.selenium.PreAndPost;

public class TC01_HomePageTest extends PreAndPost {
		@BeforeTest
		public void setValues() {
			testCaseName = "Jenkins Test";
			testCaseStatus = "PASS";
			testDescription = "Jenkins test";
			nodes = "Admin";
			authors = "Pavithra C";
			category = "Smoke";
			dataSheetName = "Jenkins_test_sheet";
			sheetName = "Home";
		}

		@Test(dataProvider="fetchData")
		public void jenkinsTest(String pageTitle) throws InterruptedException {
			
			
			new HomePage(driver,test)
			.validateTitleOfPage(pageTitle)
			;
		}

}


