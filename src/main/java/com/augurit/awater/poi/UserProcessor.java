package com.augurit.awater.poi;

import com.augurit.awater.DefaultIdGenerator;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.List;

public class UserProcessor {
	public static List<User> importFileToUsers(File xlsFile, User loginUser) throws Exception {
		if(!xlsFile.exists()) {
			throw new AppException();
		}

		List<User> users = Lists.newArrayList();
		XSSFWorkbook workbook = new XSSFWorkbook(xlsFile);
		for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
			XSSFSheet sheet = workbook.getSheetAt(i);
			if (sheet == null) {
				continue;
			}

			// 对于每个sheet，读取其中的每一行
			for (int j = 1; j <= sheet.getLastRowNum(); j++) {
				XSSFRow row = sheet.getRow(j);
				if (row == null) {
					continue;
				}
				XSSFCell cell2 = row.getCell(2);
				cell2.setCellType(CellType.STRING);
				int userType = Integer.parseInt(cell2.getStringCellValue());
				if(userType != loginUser.getUserType() + 1) {
					// 用户没有权限操作
					continue;
				}

				XSSFCell cell0 = row.getCell(0);
				cell0.setCellType(CellType.STRING);
				XSSFCell cell1 = row.getCell(1);
				cell1.setCellType(CellType.STRING);

				User user = new User();
				user.setId(DefaultIdGenerator.getIdForStr());
				user.setLoginName(cell0.getStringCellValue());
				user.setUserName(cell1.getStringCellValue());
				user.setPasswd("888888");
				user.setUserType(userType);
				users.add(user);
			}
		}

		return users;
	}

	public File exportUsersToFile(List<User> users) throws Exception {
		return null;
	}
}
