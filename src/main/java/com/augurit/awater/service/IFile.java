package com.augurit.awater.service;

import com.augurit.awater.entity.FileInfo;

import java.util.List;

/**
 * 说    明：
 * 创 建 人： ebo
 * 创建日期： 2017-11-22 14:13
 * 修改说明：
 */
public interface IFile {
	/**
	 * 保存附件
	 * @param fileInfo 附件信息对象
	 * @throws Exception
	 */
	void saveFile(FileInfo fileInfo) throws Exception;

	/**
	 * 删除附件
	 * @param ids 附件ID集合
	 * @throws Exception
	 */
	void delFileInfos(List<String> ids) throws Exception;

	/**
	 * 更新附件状态
	 * @param id 附件ID
	 * @param isActive 0：无效 1：有效
	 * @throws Exception
	 */
	void updateFileStatus(String id, String isActive) throws Exception;

	/**
	 * 通过id组查找所有的附件信息
	 * 注意： 如果ids插入空的集合，则查询所有附件信息，该方法不负责排序相关操作
	 *
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	List<FileInfo> findFileInfos(List<String> ids) throws Exception;

	/**
	 * 通过文件目录组查找所有的附件信息
	 * 注意： 如果dirIds插入空的集合，则查询所有附件信息，该方法不负责排序相关操作
	 *
	 * @param dirIds
	 * @param fileName 文件名称
	 * @return
	 * @throws Exception
	 */
	List<FileInfo> findFileInfoList(List<String> dirIds, String fileName) throws Exception;

	/**
	 * 通过id查找单个文件信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	FileInfo getFileInfo(String id) throws Exception;

	/**
	 * 根据任务明细ID获取文件列表信息
	 * @param detailId 任务明细
	 * @return
	 * @throws Exception
	 */
	List<FileInfo> findFileInfosByTaskDetailId(String detailId) throws Exception;
}
