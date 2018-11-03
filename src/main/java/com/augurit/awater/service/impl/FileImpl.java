package com.augurit.awater.service.impl;

import com.augurit.awater.entity.FileInfo;
import com.augurit.awater.dao.FileMapper;
import com.augurit.awater.service.IFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 说    明：
 * 创 建 人： ebo
 * 创建日期： 2017-11-22 14:14
 * 修改说明：
 */
@Transactional
@Service
public class FileImpl implements IFile {

	@Autowired
	private FileMapper fileMapper;

	@Override
	public void saveFile(FileInfo fileInfo) throws Exception {
		fileMapper.saveFile(fileInfo);
	}

	@Override
	public void updateFileStatus(String id, String isActive) throws Exception {
		fileMapper.updateFileStatus(id, isActive);
	}

	@Override
	public List<FileInfo> findFileInfos(List<String> ids) throws Exception {
		return fileMapper.findFileInfos(ids);
	}

	@Override
	public List<FileInfo> listFileInfos(List<String> dirIds, String fileName) throws Exception {
		return fileMapper.listFileInfos(dirIds, fileName);
	}

	@Override
	public FileInfo getFileInfo(String id) throws Exception {
		return fileMapper.getFileInfo(id);
	}

	@Override
	public void delFileInfos(List<String> ids) throws Exception {
		fileMapper.removeFiles(ids);
	}

	@Override
	public List<FileInfo> findFileInfosByInstanceId(String instanceId) throws Exception {
		return fileMapper.findFileInfosByInstanceId(instanceId);
	}
}
