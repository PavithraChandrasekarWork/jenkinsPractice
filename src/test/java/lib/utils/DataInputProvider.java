package lib.utils;

import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lib.selenium.WebDriverServiceImpl;

public class DataInputProvider{

	public static Object[][] getSheet(String dataSheetName, String sheetName) {

		Object[][] data = null ;
		try {
			FileInputStream fis = null;
			if(new WebDriverServiceImpl().environment.equals("local")) {
				fis = new FileInputStream("./src/test/java/data/"+dataSheetName+".xlsx");
			}else if(new WebDriverServiceImpl().environment.equals("jenkins")) {
				fis = new FileInputStream("./src/test/java/data/"+dataSheetName+".xlsx");
			} else {
				fis = new FileInputStream("classes//data//"+dataSheetName+".xlsx");	
			}
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheet(sheetName);
			//XSSFSheet sheet = workbook.getSheetAt(0);	
			// get the number of rows
			int rowCount = sheet.getLastRowNum();
			// get the number of columns
			int columnCount = sheet.getRow(0).getLastCellNum();
			data = new String[rowCount][columnCount];
			// loop through the rows
			for(int i=1; i <rowCount+1; i++){
				try {
					XSSFRow row = sheet.getRow(i);
					for(int j=0; j <columnCount; j++){ // loop through the columns
						try {
							DataFormatter formatter = new DataFormatter();
							String cellValue = "";
							try{
								cellValue = formatter.formatCellValue(row.getCell(j));
//								cellValue = row.getCell(j).getStringCellValue();
							}catch(NullPointerException e){
							}
							data[i-1][j]  = cellValue; // add to the data array
							System.out.println(cellValue); 
						} catch (Exception e) {
							e.printStackTrace();
						}				
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			fis.close();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;		

	}
}
