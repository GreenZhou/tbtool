package com.augurit.awater.compress;

import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.exception.AppException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class UncompressImpl implements IUncompress {

	private String defaultUncompressDestPath = "C:/awater/compress/";

	private UnzipStrategy unzip = UnzipStrategy.getInstance();
	private UnrarStrategy unrar = UnrarStrategy.getInstance();

	@Override
	public void uncompress(String sourcePath) throws Exception {
		uncompress(sourcePath, defaultUncompressDestPath);
	}

	@Override
	public void uncompress(String sourcePath, String destPath) throws Exception {
		File sourceFile = new File(sourcePath);
		if(!sourceFile.exists() && !sourceFile.isFile()) {
			throw new AppException(RespCodeMsgDepository.FILE_NOT_EXISTS, "原文件不存在");
		}

		File destDir = new File(destPath);
		if(!destDir.exists()) {
			destDir.mkdirs();
		}

		String extension = FilenameUtils.getExtension(sourcePath);
		if(extension.endsWith("zip")) {
			unzip.uncompress(sourcePath, destPath);
		} else if(extension.endsWith("rar")) {
			unrar.uncompress(sourcePath, destPath);
		} else {
			// 压缩文件格式暂不支持
			throw new AppException();
		}
	}
}
