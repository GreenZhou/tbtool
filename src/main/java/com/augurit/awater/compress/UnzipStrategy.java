package com.augurit.awater.compress;

import com.augurit.awater.exception.AppException;
import net.lingala.zip4j.core.ZipFile;

class UnzipStrategy implements IUncompress {
	private static final UnzipStrategy instance = new UnzipStrategy();

	private UnzipStrategy() {}

	@Override
	public void uncompress(String sourcePath) throws Exception {
		throw new AppException();
	}

	@Override
	public void uncompress(String sourcePath, String destPath) throws Exception {
		ZipFile zFile = new ZipFile(sourcePath);
		zFile.setFileNameCharset("GBK");
		zFile.extractAll(destPath);
	}

	protected static UnzipStrategy getInstance() {
		return instance;
	}
}
