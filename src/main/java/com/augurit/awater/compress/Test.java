package com.augurit.awater.compress;


import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.List;

public class Test {
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		/*
		UnrarStrategy s = UnrarStrategy.getInstance();
		s.uncompress("C:\\Users\\heng\\Desktop\\数据库.rar", "C:\\Users\\heng\\Desktop\\test1\\");
		System.out.println(System.currentTimeMillis() - start);

		File[] files = new File("C:\\Users\\heng\\Desktop\\test1\\").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(name.equals("数据库1")) {
					return true;
				}
				return false;
			}
		});
		*/
		Iterator<File> files = FileUtils.iterateFiles(new File("C:\\Users\\heng\\Desktop\\test1\\"), new String[] {"sql"}, true);
		/*
		for(File file : files) {
			System.out.println(file.getCanonicalPath());
		}
		*/
		while(files.hasNext()) {
			System.out.println(files.next().getCanonicalPath());
		}
	}
}
