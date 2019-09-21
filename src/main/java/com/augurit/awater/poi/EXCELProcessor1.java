package com.augurit.awater.poi;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class EXCELProcessor1 {
	private GCJ284Converter converter = new GCJ284Converter();

	private static void process(File xlsFile) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook(xlsFile);
			XSSFSheet sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				return;
			}

			double lng, lat;
			// 对于每个sheet，读取其中的每一行
			int j = 1;
			for (j = 1; j <= sheet.getLastRowNum(); j++) {
				XSSFRow row = sheet.getRow(j);
				if (row == null) {
					continue;
				}
				try {
					lng = row.getCell(4).getNumericCellValue();
					lat = row.getCell(5).getNumericCellValue();

					double[] wgs84 = GCJ284Converter.gcj02towgs84(lng, lat);
					// System.out.println(String.valueOf(gcj02towgs84[0]) + '\t' + String.valueOf(gcj02towgs84[1]));
					Mercator merc = new Mercator();
					double[] merc_res = merc.merc(wgs84[0], wgs84[1]);

					XSSFCell cellx = row.createCell(6);
					cellx.setCellType(CellType.NUMERIC);
					cellx.setCellValue(merc_res[0]);
					XSSFCell celly = row.createCell(7);
					celly.setCellType(CellType.NUMERIC);
					celly.setCellValue(merc_res[1]);
				} catch (Exception e) {
					System.out.println(j);
					e.printStackTrace();
				}

			}
		FileOutputStream fos = new FileOutputStream("C:\\Users\\heng\\Desktop\\ff.xlsx");
		workbook.write(fos);
		fos.close();
		workbook.close();
	}

	private static String toJSON(File xlsFile) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook(xlsFile);
		XSSFSheet sheet = workbook.getSheetAt(0);
		if (sheet == null) {
			return null;
		}

		List<Map<String, Object>> ret = Lists.newArrayList();

		// 对于每个sheet，读取其中的每一行
		int j = 1;
		for (j = 1; j <= sheet.getLastRowNum(); j++) {
			XSSFRow row = sheet.getRow(j);
			if (row == null) {
				continue;
			}
			Map<String, Object> map = Maps.newHashMap();
			try {
				map.put("id", row.getCell(0).getNumericCellValue());
				map.put("name", row.getCell(1).getStringCellValue());
				map.put("lng", row.getCell(2).getNumericCellValue());
				map.put("lat", row.getCell(3).getNumericCellValue());
				map.put("merx", row.getCell(4).getNumericCellValue());
				map.put("mery", row.getCell(5).getNumericCellValue());

				ret.add(map);
			} catch (Exception e) {
				System.out.println(j);
				e.printStackTrace();
			}
			workbook.close();
		}
		return JSONArray.toJSONString(ret);

	}

	public static void main(String[] args) throws Exception {
		//System.out.println(toJSON(new File("C:\\Users\\heng\\Desktop\\hh.xlsx")));
		Mercator merc = new Mercator();
		System.out.println("1.示例-----------");
		double[] wgs84 = GCJ284Converter.gcj02towgs84(110.342172, 20.060426);
		double[] merc_res = merc.merc(wgs84[0], wgs84[1]);
		System.out.println(String.valueOf(110.342172)+'\t'+String.valueOf(20.060426)+'\t'
				+String.valueOf(merc_res[0]) + '\t' + String.valueOf(merc_res[1]));
		System.out.println("1.其中一条-----------");
		wgs84 = GCJ284Converter.gcj02towgs84(110.35722222222222, 20.035555555555558);
		merc_res = merc.merc(wgs84[0], wgs84[1]);
		System.out.println(String.valueOf(110.35722222222222)+'\t'+String.valueOf(20.035555555555558)+'\t'
				+String.valueOf(merc_res[0]) + '\t' + String.valueOf(merc_res[1]));
		System.out.println("真实数据如下：");
		System.out.println(String.valueOf(110.35722222222222)+'\t'+String.valueOf(20.035555555555558)+'\t'
				+String.valueOf(12284417.060879905) + '\t' + String.valueOf(2262847.260748469));


	}

	private static double toNumber(String mkt) throws Exception {
		String[] numbers = mkt.split("[°\\'\\.]");

		double[] ns = new double[4];
		for(int i=0; i<numbers.length; i++) {
			ns[i] = Double.parseDouble(numbers[i]);
		}

		return ns[0] + ns[1]/60 + ns[2]/3600;
	}
}
