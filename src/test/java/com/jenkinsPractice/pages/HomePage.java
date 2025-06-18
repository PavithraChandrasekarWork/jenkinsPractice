package com.jenkinsPractice.pages;

import java.util.List;

import javax.xml.xpath.XPath;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.aventstack.extentreports.ExtentTest;
import lib.selenium.PreAndPost;


public class HomePage extends PreAndPost{
	
	
	public HomePage(RemoteWebDriver driver, ExtentTest test) {
		
		this.driver = driver;
		this.test = test;
	}
	
	public HomePage validateTitleOfPage(String pageTitle) {
		if(driver.getTitle().equals(pageTitle)) {
			System.out.println("Page loaded successfully!");
		}else {
			System.out.println("Page is not loaded successfully");
		}
		return this;
	}

	public HomePage checkAllRadioBtns() {
		List<WebElement> radioBtns= locateElements("xpath", "radioBtn_xp");
		int i=1;
		for(WebElement rb:radioBtns) {
			System.out.println("radio btn click"+ i);
			i++;
			rb.click();
		}
		return this;
	}
}
