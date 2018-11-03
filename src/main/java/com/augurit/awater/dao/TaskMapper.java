package com.augurit.awater.dao;

import com.augurit.awater.entity.TaskDetail;
import com.augurit.awater.entity.TaskInstance;
import com.augurit.awater.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskMapper {
	TaskInstance getTaskInstance(@Param("id") String id) throws Exception;
	//  创建任务实例
	void saveTaskInstance(TaskInstance instance) throws Exception;
	//  发布任务实例
	void publishTaskInstance(String id, List<TaskDetail> details) throws Exception;
	//  查询所有的自己有权限任务实例
	List<TaskInstance> findTaskInstanceList(@Param("recieverId") String recieverId, @Param("creatorId") String creatorId) throws Exception;
	// 更新任务实例（仅仅在没发布、发布后没被处理才可以执行删除操作）
	void updateTaskInstance(TaskInstance instance) throws Exception;
	// 删除任务实例（仅仅在没发布、发布后没被处理才可以执行删除操作）
	void delTaskInstance(@Param("id") String id) throws Exception;
	// 废弃任务实例（仅仅在没发布、发布后没被处理、发布后各个任务明细都被处理成废弃状态才可以执行该操作）
	void abandonTaskInstance(@Param("id") String id) throws Exception;
	//  指派具体买家
	void assignTaskInstance(@Param("id") String id, @Param("customerIds") List<String> customerIds, @Param("customerNum") int customerNum) throws Exception;
	//  查询具体的任务明细
	List<TaskDetail> findTaskDetailList(@Param("user") User user, @Param("instanceId") String instanceId, @Param("excludeCols") List<String> excludeCols) throws Exception;
	//  更新具体的任务明细
	void updTaskDetail(TaskDetail detail) throws Exception;
	// 删除具体的任务明细
	void delTaskDetail(@Param("ids") List<String> ids, @Param("instanceId") String instanceId) throws Exception;
	// 保存具体的任务明细
	void saveTaskDetail(TaskDetail detail) throws Exception;
	// 批量保存具体的任务明细
	void saveTaskDetailBatch(@Param("details") List<TaskDetail> details) throws Exception;
	// 发布任务明细
	void publishTaskDetail(@Param("user") User user, @Param("instanceId") String instanceId) throws Exception;
	// 废弃任务明细
	void abandonTaskDetail(@Param("user") User user, @Param("instanceId") String instanceId, @Param("id") String id) throws Exception;
}
