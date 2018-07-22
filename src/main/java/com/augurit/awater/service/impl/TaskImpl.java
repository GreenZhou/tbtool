package com.augurit.awater.service.impl;

import com.augurit.awater.entity.TaskDetail;
import com.augurit.awater.entity.TaskInstance;
import com.augurit.awater.exception.AppException;
import com.augurit.awater.service.ITask;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskImpl implements ITask {
    @Override
    public void saveTaskInstance(TaskInstance instance) throws AppException {

    }

    @Override
    public void publishTaskInstance(String id, List<TaskDetail> details) throws AppException {

    }

    @Override
    public List<TaskInstance> findTaskInstanceList(String userId) throws AppException {
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
    public List<TaskDetail> findTaskDetailList(String userId, String instanceId) throws AppException {
        return null;
    }

    @Override
    public void updTaskDetail(TaskDetail detail) throws AppException {

    }
}
