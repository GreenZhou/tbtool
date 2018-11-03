package com.augurit.awater.poi;

import com.augurit.awater.DefaultIdGenerator;
import com.augurit.awater.entity.TaskDetail;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskDetailProcessor {
	public List<TaskDetail> process(File xlsFile) throws Exception {
		List<TaskDetail> details = Lists.newArrayList();

		XSSFWorkbook workbook = new XSSFWorkbook(xlsFile);
		for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
			XSSFSheet sheet = workbook.getSheetAt(i);
			if (sheet == null) {
				continue;
			}
			String salerName = null, salerTMName = null;
			// 对于每个sheet，读取其中的每一行
			for (int j = 0; j <= sheet.getLastRowNum(); j++) {
				XSSFRow row = sheet.getRow(j);
				if (row == null) {
					continue;
				}
				if("店铺名称".equals(row.getCell(0).getStringCellValue())) {
					salerName = row.getCell(1).getStringCellValue();
				} else if("店铺旺旺".equals(row.getCell(0).getStringCellValue())) {
					salerTMName = row.getCell(1).getStringCellValue();
				} else if("关键词".equals(row.getCell(0).getStringCellValue())) {
					details.addAll(processTaskDetails(j + 1, sheet, salerName, salerTMName));
				}
			}
		}

		workbook.close();

		return details;
	}

	private List<TaskDetail> processTaskDetails(int rownum, XSSFSheet sheet, String salerName, String salerTMName) throws Exception {
		List<TaskDetail> details = new ArrayList<TaskDetail>();
		for(int j = rownum; j <= sheet.getLastRowNum(); j++) {
			XSSFRow row = sheet.getRow(j);
			if (row == null) {
				continue;
			}
			String taskDetailName = row.getCell(0).getStringCellValue();
			String taskDesc = row.getCell(1).getStringCellValue();
			String taskUrl = row.getCell(2).getStringCellValue();
			XSSFCell cell3 = row.getCell(3);
			cell3.setCellType(CellType.NUMERIC);
			double totalCommission = cell3.getNumericCellValue();
			XSSFCell cell4 = row.getCell(4);
			cell4.setCellType(CellType.NUMERIC);
			double taskUnitPrice = cell4.getNumericCellValue();
			XSSFCell cell5 = row.getCell(5);
			cell5.setCellType(CellType.STRING);
			int taskNum = Integer.parseInt(cell5.getStringCellValue());
			XSSFCell cell6 = row.getCell(6);
			cell6.setCellType(CellType.NUMERIC);
			double taskTotalPrice = cell6.getNumericCellValue();

			TaskDetail detail = new TaskDetail();
			detail.setId(DefaultIdGenerator.getIdForStr());
			detail.setSalerName(salerName);
			detail.setSalerTMName(salerTMName);// 阿里旺旺名称
			detail.setTaskDetailName(taskDetailName);
			detail.setTaskDesc(taskDesc);
			detail.setTaskUrl(taskUrl);
			detail.setTotalCommission(totalCommission);
			detail.setTaskUnitPrice(taskUnitPrice);
			detail.setTaskNum(taskNum);
			detail.setTaskTotalPrice(taskTotalPrice);
			detail.setCreateTime(new Date());

			details.add(detail);
			//System.out.println(salerName + "," + salerTMName + "," + taskDetailName + "," + taskDesc + "," + taskUrl + "," + totalCommission + "," + taskUnitPrice + "," + taskNum + "," + taskTotalPrice);
		}

		return details;
	}

	public static void main(String[] args) throws Exception {
		new TaskDetailProcessor().process(new File("C:\\Users\\heng\\Desktop\\任务.xlsx"));
	}
}
