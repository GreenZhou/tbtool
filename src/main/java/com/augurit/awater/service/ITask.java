package com.augurit.awater.service;

import com.augurit.awater.entity.TaskDetail;
import com.augurit.awater.entity.TaskInstance;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;

import java.util.List;

public interface ITask {
    TaskInstance getTaskInstance(String id) throws AppException;
    //  创建任务实例
    void saveTaskInstance(TaskInstance instance) throws AppException;
    //  发布任务实例
    void publishTaskInstance(String id, List<TaskDetail> details) throws AppException;
    //  查询所有的自己有权限任务实例
    List<TaskInstance> findTaskInstanceList(String recieverId, String creatorId, String taskName) throws AppException;
    // 更新任务实例（仅仅在没发布、发布后没被处理才可以执行删除操作）
    void updateTaskInstance(TaskInstance instance) throws AppException;
    // 删除任务实例（仅仅在没发布、发布后没被处理才可以执行删除操作）
    void delTaskInstance(String id) throws AppException;
    // 废弃任务实例（仅仅在没发布、发布后没被处理、发布后各个任务明细都被处理成废弃状态才可以执行该操作）
    void abandonTaskInstance(TaskInstance instance) throws AppException;
    // 指派具体买家
    void assignTaskInstance(String id, List<String> customerIds, int customerNum) throws AppException;
    // 获取任务接收人列表范围
    List<User> getTaskRecievers(String instanceId) throws AppException;
    //  查询具体的任务明细
    List<TaskDetail> findTaskDetailList(User user, String instanceId, String taskDetailName, List<String> excludeCols) throws AppException;
    //  更新具体的任务明细
    void updTaskDetail(TaskDetail detail) throws AppException;
    // 删除具体的任务明细
    void delTaskDetail(List<String> ids, String instanceId) throws AppException;
    // 删除具体的任务明细文件列表
    void delTaskDetailFiles(List<String> fileIds, String detailId) throws AppException;
    // 保存具体的任务明细
    void saveTaskDetail(TaskDetail detail) throws AppException;
    // 批量保存具体的任务明细列表
    void saveTaskDetailBatch(List<TaskDetail> details) throws AppException;
    // 废弃任务明细
    void abandonTaskDetail(User user, String instanceId, String id) throws AppException;
}
