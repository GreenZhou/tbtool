package com.augurit.awater.entity;

import com.augurit.awater.DefaultIdGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 说    明：
 * 创 建 人： ebo
 * 创建日期： 2017-11-22 14:17
 * 修改说明：
 */
public class FileInfo {
	// 格式化日期对象
	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String id = DefaultIdGenerator.getIdForStr();
	// 原始文件的后缀名, 如.doc、.txt等
	private String suffix;
	private String originalFilename;// 文件原始名称
	private String fileSize = "0KB";// 文件大小，带上单位
	private String isActive = "0";// 0：无效 1：有效
	private String encoding = "UTF-8";// 文件内容编码
	private String createTime = format.format(new Date());// 附件信息创建时间
	private String dirPath;
	private String creatorLoginName;
	private String creatorUserName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

	public String getCreatorLoginName() {
		return creatorLoginName;
	}

	public void setCreatorLoginName(String creatorLoginName) {
		this.creatorLoginName = creatorLoginName;
	}

	public String getCreatorUserName() {
		return creatorUserName;
	}

	public void setCreatorUserName(String creatorUserName) {
		this.creatorUserName = creatorUserName;
	}
}
