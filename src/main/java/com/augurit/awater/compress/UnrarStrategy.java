package com.augurit.awater.compress;

import com.augurit.awater.exception.AppException;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

class UnrarStrategy implements IUncompress {
	private static final UnrarStrategy instance = new UnrarStrategy();

	private UnrarStrategy() {}

	@Override
	public void uncompress(String sourcePath) throws Exception {
		throw new AppException();
	}

	@Override
	public void uncompress(String sourcePath, String destPath) throws Exception {
		Archive arch = new Archive(new File(sourcePath));
		if(arch == null) {
			throw new AppException();
		}

		if (arch.isEncrypted()) {
			throw new AppException();
		}

		FileHeader fh = null;

		while(true) {
			fh = arch.nextFileHeader();
			if(fh == null) {
				break;
			}
			if (fh.isDirectory()) {
				createDirectory(fh, new File(destPath));
			} else {
				File f = createFile(fh, new File(destPath));
				OutputStream stream = new FileOutputStream(f);
				arch.extractFile(fh, stream);
				stream.close();
			}
		}
	}

	protected static UnrarStrategy getInstance() {
		return instance;
	}

	private File createFile(FileHeader fh, File destination) throws Exception {
		File f = null;
		String name = null;
		if (fh.isFileHeader() && fh.isUnicode()) {
			name = fh.getFileNameW();
		} else {
			name = fh.getFileNameString();
		}
		f = new File(destination, name);
		if (!f.exists()) {
			f = makeFile(destination, name);
		}
		return f;
	}

	private File makeFile(File destination, String name) throws Exception {
		String[] dirs = name.split("\\\\");
		if (dirs == null) {
			return null;
		}
		String path = "";
		int size = dirs.length;
		if (size == 1) {
			return new File(destination, name);
		} else if (size > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				path = path + File.separator + dirs[i];
				new File(destination, path).mkdir();
			}
			path = path + File.separator + dirs[dirs.length - 1];
			File f = new File(destination, path);
			f.createNewFile();
			return f;
		} else {
			return null;
		}
	}

	private void createDirectory(FileHeader fh, File destination) {
		File f = null;
		if (fh.isDirectory() && fh.isUnicode()) {
			f = new File(destination, fh.getFileNameW());
			if (!f.exists()) {
				makeDirectory(destination, fh.getFileNameW());
			}
		} else if (fh.isDirectory() && !fh.isUnicode()) {
			f = new File(destination, fh.getFileNameString());
			if (!f.exists()) {
				makeDirectory(destination, fh.getFileNameString());
			}
		}
	}

	private void makeDirectory(File destination, String fileName) {
		String[] dirs = fileName.split("\\\\");
		if (dirs == null) {
			return;
		}
		String path = "";
		for (String dir : dirs) {
			path = path + File.separator + dir;
			new File(destination, path).mkdir();
		}

	}
}
