package com.augurit.awater.dao;

import com.augurit.awater.entity.FileInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 说    明： 附件上传下载模块Mapper
 * 创 建 人： ebo
 * 创建日期： 2017-11-22 14:15
 * 修改说明：
 */
@Repository
public interface FileMapper {
	/**
	 * 保存附件
	 * @param fileInfo 附件信息对象
	 * @throws Exception
	 */
	void saveFile(FileInfo fileInfo) throws Exception;

	/**
	 * 更新附件状态
	 * @param id 附件ID
	 * @param isActive 0：无效 1：有效
	 * @throws Exception
	 */
	void updateFileStatus(@Param("id") String id, @Param("isActive") String isActive) throws Exception;

	/**
	 * 通过id组查找所有的附件信息
	 * 注意： 如果ids插入空的集合，则查询所有附件信息，该方法不负责排序相关操作
	 *
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	List<FileInfo> findFileInfos(@Param("ids") List<String> ids) throws Exception;

	/**
	 * 通过文件目录组查找所有的附件信息
	 * 注意： 如果dirIds插入空的集合，则查询所有附件信息，该方法不负责排序相关操作
	 *
	 * @param dirIds
	 * @param fileName 文件名称
	 * @return
	 * @throws Exception
	 */
	List<FileInfo> listFileInfos(@Param("dirIds") List<String> dirIds, @Param("fileName") String fileName) throws Exception;

	/**
	 * 通过id查找单个文件信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	FileInfo getFileInfo(String id) throws Exception;

	/**
	 * 保存服务附件
	 * @param instanceId 申请ID
	 * @param ids 附件ID
	 * @throws Exception
	 */
	void saveTaskFiles(@Param("instanceId") String instanceId, @Param("ids") List<String> ids) throws Exception;

	/**
	 * 根据申请实例ID获取文件列表信息
	 * @param instanceId
	 * @return
	 * @throws Exception
	 */
	List<FileInfo> findFileInfosByInstanceId(@Param("instanceId") String instanceId) throws Exception;

	/**
	 * 删除一条记录
	 * @param instanceId
	 * @param id
	 */
	void removeTaskFile(@Param("instanceId") String instanceId, @Param("id") String id);

	/**
	 * 删除一组文件记录
	 * @param ids 文件ID集合
	 */
	void removeFiles(@Param("ids") List<String> ids) throws Exception;
}
