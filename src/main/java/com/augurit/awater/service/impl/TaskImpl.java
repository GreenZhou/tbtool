package com.augurit.awater.service.impl;

import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.dao.TaskMapper;
import com.augurit.awater.entity.TaskDetail;
import com.augurit.awater.entity.TaskInstance;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;
import com.augurit.awater.service.ITask;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskImpl implements ITask {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public TaskInstance getTaskInstance(String id) throws AppException {
        TaskInstance instance;

        try {
            instance = taskMapper.getTaskInstance(id);
        } catch(Exception e) {
            throw new AppException(RespCodeMsgDepository.TASK_NOT_EXISTS, "任务不存在");
        }

        return instance;
    }

    @Override
    public void saveTaskInstance(TaskInstance instance) throws AppException {
        try {
            taskMapper.saveTaskInstance(instance);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.TASK_PUBLISHED_FAILED, "任务发布失败");
        }
    }

    @Override
    public void publishTaskInstance(String id, List<TaskDetail> details) throws AppException {

    }

    @Override
    public List<TaskInstance> findTaskInstanceList(String recieverId, String creatorId) throws AppException {
        try {
            return taskMapper.findTaskInstanceList(recieverId, creatorId);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务查询失败");
        }
    }

    @Override
    public void updateTaskInstance(TaskInstance instance) throws AppException {
        try {
            taskMapper.updateTaskInstance(instance);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务更新失败");
        }
    }

    @Override
    public void delTaskInstance(String id) throws AppException {
        try {
            taskMapper.delTaskInstance(id);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务删除失败");
        }
    }

    @Override
    public void abandonTaskInstance(String id) throws AppException {

    }

    @Override
    public void assignTaskInstance(String id, List<String> customerIds, int customerNum) throws AppException {

    }

    @Override
    public List<TaskDetail> findTaskDetailList(User user, String instanceId, List<String> excludeCols) throws AppException {
        try {
            List<TaskDetail> details = taskMapper.findTaskDetailList(user, instanceId, excludeCols);
            if(CollectionUtils.isNotEmpty(excludeCols)) {
                // TODO 屏蔽掉没权限的属性字段
            }
            return details;
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细列表查询失败");
        }
    }

    @Override
    public void updTaskDetail(TaskDetail detail) throws AppException {
        try {
            taskMapper.updTaskDetail(detail);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细更新除失败");
        }
    }

    @Override
    public void delTaskDetail(List<String> ids, String instanceId) throws AppException {
        try {
            taskMapper.delTaskDetail(ids, instanceId);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细删除失败");
        }
    }

    @Override
    public void saveTaskDetail(TaskDetail detail) throws AppException {
        try {
            taskMapper.saveTaskDetail(detail);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细添加失败");
        }
    }

    @Override
    public void saveTaskDetailBatch(List<TaskDetail> details) throws AppException {
        try {
            taskMapper.saveTaskDetailBatch(details);
        } catch (Exception e) {
            throw new AppException(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, "任务明细导入失败");
        }
    }

    @Override
    public void publishTaskDetail(User user, String instanceId) throws AppException {

    }

    @Override
    public void abandonTaskDetail(User user, String instanceId, String id) throws AppException {

    }
}
