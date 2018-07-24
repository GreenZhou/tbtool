package com.augurit.awater.service;

import com.augurit.awater.entity.Saler;
import com.augurit.awater.entity.TaskDetail;
import com.augurit.awater.entity.TaskInstance;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ITask {
    TaskInstance getTaskInstance(String id) throws AppException;
    //  创建任务实例
    void saveTaskInstance(TaskInstance instance) throws AppException;
    //  发布任务实例
    void publishTaskInstance(String id, List<TaskDetail> details) throws AppException;
    //  查询所有的自己有权限任务实例
    List<TaskInstance> findTaskInstanceList(String recieverId, String creatorId) throws AppException;
    // 更新任务实例（仅仅在没发布、发布后没被处理才可以执行删除操作）
    void updateTaskInstance(TaskInstance instance) throws AppException;
    // 删除任务实例（仅仅在没发布、发布后没被处理才可以执行删除操作）
    void delTaskInstance(String id) throws AppException;
    // 废弃任务实例（仅仅在没发布、发布后没被处理、发布后各个任务明细都被处理成废弃状态才可以执行该操作）
    void abandonTaskInstance(String id) throws AppException;
    //  指派具体买家
    void assignTaskInstance(String id, List<String> customerIds, int customerNum) throws AppException;
    //  查询具体的任务明细
    List<TaskDetail> findTaskDetailList(User user, String instanceId, List<String> excludeCols) throws AppException;
    //  更新具体的任务明细
    void updTaskDetail(TaskDetail detail) throws AppException;
    // 删除具体的任务明细
    void delTaskDetail(String id, String instanceId) throws AppException;
    // 保存具体的任务明细
    void saveTaskDetail(TaskDetail detail) throws AppException;
    // 发布任务明细
    void publishTaskDetail(User user, String instanceId) throws AppException;
    // 废弃任务明细
    void abandonTaskDetail(User user, String instanceId, String id) throws AppException;
}
