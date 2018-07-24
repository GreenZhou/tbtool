package com.augurit.awater.service.impl;

import com.augurit.awater.entity.TaskDetail;
import com.augurit.awater.entity.TaskInstance;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;
import com.augurit.awater.service.ITask;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskImpl implements ITask {
    @Override
    public TaskInstance getTaskInstance(String id) throws AppException {
        return null;
    }

    @Override
    public void saveTaskInstance(TaskInstance instance) throws AppException {

    }

    @Override
    public void publishTaskInstance(String id, List<TaskDetail> details) throws AppException {

    }

    @Override
    public List<TaskInstance> findTaskInstanceList(String recieverId, String creatorId) throws AppException {
        return null;
    }

    @Override
    public void updateTaskInstance(TaskInstance instance) throws AppException {

    }

    @Override
    public void delTaskInstance(String id) throws AppException {

    }

    @Override
    public void abandonTaskInstance(String id) throws AppException {

    }

    @Override
    public void assignTaskInstance(String id, List<String> customerIds, int customerNum) throws AppException {

    }

    @Override
    public List<TaskDetail> findTaskDetailList(User user, String instanceId, List<String> excludeCols) throws AppException {
        return null;
    }

    @Override
    public void updTaskDetail(TaskDetail detail) throws AppException {

    }

    @Override
    public void delTaskDetail(String id, String instanceId) throws AppException {

    }

    @Override
    public void saveTaskDetail(TaskDetail detail) throws AppException {

    }

    @Override
    public void publishTaskDetail(User user, String instanceId) throws AppException {

    }

    @Override
    public void abandonTaskDetail(User user, String instanceId, String id) throws AppException {

    }
}
