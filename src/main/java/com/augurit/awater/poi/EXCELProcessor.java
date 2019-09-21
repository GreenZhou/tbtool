package com.augurit.awater.poi;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

public class EXCELProcessor {
	private static HxzbToWgsUtils utils = new HxzbToWgsUtils();

	private static void process(File xlsFile) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook(xlsFile);
		//for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
			XSSFSheet sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				return;
			}

			double lgtd, lttd;
			// 对于每个sheet，读取其中的每一行
			int j = 1;
			for (j = 1; j <= sheet.getLastRowNum(); j++) {
				XSSFRow row = sheet.getRow(j);
				if (row == null) {
					continue;
				}
				try {
					lgtd = Double.parseDouble(row.getCell(3).getStringCellValue());
					lttd = Double.parseDouble(row.getCell(4).getStringCellValue());

					double[] xys = utils.gcj02towgs84(lgtd, lttd);
					XSSFCell cellx = row.createCell(29);
					cellx.setCellType(CellType.NUMERIC);
					cellx.setCellValue(xys[0]);
					XSSFCell celly = row.createCell(30);
					celly.setCellType(CellType.NUMERIC);
					celly.setCellValue(xys[1]);


				} catch (Exception e) {
					System.out.println(j);
					e.printStackTrace();
				}

			}

			//sheet.
		//}
		FileOutputStream fos = new FileOutputStream("C:\\Users\\heng\\Desktop\\dd.xlsx");
		workbook.write(fos);
		fos.close();
		workbook.close();
	}

	public static void main(String[] args) throws Exception {
		new EXCELProcessor().process(new File("C:\\Users\\heng\\Desktop\\cc.xlsx"));
	}
}
