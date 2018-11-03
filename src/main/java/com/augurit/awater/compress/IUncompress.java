package com.augurit.awater.compress;

public interface IUncompress {
	void uncompress(String sourcePath) throws Exception;
	void uncompress(String sourcePath, String destPath) throws Exception;
}
